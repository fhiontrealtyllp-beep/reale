<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$idToken = is_string($body['idToken'] ?? null) ? trim($body['idToken']) : '';
if ($idToken === '') {
    respond(422, false, 'Google ID token is required');
}

$clientId = (string) ($config['google']['web_client_id'] ?? '');
if ($clientId === '') {
    error_log('Google sign-in is not configured (google.web_client_id missing)');
    respond(500, false, 'Google sign-in is not configured');
}

$request = curl_init('https://oauth2.googleapis.com/tokeninfo?id_token=' . rawurlencode($idToken));
curl_setopt_array($request, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_TIMEOUT => 15,
]);
$response = curl_exec($request);
$status = (int) curl_getinfo($request, CURLINFO_HTTP_CODE);
curl_close($request);

$claims = is_string($response) ? json_decode($response, true) : null;
if ($status !== 200 || !is_array($claims)) {
    respond(401, false, 'Google sign-in failed');
}

$audience = (string) ($claims['aud'] ?? '');
$issuer = (string) ($claims['iss'] ?? '');
$expiresAt = (int) ($claims['exp'] ?? 0);
$email = normalizedEmail($claims['email'] ?? null);
$emailVerified = filter_var($claims['email_verified'] ?? false, FILTER_VALIDATE_BOOLEAN);

if (
    $audience !== $clientId
    || !in_array($issuer, ['accounts.google.com', 'https://accounts.google.com'], true)
    || $expiresAt <= time()
    || $email === ''
    || !$emailVerified
) {
    respond(401, false, 'Google sign-in failed');
}

$name = trim(is_string($claims['name'] ?? null) ? $claims['name'] : '');
if (mb_strlen($name) < 2) {
    $name = strstr($email, '@', true) ?: 'User';
}
$name = mb_substr($name, 0, 120);
$picture = trim(is_string($claims['picture'] ?? null) ? $claims['picture'] : '');

$pdo = database();
$statement = $pdo->prepare('SELECT * FROM users WHERE email = :email LIMIT 1');
$statement->execute(['email' => $email]);
$user = $statement->fetch();

if (!$user) {
    $pdo->beginTransaction();
    try {
        $insert = $pdo->prepare(
            'INSERT INTO users (name, email, password_hash, address, image, password_set) '
            . 'VALUES (:name, :email, :password_hash, :address, :image, 0)'
        );
        $insert->execute([
            'name' => $name,
            'email' => $email,
            'password_hash' => password_hash(bin2hex(random_bytes(24)), PASSWORD_DEFAULT),
            'address' => '',
            'image' => $picture !== '' ? $picture : null,
        ]);
        $userId = (int) $pdo->lastInsertId();
        $token = issueToken($pdo, $userId);
        $userStatement = $pdo->prepare('SELECT * FROM users WHERE id = :id LIMIT 1');
        $userStatement->execute(['id' => $userId]);
        $user = $userStatement->fetch();
        $pdo->commit();
        respond(200, true, 'Login successful', ['user' => publicUser($user, $token)]);
    } catch (Throwable $exception) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        throw $exception;
    }
}

if ((string) $user['status'] !== 'active') {
    respond(403, false, 'This account is not active');
}

$updates = [];
$params = ['id' => (int) $user['id']];
if ((string) $user['name'] === '' && $name !== '') {
    $updates[] = 'name = :name';
    $params['name'] = $name;
}
if ($user['image'] === null && $picture !== '') {
    $updates[] = 'image = :image';
    $params['image'] = $picture;
}
if ($updates !== []) {
    $update = $pdo->prepare('UPDATE users SET ' . implode(', ', $updates) . ' WHERE id = :id');
    $update->execute($params);
    $userStatement = $pdo->prepare('SELECT * FROM users WHERE id = :id LIMIT 1');
    $userStatement->execute(['id' => (int) $user['id']]);
    $user = $userStatement->fetch();
}

$token = issueToken($pdo, (int) $user['id']);
respond(200, true, 'Login successful', ['user' => publicUser($user, $token)]);
