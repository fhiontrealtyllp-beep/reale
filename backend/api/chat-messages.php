<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';

$pdo = database();
$user = authenticatedUser($pdo);

/**
 * Loads the enquiry row joined with the property owner and the enquirer's
 * display name. Returns the row or responds 404.
 */
function chatEnquiry(PDO $pdo, string $enquiryId): array
{
    $statement = $pdo->prepare(
        'SELECT enquiries.*, properties.user_id AS owner_id, users.name AS enquirer_name
         FROM enquiries
         LEFT JOIN properties ON properties.id = CAST(enquiries.property_id AS UNSIGNED)
         LEFT JOIN users ON users.id = enquiries.user_id
         WHERE enquiries.id = :id
         LIMIT 1'
    );
    $statement->execute(['id' => $enquiryId]);
    $row = $statement->fetch();
    if (!$row || $row['owner_id'] === null) {
        respond(404, false, 'Enquiry not found');
    }
    return $row;
}

/**
 * The only participants of a thread are the enquirer and the property owner.
 */
function chatParticipants(array $enquiry): array
{
    $participants = [(int) $enquiry['owner_id']];
    if ($enquiry['user_id'] !== null) {
        $participants[] = (int) $enquiry['user_id'];
    }
    return $participants;
}

function mapMessage(array $row): array
{
    return [
        'id' => (string) $row['id'],
        'enquiryId' => (string) $row['enquiry_id'],
        'senderId' => (string) $row['sender_id'],
        'senderName' => $row['sender_name'] !== null ? (string) $row['sender_name'] : '',
        'message' => (string) $row['message'],
        'createdAt' => (string) $row['created_at'],
    ];
}

$method = $_SERVER['REQUEST_METHOD'] ?? '';

if ($method === 'GET') {
    $enquiryId = is_string($_GET['enquiryId'] ?? null) ? trim($_GET['enquiryId']) : '';
    if ($enquiryId === '') {
        respond(422, false, 'enquiryId is required');
    }

    $enquiry = chatEnquiry($pdo, $enquiryId);
    if (!in_array((int) $user['id'], chatParticipants($enquiry), true)) {
        respond(403, false, 'Not a participant of this enquiry');
    }

    // The original enquiry text is the first message of the thread.
    $messages = [[
        'id' => 'e' . (string) $enquiry['id'],
        'enquiryId' => (string) $enquiry['id'],
        'senderId' => $enquiry['user_id'] !== null ? (string) $enquiry['user_id'] : '',
        'senderName' => $enquiry['enquirer_name'] !== null ? (string) $enquiry['enquirer_name'] : '',
        'message' => (string) $enquiry['message'],
        'createdAt' => (string) $enquiry['created_at'],
    ]];

    $statement = $pdo->prepare(
        'SELECT enquiry_messages.*, users.name AS sender_name
         FROM enquiry_messages
         LEFT JOIN users ON users.id = enquiry_messages.sender_id
         WHERE enquiry_messages.enquiry_id = :enquiry_id
         ORDER BY enquiry_messages.created_at ASC, enquiry_messages.id ASC'
    );
    $statement->execute(['enquiry_id' => $enquiryId]);
    foreach ($statement->fetchAll() as $row) {
        $messages[] = mapMessage($row);
    }

    respond(200, true, 'Messages loaded', ['messages' => $messages]);
}

if ($method === 'POST') {
    $body = jsonBody();
    $enquiryId = is_string($body['enquiryId'] ?? null) ? trim($body['enquiryId']) : '';
    if ($enquiryId === '') {
        respond(422, false, 'enquiryId is required');
    }

    $message = is_string($body['message'] ?? null) ? trim($body['message']) : '';
    if ($message === '') {
        respond(422, false, 'message is required');
    }

    $enquiry = chatEnquiry($pdo, $enquiryId);
    $participants = chatParticipants($enquiry);
    if (!in_array((int) $user['id'], $participants, true)) {
        respond(403, false, 'Not a participant of this enquiry');
    }

    $statement = $pdo->prepare(
        'INSERT INTO enquiry_messages (enquiry_id, sender_id, message)
         VALUES (:enquiry_id, :sender_id, :message)'
    );
    $statement->execute([
        'enquiry_id' => $enquiryId,
        'sender_id' => $user['id'],
        'message' => $message,
    ]);
    $messageId = (int) $pdo->lastInsertId();

    // Mark the enquiry as replied when the owner answers for the first time.
    if ((int) $user['id'] === (int) $enquiry['owner_id'] && $enquiry['status'] === 'new') {
        $update = $pdo->prepare('UPDATE enquiries SET status = :status WHERE id = :id');
        $update->execute(['status' => 'replied', 'id' => $enquiryId]);
    }

    $recipientId = (int) $user['id'] === (int) $enquiry['owner_id']
        ? (int) $enquiry['user_id']
        : (int) $enquiry['owner_id'];
    if ($recipientId > 0) {
        sendPushToUser(
            $pdo,
            $recipientId,
            'New message: ' . (string) $enquiry['property_title'],
            $message,
            [
                'propertyId' => (string) $enquiry['property_id'],
                'enquiryId' => (string) $enquiry['id'],
                'message' => $message,
            ]
        );
    }

    respond(201, true, 'Message sent', [
        'message' => [
            'id' => (string) $messageId,
            'enquiryId' => (string) $enquiry['id'],
            'senderId' => (string) $user['id'],
            'senderName' => (string) $user['name'],
            'message' => $message,
            'createdAt' => (new DateTimeImmutable('now', new DateTimeZone('UTC')))->format('Y-m-d H:i:s'),
        ],
    ]);
}

header('Allow: GET, POST');
respond(405, false, 'Method not allowed');
