<?php
declare(strict_types=1);

require __DIR__ . '/bootstrap.php';
requireMethod('GET');

$pdo = database();
$user = authenticatedUser($pdo);
respond(200, true, 'User loaded', ['user' => publicUser($user)]);
