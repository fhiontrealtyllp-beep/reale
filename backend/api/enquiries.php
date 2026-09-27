<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('GET');

$pdo = database();
$user = authenticatedUser($pdo);

$type = $_GET['type'] ?? '';

if ($type === 'user') {
    $statement = $pdo->prepare(
        'SELECT e.*, u.name AS user_name FROM enquiries e '
        . 'LEFT JOIN users u ON e.user_id = u.id '
        . 'WHERE e.user_id = :user_id ORDER BY e.created_at DESC'
    );
    $statement->execute(['user_id' => $user['id']]);
} elseif ($type === 'property') {
    $propertyId = is_string($_GET['propertyId'] ?? null) ? trim($_GET['propertyId']) : '';
    if ($propertyId === '') {
        respond(422, false, 'propertyId is required');
    }

    $statement = $pdo->prepare(
        'SELECT e.*, u.name AS user_name FROM enquiries e '
        . 'LEFT JOIN users u ON e.user_id = u.id '
        . 'WHERE e.property_id = :property_id ORDER BY e.created_at DESC'
    );
    $statement->execute(['property_id' => $propertyId]);
} else {
    respond(400, false, 'Invalid type parameter');
}

$rows = $statement->fetchAll();

$enquiries = array_map(function (array $row): array {
    return [
        'id' => (string) $row['id'],
        'propertyId' => (string) $row['property_id'],
        'propertyTitle' => (string) $row['property_title'],
        'propertyLocation' => (string) $row['property_location'],
        'propertyImage' => (string) $row['property_image'],
        'agentPhone' => (string) $row['agent_phone'],
        'message' => (string) $row['message'],
        'userId' => $row['user_id'] !== null ? (string) $row['user_id'] : null,
        'userName' => (string) ($row['user_name'] ?? ''),
        'status' => (string) $row['status'],
        'createdAt' => (string) $row['created_at'],
    ];
}, $rows);
respond(200, true, 'Enquiries loaded', ['enquiries' => $enquiries]);
