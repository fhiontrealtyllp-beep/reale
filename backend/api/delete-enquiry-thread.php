<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$pdo = database();
$currentUser = authenticatedUser($pdo);

$propertyId = is_string($body['propertyId'] ?? null) ? trim($body['propertyId']) : '';
$userId = is_string($body['userId'] ?? null) ? trim($body['userId']) : '';

if ($propertyId === '' || $userId === '') {
    respond(422, false, 'propertyId and userId are required');
}

$propertyStatement = $pdo->prepare('SELECT user_id FROM properties WHERE id = :id LIMIT 1');
$propertyStatement->execute(['id' => $propertyId]);
$property = $propertyStatement->fetch();

if (!$property) {
    respond(404, false, 'Property not found');
}

$isOwner = (string) $property['user_id'] === (string) $currentUser['id'];
$isEnquirer = (string) $userId === (string) $currentUser['id'];

if (!$isOwner && !$isEnquirer) {
    respond(403, false, 'Not authorized to delete this chat');
}

$enquiryStatement = $pdo->prepare(
    'SELECT id FROM enquiries WHERE property_id = :property_id AND user_id = :user_id'
);
$enquiryStatement->execute(['property_id' => $propertyId, 'user_id' => $userId]);
$enquiryIds = array_map('strval', array_column($enquiryStatement->fetchAll(), 'id'));

if (empty($enquiryIds)) {
    respond(200, true, 'Nothing to delete');
}

$pdo->beginTransaction();
try {
    $inPlaceholder = implode(',', array_fill(0, count($enquiryIds), '?'));

    $deleteMessages = $pdo->prepare(
        "DELETE FROM enquiry_messages WHERE enquiry_id IN ($inPlaceholder)"
    );
    $deleteMessages->execute($enquiryIds);

    $deleteEnquiries = $pdo->prepare(
        'DELETE FROM enquiries WHERE property_id = :property_id AND user_id = :user_id'
    );
    $deleteEnquiries->execute(['property_id' => $propertyId, 'user_id' => $userId]);

    $pdo->commit();
    respond(200, true, 'Chat deleted');
} catch (Throwable $exception) {
    $pdo->rollBack();
    throw $exception;
}
