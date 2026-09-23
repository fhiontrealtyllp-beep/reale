<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';

$method = $_SERVER['REQUEST_METHOD'] ?? '';
$pdo = database();
$user = authenticatedUser($pdo);

if ($method === 'POST') {
    requireMethod('POST');
    $body = jsonBody();

    $title = trim(is_string($body['title'] ?? null) ? $body['title'] : '');
    if ($title === '') {
        respond(422, false, 'Title is required');
    }

    $price = is_numeric($body['price'] ?? null) ? (float) $body['price'] : null;
    $city = trim(is_string($body['city'] ?? null) ? $body['city'] : '');
    $locality = trim(is_string($body['locality'] ?? null) ? $body['locality'] : '');

    if ($city === '') {
        respond(422, false, 'City is required');
    }
    if ($locality === '') {
        respond(422, false, 'Locality is required');
    }

    $latitude = is_numeric($body['latitude'] ?? null) ? (float) $body['latitude'] : null;
    $longitude = is_numeric($body['longitude'] ?? null) ? (float) $body['longitude'] : null;

    $description = is_string($body['description'] ?? null) ? trim($body['description']) : '';
    $pincode = is_string($body['pincode'] ?? null) ? trim($body['pincode']) : '';
    $address = is_string($body['address'] ?? null) ? trim($body['address']) : '';
    $agentPhone = is_string($body['agentPhone'] ?? null) ? trim($body['agentPhone']) : '';
    $status = is_string($body['status'] ?? null) && $body['status'] !== '' ? $body['status'] : 'live';
    $listingCategory = is_string($body['listingCategory'] ?? null) && $body['listingCategory'] !== '' ? $body['listingCategory'] : 'NORMAL';

    $rentBuy = is_string($body['rentBuy'] ?? null) ? $body['rentBuy'] : null;
    $residentialCommercial = is_string($body['residentialCommercial'] ?? null) ? $body['residentialCommercial'] : null;
    $propertyType = is_string($body['propertyType'] ?? null) ? $body['propertyType'] : null;
    $bedroomType = is_string($body['bedroomType'] ?? null) ? $body['bedroomType'] : null;
    $bathrooms = filter_var($body['bathrooms'] ?? null, FILTER_VALIDATE_INT) ?: null;
    $furnishing = is_string($body['furnishing'] ?? null) ? $body['furnishing'] : null;
    $facing = is_string($body['facing'] ?? null) ? $body['facing'] : null;
    $age = is_string($body['age'] ?? null) ? $body['age'] : null;

    $carpetArea = is_numeric($body['carpetArea'] ?? null) ? (float) $body['carpetArea'] : null;
    $builtUpArea = is_numeric($body['builtUpArea'] ?? null) ? (float) $body['builtUpArea'] : null;
    $superBuiltUpArea = is_numeric($body['superBuiltUpArea'] ?? null) ? (float) $body['superBuiltUpArea'] : null;

    $amenities = is_array($body['amenities'] ?? null) ? array_values(array_filter($body['amenities'], 'is_string')) : [];
    $images = is_array($body['images'] ?? null) ? array_values(array_filter($body['images'], 'is_string')) : [];
    $nearbyPlaces = is_array($body['nearbyPlaces'] ?? null) ? $body['nearbyPlaces'] : [];

    $statement = $pdo->prepare(
        'INSERT INTO properties (
            user_id, title, description, price, city, locality, pincode, address,
            latitude, longitude, status, listing_category, rent_buy, residential_commercial,
            property_type, bedroom_type, bathrooms, furnishing, facing, age,
            amenities, nearby_places, images, carpet_area, built_up_area, super_built_up_area, agent_phone
        ) VALUES (
            :user_id, :title, :description, :price, :city, :locality, :pincode, :address,
            :latitude, :longitude, :status, :listing_category, :rent_buy, :residential_commercial,
            :property_type, :bedroom_type, :bathrooms, :furnishing, :facing, :age,
            :amenities, :nearby_places, :images, :carpet_area, :built_up_area, :super_built_up_area, :agent_phone
        )'
    );

    $statement->execute([
        'user_id' => $user['id'],
        'title' => $title,
        'description' => $description,
        'price' => $price,
        'city' => $city,
        'locality' => $locality,
        'pincode' => $pincode,
        'address' => $address,
        'latitude' => $latitude,
        'longitude' => $longitude,
        'status' => $status,
        'listing_category' => $listingCategory,
        'rent_buy' => $rentBuy,
        'residential_commercial' => $residentialCommercial,
        'property_type' => $propertyType,
        'bedroom_type' => $bedroomType,
        'bathrooms' => $bathrooms,
        'furnishing' => $furnishing,
        'facing' => $facing,
        'age' => $age,
        'amenities' => json_encode($amenities, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE),
        'nearby_places' => json_encode($nearbyPlaces, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE),
        'images' => json_encode($images, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE),
        'carpet_area' => $carpetArea,
        'built_up_area' => $builtUpArea,
        'super_built_up_area' => $superBuiltUpArea,
        'agent_phone' => $agentPhone,
    ]);

    $propertyId = (int) $pdo->lastInsertId();
    respond(201, true, 'Property added successfully', ['propertyId' => (string) $propertyId]);
}

if ($method === 'GET') {
    requireMethod('GET');

    $statement = $pdo->prepare(
        'SELECT * FROM properties WHERE user_id = :user_id ORDER BY created_at DESC'
    );
    $statement->execute(['user_id' => $user['id']]);
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

    respond(200, true, 'Properties loaded', ['properties' => $properties]);
}

respond(405, false, 'Method not allowed');
