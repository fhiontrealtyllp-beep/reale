<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();

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
$userId = is_numeric($body['userId'] ?? null) ? (int) $body['userId'] : null;

$pdo = database();
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
    'user_id' => $userId,
    'status' => 'new',
]);

$enquiryId = (int) $pdo->lastInsertId();
respond(201, true, 'Enquiry sent successfully', ['enquiryId' => (string) $enquiryId]);
