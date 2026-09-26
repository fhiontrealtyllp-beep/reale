<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$pdo = database();
$user = authenticatedUser($pdo);
$body = jsonBody();
$token = is_string($body['token'] ?? null) ? trim($body['token']) : '';
$platform = is_string($body['platform'] ?? null) ? trim($body['platform']) : 'android';

if ($token === '') {
    respond(422, false, 'token is required');
}

$statement = $pdo->prepare(
    'INSERT INTO device_tokens (user_id, token, platform) VALUES (:user_id, :token, :platform) '
    . 'ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), platform = VALUES(platform), updated_at = CURRENT_TIMESTAMP'
);
$statement->execute([
    'user_id' => $user['id'],
    'token' => $token,
    'platform' => $platform,
]);

respond(200, true, 'Device token registered');
