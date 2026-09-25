<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$pdo = database();
$user = authenticatedUser($pdo);
$body = jsonBody();

$propertyId = filter_var($body['propertyId'] ?? null, FILTER_VALIDATE_INT);
if ($propertyId === false || $propertyId === null || $propertyId <= 0) {
    respond(422, false, 'A valid propertyId is required');
}

$statement = $pdo->prepare(
    'SELECT id, user_id, images FROM properties WHERE id = :id LIMIT 1'
);
$statement->execute(['id' => $propertyId]);
$property = $statement->fetch();

if (!$property || (int) $property['user_id'] !== (int) $user['id']) {
    respond(404, false, 'Property not found');
}

$imageUrls = json_decode((string) ($property['images'] ?? ''), true);
if (!is_array($imageUrls)) {
    $imageUrls = [];
}

$deleteImages = $pdo->prepare('DELETE FROM likes WHERE property_id = :property_id');
$deleteImages->execute(['property_id' => (string) $propertyId]);

$deleteEnquiries = $pdo->prepare('DELETE FROM enquiries WHERE property_id = :property_id');
$deleteEnquiries->execute(['property_id' => (string) $propertyId]);

$deleteProperty = $pdo->prepare(
    'DELETE FROM properties WHERE id = :id AND user_id = :user_id'
);
$deleteProperty->execute([
    'id' => $propertyId,
    'user_id' => $user['id'],
]);

$uploadsRoot = dirname(__DIR__) . '/uploads';
$deletedDirs = [];
foreach ($imageUrls as $url) {
    if (!is_string($url) || $url === '') {
        continue;
    }
    $path = parse_url($url, PHP_URL_PATH);
    if (!is_string($path)) {
        continue;
    }
    $marker = strpos($path, '/uploads/');
    if ($marker === false) {
        continue;
    }
    $relative = substr($path, $marker + strlen('/uploads/'));
    if ($relative === '' || str_contains($relative, '..')) {
        continue;
    }
    $filePath = $uploadsRoot . '/' . $relative;
    $real = realpath($filePath);
    if ($real === false || !str_starts_with($real, realpath($uploadsRoot) . DIRECTORY_SEPARATOR)) {
        continue;
    }
    if (is_file($real)) {
        @unlink($real);
        $deletedDirs[dirname($real)] = true;
    }
}

// Remove now-empty upload group directories (and the user dir if empty).
foreach (array_keys($deletedDirs) as $dir) {
    @rmdir($dir);
    $parent = dirname($dir);
    if (is_dir($parent)) {
        @rmdir($parent);
    }
}

respond(200, true, 'Property deleted successfully');
