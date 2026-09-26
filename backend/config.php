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
    'firebase' => [
        'project_id' => 'homefinder-b7907',
          'client_email' => 'firebase-adminsdk-fbsvc@homefinder-b7907.iam.gserviceaccount.com',
          'private_key' => "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC7J5kYAp7uuaXB
                                                         BmmEXN/ejhGlUojg+6NjbvMCJ4haFOh1fDZiB8A4M5cKZWpa7PpHIVcGn1Jg0rPn
                                                         VLybV/5GuNkNSqamT3zzDNGaXjnUT6P2jvev+6iHxoTbZonhfevByBAjHpINQk4m
                                                         sGvtcCWxu/t/2OpuMv9F7zX7zl0lvM1it/TrsG+QKdwrGa84c9BcA3LpIu4C7OKz
                                                         +lM4u5XVCn8YhL7hxxx6yMtkqWmF7otp14WgOxnFTk3KbBZLjRmj+tjyiTmUcxqB
                                                         fMtTpfbvRheUQxm86yFAjNcY9fJG5IrBqwfITFQQeFqmYnVsPyTBGekI7S++n8hU
                                                         5I9plYa/AgMBAAECggEANABFI+w6f8c6fH8NG3d4xow/7+kWWjjhBZRC1iLo2mq7
                                                         ykUIkUVpDdSbz4otOnVk9xWW6iKjj36L+SdMvnb9EmZy/KP02Pn2FExIAvXSaKD2
                                                         RGAmBxJMGem5aYK8pdhIhM/TWAa6+w8kytAzNMZ32sIKF9RMEvF9h093du84Z27r
                                                         pt98+NpvsTwjW0G+pN9870yoilr7A4AHubU0HRnCI0bCfixSf1wgJhSedyVd8Ng5
                                                         GZxP6plMf5rVmkSv0tigy1zVRP6fXym3ouEbACZ/qKuwPS9fIcrOC+/KlHigGX4L
                                                         yhq20cZPqwRfdQTBv6oKWale04wnqzztPq34azPyDQKBgQDuc/RJT7L4oU22kReN
                                                         l8hbRGB9tw1S337ydw5+h+Ca4NKglli8YbXgV1V7XKfWgIsUOR9dF/lMJvT7JunI
                                                         dPwUco54mj9TOC+RyiJL6xZBUepZH5p4ESX+4/njuTnh2O/6tRdCQZ5DHXovs8Qw
                                                         eql/8d3eLrrrsdGzYVob/EeLUwKBgQDI7UWsH+ydXelDI+L3ABOZYWv6FLAFO1tH
                                                         jXT1+/5MlbCO9XgUE/ZHBiJu0N0ZGhg0I+KWN/q8UiAkwc0bygF8sPYM4aSF/6/L
                                                         itrKhRmu5G09vKpQaPUch+tAouWM00miYu6eirEcVs0f7WR4fZqmAGwmrOnzz+UR
                                                         YQMX5WNVZQKBgQDncu1ea1mQMyE3X8W4zebPj6MI4zCvi/ewaRdMgZ7lNeu7q7Md
                                                         xsm+o4Z5YwoWDlbwnjFvLuHvpLCNRswMomA7iKX5Md7da9/gZo67mcfURypsr1xD
                                                         xoFW0vmurA8exG7KrhORYBe5lVcQDjETzTvQ0HdGPZ15QPQGZSwNleXruQKBgEHx
                                                         hY1RSPH48Wakr4fLUaNkUvDXu1FsQrAAICPfWX2/Hxw8OfW0+34h6EkgaYAkjU8H
                                                         fAU7SCJofb4ykOOO7+ABoC9oGvwTh5mw78/J0T0SITxC4E7Vs6Ryg2ZKUDZg3eja
                                                         uV/1Ot2HK3lQC0p0Rr42o0gqNfpb/90tTruYAHr1AoGBAKBhqYnqArnwv+Ep204u
                                                         NchiRoP8KoAKkquV9wIyTpq9x/vsZtn8gt3gVv+f6gXwwl62DgJ46GC961iUNk4t
                                                         VSnnqv2m6CnN6DGX/FEn4CfOwAE5KkH7C2GsJWsc4h+lcEQiQRI4aao68UMX8YCd
                                                         u3WbFrFU0BDvDpbG+i4Urere\n-----END PRIVATE KEY-----\n",
  ],
    'google' => [
        // Web client ID from google-services.json (used as serverClientId by the Android app).
        'web_client_id' => getenv('GOOGLE_WEB_CLIENT_ID')
            ?: '105670539110-kvp16h5kf3e00mqpkk7847ai28pa2qjp.apps.googleusercontent.com',
    ],
    'cors' => [
        'allowed_origins' => ['*'],
    ],
];
