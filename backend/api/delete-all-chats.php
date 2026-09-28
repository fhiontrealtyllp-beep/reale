<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('POST');

$body = jsonBody();
$pdo = database();
$currentUser = authenticatedUser($pdo);

$confirmed = ($body['confirm'] ?? false) === true;
$userId = is_numeric($body['userId'] ?? null) ? (int) $body['userId'] : null;

if (!$confirmed) {
    respond(422, false, 'Pass confirm: true to delete chats');
}

$currentUserId = (int) $currentUser['id'];

if ($userId !== null && $userId !== $currentUserId) {
    respond(403, false, 'Can only delete chats for the authenticated user');
}

$pdo->beginTransaction();
try {
    if ($userId !== null) {
        // Fetch property IDs owned by this user so we can delete enquiries
        // where the user is either the enquirer or the property owner.
        $propertyStatement = $pdo->prepare('SELECT id FROM properties WHERE user_id = :owner_id');
        $propertyStatement->execute(['owner_id' => $currentUserId]);
        $ownerPropertyIds = array_map('strval', array_column($propertyStatement->fetchAll(), 'id'));

        $placeholders = [];
        $params = [$currentUserId];

        $sql = 'SELECT id FROM enquiries WHERE user_id = ?';
        if (!empty($ownerPropertyIds)) {
            $placeholders = array_fill(0, count($ownerPropertyIds), '?');
            $sql .= ' OR property_id IN (' . implode(',', $placeholders) . ')';
            $params = array_merge($params, $ownerPropertyIds);
        }

        $enquiryStatement = $pdo->prepare($sql);
        $enquiryStatement->execute($params);
        $enquiryIds = array_map('intval', array_column($enquiryStatement->fetchAll(), 'id'));

        if (!empty($enquiryIds)) {
            $inPlaceholder = implode(',', array_fill(0, count($enquiryIds), '?'));

            $deleteMessages = $pdo->prepare("DELETE FROM enquiry_messages WHERE enquiry_id IN ($inPlaceholder)");
            $deleteMessages->execute($enquiryIds);

            $deleteEnquiries = $pdo->prepare("DELETE FROM enquiries WHERE id IN ($inPlaceholder)");
            $deleteEnquiries->execute($enquiryIds);
        }
    } else {
        // Bulk delete every chat and enquiry.
        $pdo->exec('DELETE FROM enquiry_messages');
        $pdo->exec('DELETE FROM enquiries');
    }

    $pdo->commit();
    respond(200, true, 'Chats deleted');
} catch (Throwable $exception) {
    $pdo->rollBack();
    throw $exception;
}
