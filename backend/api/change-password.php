<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$token = bearerToken();
$pdo = database();
$user = authenticatedUser($pdo);

$body = jsonBody();
$currentPassword = is_string($body['currentPassword'] ?? null) ? $body['currentPassword'] : '';
$newPassword = is_string($body['newPassword'] ?? null) ? $body['newPassword'] : '';

if (strlen($newPassword) < 8 || strlen($newPassword) > 72) {
    respond(422, false, 'New password must be between 8 and 72 characters');
}

$hasPassword = (int) ($user['password_set'] ?? 1) === 1;
if ($hasPassword && !password_verify($currentPassword, (string) $user['password_hash'])) {
    respond(401, false, 'Current password is incorrect');
}

$pdo->beginTransaction();
try {
    $update = $pdo->prepare(
        'UPDATE users SET password_hash = :password_hash, password_set = 1 WHERE id = :id'
    );
    $update->execute([
        'password_hash' => password_hash($newPassword, PASSWORD_DEFAULT),
        'id' => (int) $user['id'],
    ]);

    // Revoke every other session so a leaked/lost device loses access after a password change.
    $revoke = $pdo->prepare(
        'DELETE FROM auth_tokens WHERE user_id = :user_id AND token_hash != :token_hash'
    );
    $revoke->execute([
        'user_id' => (int) $user['id'],
        'token_hash' => hash('sha256', $token),
    ]);

    $pdo->commit();
    respond(200, true, 'Password updated successfully');
} catch (Throwable $exception) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    throw $exception;
}
