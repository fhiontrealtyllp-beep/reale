<?php
declare(strict_types=1);

ini_set('log_errors', '1');
ini_set('error_log', __DIR__ . '/error_log');
error_reporting(E_ALL);

const JSON_FLAGS = JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE;

$configFile = dirname(__DIR__) . '/config.php';
if (!is_file($configFile)) {
    respond(500, false, 'Server configuration is missing');
}
$config = require $configFile;

$origin = $_SERVER['HTTP_ORIGIN'] ?? '';
$allowedOrigins = $config['cors']['allowed_origins'] ?? [];
if (in_array('*', $allowedOrigins, true)) {
    header('Access-Control-Allow-Origin: *');
} elseif ($origin !== '' && in_array($origin, $allowedOrigins, true)) {
    header('Access-Control-Allow-Origin: ' . $origin);
    header('Vary: Origin');
}
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Headers: Authorization, Content-Type');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Cache-Control: no-store');

if (($_SERVER['REQUEST_METHOD'] ?? '') === 'OPTIONS') {
    http_response_code(204);
    exit;
}

set_exception_handler(static function (Throwable $exception) use ($config): void {
    error_log((string) $exception);
    $debug = (bool) ($config['app']['debug'] ?? false);
    $message = $debug
        ? $exception::class . ': ' . $exception->getMessage()
        : 'Internal server error';
    respond(500, false, $message);
});

function database(): PDO
{
    global $config;
    static $pdo = null;
    if ($pdo instanceof PDO) {
        return $pdo;
    }
    $db = $config['database'];
    $dsn = sprintf(
        'mysql:host=%s;port=%d;dbname=%s;charset=%s',
        $db['host'],
        $db['port'],
        $db['name'],
        $db['charset']
    );
    $pdo = new PDO($dsn, $db['user'], $db['password'], [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]);
    return $pdo;
}

function respond(int $status, bool $success, string $message, array $data = []): never
{
    http_response_code($status);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data,
    ], JSON_FLAGS);
    exit;
}

function requireMethod(string $method): void
{
    if (($_SERVER['REQUEST_METHOD'] ?? '') !== $method) {
        header('Allow: ' . $method);
        respond(405, false, 'Method not allowed');
    }
}

function jsonBody(): array
{
    $raw = file_get_contents('php://input');
    if ($raw === false || trim($raw) === '') {
        respond(400, false, 'Request body is required');
    }
    try {
        $body = json_decode($raw, true, 512, JSON_THROW_ON_ERROR);
    } catch (JsonException) {
        respond(400, false, 'Invalid JSON body');
    }
    if (!is_array($body)) {
        respond(400, false, 'Invalid request body');
    }
    return $body;
}

function normalizedEmail(mixed $value): string
{
    return strtolower(trim(is_string($value) ? $value : ''));
}

function publicUser(array $user, string $token = ''): array
{
    return [
        'id' => (string) $user['id'],
        'name' => (string) $user['name'],
        'email' => (string) $user['email'],
        'phone' => (string) $user['phone'],
        'status' => (string) $user['status'],
        'city' => (string) $user['city'],
        'location' => (string) $user['location'],
        'address' => (string) $user['address'],
        'image' => $user['image'] === null ? null : (string) $user['image'],
        'hasPassword' => (int) ($user['password_set'] ?? 1) === 1,
        'sessionId' => $token,
    ];
}

function issueToken(PDO $pdo, int $userId): string
{
    global $config;
    $token = bin2hex(random_bytes(32));
    $tokenHash = hash('sha256', $token);
    $days = max(1, (int) ($config['auth']['token_ttl_days'] ?? 30));
    $expiresAt = (new DateTimeImmutable('now', new DateTimeZone('UTC')))
        ->modify('+' . $days . ' days')
        ->format('Y-m-d H:i:s');
    $statement = $pdo->prepare(
        'INSERT INTO auth_tokens (user_id, token_hash, expires_at) VALUES (:user_id, :token_hash, :expires_at)'
    );
    $statement->execute([
        'user_id' => $userId,
        'token_hash' => $tokenHash,
        'expires_at' => $expiresAt,
    ]);
    return $token;
}

function bearerToken(): string
{
    $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if (!preg_match('/^Bearer\s+(.+)$/i', $header, $matches)) {
        respond(401, false, 'Authentication required');
    }
    return trim($matches[1]);
}

function authenticatedUser(PDO $pdo): array
{
    $token = bearerToken();
    $statement = $pdo->prepare(
        'SELECT users.* FROM auth_tokens JOIN users ON users.id = auth_tokens.user_id '
        . 'WHERE auth_tokens.token_hash = :token_hash AND auth_tokens.expires_at > UTC_TIMESTAMP() LIMIT 1'
    );
    $statement->execute(['token_hash' => hash('sha256', $token)]);
    $user = $statement->fetch();
    if (!$user) {
        respond(401, false, 'Session is invalid or expired');
    }
    return $user;
}

function base64UrlEncode(string $value): string
{
    return rtrim(strtr(base64_encode($value), '+/', '-_'), '=');
}

function firebaseAccessToken(): ?string
{
    global $config;
    $firebase = $config['firebase'] ?? [];
    $clientEmail = (string) ($firebase['client_email'] ?? '');
    $privateKey = (string) ($firebase['private_key'] ?? '');
    if ($clientEmail === '' || $privateKey === '') {
        error_log('Firebase service account is not configured');
        return null;
    }
    $now = time();
    $header = base64UrlEncode(json_encode(['alg' => 'RS256', 'typ' => 'JWT'], JSON_FLAGS));
    $claims = base64UrlEncode(json_encode([
        'iss' => $clientEmail,
        'scope' => 'https://www.googleapis.com/auth/firebase.messaging',
        'aud' => 'https://oauth2.googleapis.com/token',
        'iat' => $now,
        'exp' => $now + 3600,
    ], JSON_FLAGS));
    $unsignedToken = $header . '.' . $claims;
    $signature = '';
    if (!openssl_sign($unsignedToken, $signature, $privateKey, OPENSSL_ALGO_SHA256)) {
        error_log('Unable to sign Firebase access token');
        return null;
    }
    $request = curl_init('https://oauth2.googleapis.com/token');
    curl_setopt_array($request, [
        CURLOPT_POST => true,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_HTTPHEADER => ['Content-Type: application/x-www-form-urlencoded'],
        CURLOPT_POSTFIELDS => http_build_query([
            'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
            'assertion' => $unsignedToken . '.' . base64UrlEncode($signature),
        ]),
        CURLOPT_TIMEOUT => 15,
    ]);
    $response = curl_exec($request);
    $status = (int) curl_getinfo($request, CURLINFO_HTTP_CODE);
    curl_close($request);
    $payload = is_string($response) ? json_decode($response, true) : null;
    if ($status !== 200 || !is_array($payload) || !is_string($payload['access_token'] ?? null)) {
        error_log('Unable to obtain Firebase access token: HTTP ' . $status);
        return null;
    }
    return $payload['access_token'];
}

function sendEnquiryPush(PDO $pdo, int $ownerId, int $enquiryId, string $propertyId, string $propertyTitle, string $message): void
{
    sendPushToUser($pdo, $ownerId, 'New enquiry: ' . $propertyTitle, $message, [
        'propertyId' => $propertyId,
        'enquiryId' => (string) $enquiryId,
        'message' => $message,
    ]);
}

function sendPushToUser(PDO $pdo, int $userId, string $title, string $body, array $data): void
{
    global $config;
    $projectId = (string) ($config['firebase']['project_id'] ?? '');
    $accessToken = firebaseAccessToken();
    if ($projectId === '' || $accessToken === null) {
        return;
    }
    $statement = $pdo->prepare('SELECT token FROM device_tokens WHERE user_id = :user_id');
    $statement->execute(['user_id' => $userId]);
    $tokens = $statement->fetchAll(PDO::FETCH_COLUMN);
    foreach ($tokens as $token) {
        $payload = json_encode([
            'message' => [
                'token' => (string) $token,
                'notification' => [
                    'title' => $title,
                    'body' => $body,
                ],
                'data' => array_map('strval', $data),
                'android' => [
                    'priority' => 'high',
                    'notification' => ['channel_id' => 'enquiries'],
                ],
            ],
        ], JSON_FLAGS);
        $request = curl_init('https://fcm.googleapis.com/v1/projects/' . rawurlencode($projectId) . '/messages:send');
        curl_setopt_array($request, [
            CURLOPT_POST => true,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_HTTPHEADER => [
                'Authorization: Bearer ' . $accessToken,
                'Content-Type: application/json; charset=utf-8',
            ],
            CURLOPT_POSTFIELDS => $payload,
            CURLOPT_TIMEOUT => 15,
        ]);
        $response = curl_exec($request);
        $status = (int) curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($status >= 400) {
            error_log('Firebase message failed: HTTP ' . $status . ' ' . (is_string($response) ? $response : ''));
            if ($status === 404) {
                $delete = $pdo->prepare('DELETE FROM device_tokens WHERE token = :token');
                $delete->execute(['token' => $token]);
            }
        }
    }
}
