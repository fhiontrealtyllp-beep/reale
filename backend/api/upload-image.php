<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

const MAX_PROPERTY_PHOTOS = 5;
const MAX_IMAGE_BYTES = 12 * 1024 * 1024;
const MAX_IMAGE_PIXELS = 40_000_000;
const RESIZE_LONG_EDGE = 2560;
const JPEG_QUALITY = 94;
const WEBP_QUALITY = 92;

$pdo = database();
$user = authenticatedUser($pdo);
$body = jsonBody();

$base64 = is_string($body['data'] ?? null) ? preg_replace('/\s+/', '', $body['data']) : '';
if (!is_string($base64) || $base64 === '') {
    respond(422, false, 'Image data is required');
}
if (strlen($base64) > (int) ceil(MAX_IMAGE_BYTES * 4 / 3) + 4) {
    respond(413, false, 'Image must be 12 MB or smaller');
}

$decoded = base64_decode($base64, true);
if ($decoded === false || $decoded === '') {
    respond(422, false, 'Invalid image data');
}
if (strlen($decoded) > MAX_IMAGE_BYTES) {
    respond(413, false, 'Image must be 12 MB or smaller');
}

$imageInfo = @getimagesizefromstring($decoded);
$finfo = new finfo(FILEINFO_MIME_TYPE);
$detectedMime = $finfo->buffer($decoded);
$supportedTypes = [
    IMAGETYPE_JPEG => ['mime' => 'image/jpeg', 'extension' => 'jpg'],
    IMAGETYPE_PNG => ['mime' => 'image/png', 'extension' => 'png'],
    IMAGETYPE_WEBP => ['mime' => 'image/webp', 'extension' => 'webp'],
];
$imageType = is_array($imageInfo) ? ($imageInfo[2] ?? null) : null;
if (!is_int($imageType) || !isset($supportedTypes[$imageType])) {
    respond(422, false, 'Only valid JPEG, PNG, and WebP images are supported');
}
$type = $supportedTypes[$imageType];
if ($detectedMime !== $type['mime']) {
    respond(422, false, 'Image content does not match a supported format');
}

$width = (int) ($imageInfo[0] ?? 0);
$height = (int) ($imageInfo[1] ?? 0);
$pixels = $width * $height;
if ($width < 1 || $height < 1 || $pixels > MAX_IMAGE_PIXELS) {
    respond(422, false, 'Image dimensions are invalid or too large');
}

$uploadGroupId = is_string($body['uploadGroupId'] ?? null)
    ? strtolower(trim($body['uploadGroupId']))
    : 'legacy';
if (!preg_match('/^[a-z0-9_-]{1,64}$/', $uploadGroupId)) {
    respond(422, false, 'Invalid upload group');
}

$uploadDir = dirname(__DIR__) . '/uploads/properties/' . $user['id'] . '/' . $uploadGroupId;
if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true) && !is_dir($uploadDir)) {
    respond(500, false, 'Failed to create upload directory');
}
$existingFiles = glob($uploadDir . '/*.{jpg,jpeg,png,webp}', GLOB_BRACE);
if (is_array($existingFiles) && count($existingFiles) >= MAX_PROPERTY_PHOTOS) {
    respond(409, false, 'A property can have a maximum of 5 photos');
}

$output = $decoded;
$outputType = $imageType;
if (max($width, $height) > RESIZE_LONG_EDGE) {
    if (!extension_loaded('gd')) {
        respond(422, false, 'This image is too large to process');
    }
    $source = @imagecreatefromstring($decoded);
    if ($source === false) {
        respond(422, false, 'Image is corrupted or cannot be decoded');
    }
    $scale = RESIZE_LONG_EDGE / max($width, $height);
    $targetWidth = max(1, (int) round($width * $scale));
    $targetHeight = max(1, (int) round($height * $scale));
    $target = imagecreatetruecolor($targetWidth, $targetHeight);
    if ($target === false) {
        imagedestroy($source);
        respond(500, false, 'Failed to process image');
    }
    if ($imageType === IMAGETYPE_PNG || $imageType === IMAGETYPE_WEBP) {
        imagealphablending($target, false);
        imagesavealpha($target, true);
        $transparent = imagecolorallocatealpha($target, 0, 0, 0, 127);
        imagefilledrectangle($target, 0, 0, $targetWidth, $targetHeight, $transparent);
    }
    if (!imagecopyresampled($target, $source, 0, 0, 0, 0, $targetWidth, $targetHeight, $width, $height)) {
        imagedestroy($target);
        imagedestroy($source);
        respond(500, false, 'Failed to resize image');
    }
    ob_start();
    $encoded = match ($imageType) {
        IMAGETYPE_JPEG => imagejpeg($target, null, JPEG_QUALITY),
        IMAGETYPE_PNG => imagepng($target, null, 6),
        IMAGETYPE_WEBP => function_exists('imagewebp') && imagewebp($target, null, WEBP_QUALITY),
        default => false,
    };
    $processed = ob_get_clean();
    imagedestroy($target);
    imagedestroy($source);
    if (!$encoded || !is_string($processed) || $processed === '') {
        respond(500, false, 'Failed to optimize image');
    }
    $output = $processed;
}

$safeName = bin2hex(random_bytes(16)) . '.' . $supportedTypes[$outputType]['extension'];
$targetPath = $uploadDir . '/' . $safeName;
if (file_put_contents($targetPath, $output, LOCK_EX) === false) {
    respond(500, false, 'Failed to save image');
}
@chmod($targetPath, 0644);

$forwardedProto = $_SERVER['HTTP_X_FORWARDED_PROTO'] ?? '';
$scheme = $forwardedProto !== ''
    ? trim(explode(',', $forwardedProto)[0])
    : ($_SERVER['REQUEST_SCHEME'] ?? 'https');
$host = $_SERVER['HTTP_HOST'] ?? 'localhost';
$scriptName = str_replace('\\', '/', $_SERVER['SCRIPT_NAME'] ?? '/api/upload-image.php');
$basePath = rtrim(dirname(dirname($scriptName)), '/');
$url = $scheme . '://' . $host . $basePath . '/uploads/properties/' . $user['id'] . '/' . $uploadGroupId . '/' . $safeName;

respond(201, true, 'Image uploaded successfully', ['url' => $url]);
