<?php
declare(strict_types=1);

return [
    'app' => [
        'debug' => true,
    ],
    'database' => [
        'host' => 'localhost',
        'port' => 3306,
        'name' => 'fhionf96_app',
        'user' => 'fhionf96_app_user',
        'password' => 'fhionf96_app_user',
        'charset' => 'utf8mb4',
    ],
    'auth' => [
        'token_ttl_days' => 30,
    ],
    'cors' => [
        'allowed_origins' => ['*'],
    ],
];
