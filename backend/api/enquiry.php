<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$pdo = database();
$user = authenticatedUser($pdo);

$propertyId = is_string($body['propertyId'] ?? null) ? trim($body['propertyId']) : '';
if ($propertyId === '') {
    respond(422, false, 'propertyId is required');
}

$message = is_string($body['message'] ?? null) ? trim($body['message']) : '';
if ($message === '') {
    respond(422, false, 'message is required');
}

$propertyTitle = is_string($body['propertyTitle'] ?? null) ? trim($body['propertyTitle']) : '';
$propertyLocation = is_string($body['propertyLocation'] ?? null) ? trim($body['propertyLocation']) : '';
$propertyImage = is_string($body['propertyImage'] ?? null) ? trim($body['propertyImage']) : '';
$agentPhone = is_string($body['agentPhone'] ?? null) ? trim($body['agentPhone']) : '';

$propertyStatement = $pdo->prepare('SELECT user_id, title FROM properties WHERE id = :id AND status = :status LIMIT 1');
$propertyStatement->execute(['id' => $propertyId, 'status' => 'live']);
$property = $propertyStatement->fetch();
if (!$property) {
    respond(404, false, 'Property not found');
}

$pdo->beginTransaction();
try {
    $statement = $pdo->prepare(
        'INSERT INTO enquiries (
            property_id, property_title, property_location, property_image,
            agent_phone, message, user_id, status
        ) VALUES (
            :property_id, :property_title, :property_location, :property_image,
            :agent_phone, :message, :user_id, :status
        )'
    );

    $statement->execute([
        'property_id' => $propertyId,
        'property_title' => $propertyTitle,
        'property_location' => $propertyLocation,
        'property_image' => $propertyImage,
        'agent_phone' => $agentPhone,
        'message' => $message,
        'user_id' => $user['id'],
        'status' => 'new',
    ]);
    $enquiryId = (int) $pdo->lastInsertId();
    $pdo->commit();
} catch (Throwable $exception) {
    $pdo->rollBack();
    throw $exception;
}

sendEnquiryPush(
    $pdo,
    (int) $property['user_id'],
    $enquiryId,
    $propertyId,
    (string) $property['title'],
    $message
);
respond(201, true, 'Enquiry sent successfully', ['enquiryId' => (string) $enquiryId]);
