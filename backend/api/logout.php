<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$token = bearerToken();
$pdo = database();
$statement = $pdo->prepare('DELETE FROM auth_tokens WHERE token_hash = :token_hash');
$statement->execute(['token_hash' => hash('sha256', $token)]);
respond(200, true, 'Logout successful');
