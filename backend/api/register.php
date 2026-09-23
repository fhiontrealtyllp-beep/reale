<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$name = trim(is_string($body['name'] ?? null) ? $body['name'] : '');
$email = normalizedEmail($body['email'] ?? null);
$password = is_string($body['password'] ?? null) ? $body['password'] : '';

if ($name === '' || mb_strlen($name) < 2 || mb_strlen($name) > 120) {
    respond(422, false, 'Name must be between 2 and 120 characters');
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 190) {
    respond(422, false, 'A valid email is required');
}
if (strlen($password) < 8 || strlen($password) > 72) {
    respond(422, false, 'Password must be between 8 and 72 characters');
}

$pdo = database();
$existing = $pdo->prepare('SELECT id FROM users WHERE email = :email LIMIT 1');
$existing->execute(['email' => $email]);
if ($existing->fetch()) {
    respond(409, false, 'An account already exists for this email');
}

$pdo->beginTransaction();
try {
    $statement = $pdo->prepare(
        'INSERT INTO users (name, email, password_hash, address) VALUES (:name, :email, :password_hash, :address)'
    );
    $statement->execute([
        'name' => $name,
        'email' => $email,
        'password_hash' => password_hash($password, PASSWORD_DEFAULT),
        'address' => '',
    ]);
    $userId = (int) $pdo->lastInsertId();
    $token = issueToken($pdo, $userId);
    $userStatement = $pdo->prepare('SELECT * FROM users WHERE id = :id LIMIT 1');
    $userStatement->execute(['id' => $userId]);
    $user = $userStatement->fetch();
    $pdo->commit();
    respond(201, true, 'Registration successful', ['user' => publicUser($user, $token)]);
} catch (Throwable $exception) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    throw $exception;
}
