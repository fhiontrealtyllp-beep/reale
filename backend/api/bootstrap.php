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
