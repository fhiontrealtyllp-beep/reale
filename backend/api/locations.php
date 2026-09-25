<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';

$method = $_SERVER['REQUEST_METHOD'] ?? '';
$pdo = database();

if ($method === 'GET') {
    // Public read: the city picker and suggestion chips must work for
    // logged-out users during onboarding.
    $cityStatement = $pdo->query('SELECT name FROM cities ORDER BY name ASC');
    $cities = array_values(array_map(
        static fn (array $row): string => (string) $row['name'],
        $cityStatement->fetchAll()
    ));

    $localityStatement = $pdo->query('SELECT city, locality FROM localities ORDER BY city ASC, locality ASC');
    $localities = array_map(static function (array $row): array {
        return [
            'city' => (string) $row['city'],
            'locality' => (string) $row['locality'],
        ];
    }, $localityStatement->fetchAll());

    respond(200, true, 'Locations loaded', [
        'cities' => $cities,
        'localities' => $localities,
    ]);
}

if ($method === 'POST') {
    requireMethod('POST');
    $user = authenticatedUser($pdo);
    $body = jsonBody();

    $city = trim(is_string($body['city'] ?? null) ? $body['city'] : '');
    $locality = trim(is_string($body['locality'] ?? null) ? $body['locality'] : '');

    if ($city === '') {
        respond(422, false, 'City is required');
    }

    // utf8mb4_unicode_ci is case-insensitive, so the unique keys dedupe
    // e.g. "Goa" and "goa" into a single row.
    $upsertCity = $pdo->prepare('INSERT IGNORE INTO cities (name) VALUES (:name)');
    $upsertCity->execute(['name' => $city]);

    if ($locality !== '') {
        $upsertLocality = $pdo->prepare(
            'INSERT IGNORE INTO localities (city, locality) VALUES (:city, :locality)'
        );
        $upsertLocality->execute([
            'city' => $city,
            'locality' => $locality,
        ]);
    }

    respond(200, true, 'Location saved');
}

respond(405, false, 'Method not allowed');
