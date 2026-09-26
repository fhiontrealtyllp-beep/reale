<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('GET');

$pdo = database();
authenticatedUser($pdo);

$idsParam = $_GET['ids'] ?? '';
$ids = array_values(array_filter(array_map('trim', explode(',', $idsParam)), 'is_numeric'));

if (empty($ids)) {
    respond(200, true, 'No property IDs provided', ['counts' => new stdClass()]);
}

$placeholders = implode(',', array_fill(0, count($ids), '?'));
$statement = $pdo->prepare(
    "SELECT property_id, COUNT(*) AS count FROM enquiries WHERE property_id IN ($placeholders) GROUP BY property_id"
);
$statement->execute($ids);

$counts = [];
foreach ($statement->fetchAll() as $row) {
    $counts[(string) $row['property_id']] = (int) $row['count'];
}

respond(200, true, 'Enquiry counts loaded', ['counts' => (object) $counts]);
