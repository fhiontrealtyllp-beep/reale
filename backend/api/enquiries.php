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
} elseif ($type === 'owner') {
    $propertyStatement = $pdo->prepare('SELECT id FROM properties WHERE user_id = :owner_id');
    $propertyStatement->execute(['owner_id' => $user['id']]);
    $propertyIds = array_map('strval', array_column($propertyStatement->fetchAll(), 'id'));

    if (empty($propertyIds)) {
        respond(200, true, 'Enquiries loaded', ['enquiries' => []]);
    }

    $placeholders = implode(',', array_fill(0, count($propertyIds), '?'));
    $statement = $pdo->prepare(
        'SELECT e.*, u.name AS user_name FROM enquiries e '
        . 'LEFT JOIN users u ON e.user_id = u.id '
        . "WHERE e.property_id IN ($placeholders) "
        . 'ORDER BY e.created_at DESC'
    );
    $statement->execute($propertyIds);
} else {
    respond(400, false, 'Invalid type parameter');
}

$rows = $statement->fetchAll();

if (empty($rows)) {
    respond(200, true, 'Enquiries loaded', ['enquiries' => []]);
}

$enquiryIds = array_map('intval', array_column($rows, 'id'));

$enquiryIdPlaceholders = implode(',', array_fill(0, count($enquiryIds), '?'));
$messagesStatement = $pdo->prepare(
    'SELECT enquiry_id, message, created_at '
    . "FROM enquiry_messages WHERE enquiry_id IN ($enquiryIdPlaceholders) "
    . 'ORDER BY created_at ASC, id ASC'
);
$messagesStatement->execute($enquiryIds);
$messagesByEnquiry = [];
foreach ($messagesStatement->fetchAll() as $messageRow) {
    $eid = (int) $messageRow['enquiry_id'];
    $messagesByEnquiry[$eid][] = $messageRow;
}

$enquiries = array_map(function (array $row) use ($messagesByEnquiry): array {
    $enquiryId = (int) $row['id'];

    $latestMessage = (string) $row['message'];
    $latestAt = (string) $row['created_at'];
    $messages = $messagesByEnquiry[$enquiryId] ?? [];

    $messageTimestamps = [(string) $row['created_at']];
    foreach ($messages as $messageRow) {
        $messageTimestamps[] = (string) $messageRow['created_at'];
        if ((string) $messageRow['created_at'] > $latestAt) {
            $latestMessage = (string) $messageRow['message'];
            $latestAt = (string) $messageRow['created_at'];
        }
    }
    sort($messageTimestamps);

    return [
        'id' => (string) $row['id'],
        'propertyId' => (string) $row['property_id'],
        'propertyTitle' => (string) $row['property_title'],
        'propertyLocation' => (string) $row['property_location'],
        'propertyImage' => (string) $row['property_image'],
        'agentPhone' => (string) $row['agent_phone'],
        'message' => $latestMessage,
        'userId' => $row['user_id'] !== null ? (string) $row['user_id'] : null,
        'userName' => (string) ($row['user_name'] ?? ''),
        'status' => (string) $row['status'],
        'createdAt' => $latestAt,
        'messageTimestamps' => $messageTimestamps,
    ];
}, $rows);
respond(200, true, 'Enquiries loaded', ['enquiries' => $enquiries]);
