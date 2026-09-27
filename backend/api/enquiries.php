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
$propertyIds = array_values(array_unique(array_map('intval', array_column($rows, 'property_id'))));

$enquiryIdPlaceholders = implode(',', array_fill(0, count($enquiryIds), '?'));
$messagesStatement = $pdo->prepare(
    'SELECT id, enquiry_id, sender_id, message, created_at, read_by_owner, read_by_user '
    . "FROM enquiry_messages WHERE enquiry_id IN ($enquiryIdPlaceholders) "
    . 'ORDER BY created_at ASC, id ASC'
);
$messagesStatement->execute($enquiryIds);
$messagesByEnquiry = [];
foreach ($messagesStatement->fetchAll() as $messageRow) {
    $eid = (int) $messageRow['enquiry_id'];
    $messagesByEnquiry[$eid][] = $messageRow;
}

$propertyIdPlaceholders = implode(',', array_fill(0, count($propertyIds), '?'));
$ownersStatement = $pdo->prepare("SELECT id, user_id FROM properties WHERE id IN ($propertyIdPlaceholders)");
$ownersStatement->execute($propertyIds);
$ownersByProperty = [];
foreach ($ownersStatement->fetchAll() as $propertyRow) {
    $ownersByProperty[(int) $propertyRow['id']] = (int) $propertyRow['user_id'];
}

$enquiries = array_map(function (array $row) use ($user, $messagesByEnquiry, $ownersByProperty): array {
    $enquiryId = (int) $row['id'];
    $propertyId = (int) $row['property_id'];
    $ownerId = $ownersByProperty[$propertyId] ?? 0;
    $viewerIsOwner = (int) $user['id'] === $ownerId;

    $latestMessage = (string) $row['message'];
    $latestAt = (string) $row['created_at'];
    $messages = $messagesByEnquiry[$enquiryId] ?? [];

    foreach ($messages as $messageRow) {
        if ((string) $messageRow['created_at'] > $latestAt) {
            $latestMessage = (string) $messageRow['message'];
            $latestAt = (string) $messageRow['created_at'];
        }
    }

    $unreadCount = 0;
    if ($viewerIsOwner) {
        if ((int) $row['read_by_owner'] === 0) {
            $unreadCount++;
        }
        foreach ($messages as $messageRow) {
            if ((int) $messageRow['sender_id'] !== (int) $user['id'] && (int) $messageRow['read_by_owner'] === 0) {
                $unreadCount++;
            }
        }
    } else {
        if ((int) $row['read_by_user'] === 0) {
            $unreadCount++;
        }
        foreach ($messages as $messageRow) {
            if ((int) $messageRow['sender_id'] !== (int) $user['id'] && (int) $messageRow['read_by_user'] === 0) {
                $unreadCount++;
            }
        }
    }

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
        'unreadCount' => $unreadCount,
    ];
}, $rows);
respond(200, true, 'Enquiries loaded', ['enquiries' => $enquiries]);
