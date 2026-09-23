<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$email = normalizedEmail($body['email'] ?? null);
$password = is_string($body['password'] ?? null) ? $body['password'] : '';

if (!filter_var($email, FILTER_VALIDATE_EMAIL) || $password === '') {
    respond(422, false, 'Email and password are required');
}

$pdo = database();
$statement = $pdo->prepare('SELECT * FROM users WHERE email = :email LIMIT 1');
$statement->execute(['email' => $email]);
$user = $statement->fetch();

if (!$user || !password_verify($password, (string) $user['password_hash'])) {
    respond(401, false, 'Invalid email or password');
}
if ((string) $user['status'] !== 'active') {
    respond(403, false, 'This account is not active');
}

$token = issueToken($pdo, (int) $user['id']);
respond(200, true, 'Login successful', ['user' => publicUser($user, $token)]);
