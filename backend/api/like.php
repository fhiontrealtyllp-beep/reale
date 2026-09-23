<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$propertyId = is_string($body['propertyId'] ?? null) ? trim($body['propertyId']) : '';
if ($propertyId === '') {
    respond(422, false, 'propertyId is required');
}

$isLiked = filter_var($body['isLiked'] ?? false, FILTER_VALIDATE_BOOLEAN);

$pdo = database();
$user = authenticatedUser($pdo);

if ($isLiked) {
    $statement = $pdo->prepare(
        'INSERT IGNORE INTO likes (user_id, property_id) VALUES (:user_id, :property_id)'
    );
    $statement->execute([
        'user_id' => $user['id'],
        'property_id' => $propertyId,
    ]);
} else {
    $statement = $pdo->prepare(
        'DELETE FROM likes WHERE user_id = :user_id AND property_id = :property_id'
    );
    $statement->execute([
        'user_id' => $user['id'],
        'property_id' => $propertyId,
    ]);
}

respond(200, true, 'Like updated successfully');
