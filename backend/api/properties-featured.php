<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('GET');

$pdo = database();
$user = authenticatedUser($pdo);

$limit = filter_input(INPUT_GET, 'limit', FILTER_VALIDATE_INT) ?: 10;
if ($limit < 1) {
    $limit = 10;
}

$statement = $pdo->prepare(
    'SELECT * FROM properties '
    . 'WHERE status = :status AND listing_category = :category '
    . 'ORDER BY created_at DESC '
    . 'LIMIT :limit'
);
$statement->bindValue(':status', 'live', PDO::PARAM_STR);
$statement->bindValue(':category', 'FEATURED', PDO::PARAM_STR);
$statement->bindValue(':limit', $limit, PDO::PARAM_INT);
$statement->execute();
$rows = $statement->fetchAll();

$properties = array_map(function (array $row): array {
    return [
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
}, $rows);

respond(200, true, 'Featured properties loaded', ['properties' => $properties]);
