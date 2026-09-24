<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();

$base64 = is_string($body['data'] ?? null) ? trim($body['data']) : '';
if ($base64 === '') {
    respond(422, false, 'Image data is required');
}

$filename = is_string($body['filename'] ?? null) ? trim($body['filename']) : '';
if ($filename === '') {
    respond(422, false, 'Filename is required');
}

$mimeType = is_string($body['mimeType'] ?? null) ? strtolower(trim($body['mimeType'])) : '';

if (!preg_match('/^image\/[a-z0-9+.-]+$/i', $mimeType)) {
    respond(422, false, 'Unsupported image type');
}

$ext = 'jpg';
if ($mimeType === 'image/png') {
    $ext = 'png';
} elseif ($mimeType === 'image/webp') {
    $ext = 'webp';
} elseif ($mimeType === 'image/gif') {
    $ext = 'gif';
}

$pdo = database();
$user = authenticatedUser($pdo);

$uploadDir = dirname(__DIR__) . '/uploads/properties/' . $user['id'];
if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
    respond(500, false, 'Failed to create upload directory');
}

$safeName = bin2hex(random_bytes(8)) . '_' . preg_replace('/[^a-zA-Z0-9_.-]/', '_', $filename);
$safeName = preg_replace('/\.[^.]+$/', '', $safeName) . '.' . $ext;
$targetPath = $uploadDir . '/' . $safeName;

$decoded = base64_decode($base64, true);
if ($decoded === false || $decoded === '') {
    respond(422, false, 'Invalid image data');
}

$imageInfo = getimagesizefromstring($decoded);
if ($imageInfo === false) {
    respond(422, false, 'Invalid image content');
}

if (file_put_contents($targetPath, $decoded) === false) {
    respond(500, false, 'Failed to save image');
}

$forwardedProto = $_SERVER['HTTP_X_FORWARDED_PROTO'] ?? '';
$scheme = $forwardedProto !== ''
    ? trim(explode(',', $forwardedProto)[0])
    : ($_SERVER['REQUEST_SCHEME'] ?? 'https');
$host = $_SERVER['HTTP_HOST'] ?? 'localhost';
$scriptName = str_replace('\\', '/', $_SERVER['SCRIPT_NAME'] ?? '/api/upload-image.php');
$basePath = rtrim(dirname(dirname($scriptName)), '/');
$url = $scheme . '://' . $host . $basePath . '/uploads/properties/' . $user['id'] . '/' . $safeName;

respond(201, true, 'Image uploaded successfully', ['url' => $url]);
