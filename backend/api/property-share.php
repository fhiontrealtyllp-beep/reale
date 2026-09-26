<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('GET');

$id = isset($_GET['id']) ? trim((string) $_GET['id']) : '';
if ($id === '') {
    respond(400, false, 'Property ID is required');
}

$pdo = database();
$statement = $pdo->prepare(
    'SELECT * FROM properties WHERE id = :id AND status = :status LIMIT 1'
);
$statement->execute([
    'id' => $id,
    'status' => 'live',
]);
$row = $statement->fetch();

if (!$row) {
    respond(404, false, 'Property not found');
}

$property = [
    'id' => (string) $row['id'],
    'userId' => (string) $row['user_id'],
    'title' => (string) $row['title'],
    'description' => (string) $row['description'],
    'price' => $row['price'] !== null ? (float) $row['price'] : 0.0,
    'city' => (string) $row['city'],
    'locality' => (string) $row['locality'],
    'pincode' => (string) $row['pincode'],
    'address' => (string) $row['address'],
    'latitude' => $row['latitude'] !== null ? (float) $row['latitude'] : null,
    'longitude' => $row['longitude'] !== null ? (float) $row['longitude'] : null,
    'status' => (string) $row['status'],
    'listingCategory' => (string) $row['listing_category'],
    'rentBuy' => $row['rent_buy'],
    'residentialCommercial' => $row['residential_commercial'],
    'propertyType' => $row['property_type'],
    'bedroomType' => $row['bedroom_type'],
    'bathrooms' => $row['bathrooms'] !== null ? (int) $row['bathrooms'] : null,
    'furnishing' => $row['furnishing'],
    'facing' => $row['facing'],
    'age' => $row['age'],
    'amenities' => json_decode((string) $row['amenities'], true) ?: [],
    'nearbyPlaces' => json_decode((string) $row['nearby_places'], true) ?: [],
    'images' => json_decode((string) $row['images'], true) ?: [],
    'carpetArea' => $row['carpet_area'] !== null ? (float) $row['carpet_area'] : null,
    'builtUpArea' => $row['built_up_area'] !== null ? (float) $row['built_up_area'] : null,
    'superBuiltUpArea' => $row['super_built_up_area'] !== null ? (float) $row['super_built_up_area'] : null,
    'agentPhone' => (string) $row['agent_phone'],
    'createdAt' => (string) $row['created_at'],
];

respond(200, true, 'Property loaded', ['property' => $property]);
