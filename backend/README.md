# Fhiont PHP/MySQL API

This backend targets cPanel/HostGator shared hosting with PHP 8.1+ and MySQL 5.7+/8.0.

## Deploy

1. In cPanel, create a MySQL database and user, then grant the user all privileges on that database.
2. Open phpMyAdmin, select the database, and import `schema.sql`.
3. Copy this backend directory into an HTTPS-enabled folder such as `public_html/api`.
4. Rename `config.example.php` to `config.php` and enter the cPanel database credentials.
5. Keep `config.php` private. The included `.htaccess` blocks direct web access to it.
6. Set this Android Gradle property using the final API directory URL:

   `API_BASE_URL=https://your-domain.com/api/api`

   Put it in your user Gradle properties file or pass it through the build environment. Do not commit hosting credentials.
7. Build a new APK/AAB. When `API_BASE_URL` is configured, email login and registration use PHP/MySQL. If it is blank, the current Firebase implementation remains active.

## Endpoints

- `POST /api/register.php`: JSON body with `name`, `email`, and `password`.
- `POST /api/login.php`: JSON body with `email` and `password`.
- `GET /api/me.php`: `Authorization: Bearer <token>`.
- `POST /api/logout.php`: `Authorization: Bearer <token>`.

Passwords use PHP `password_hash()` and are never returned. Session tokens are random, stored as SHA-256 hashes, and expire according to `config.php`.

## Current migration boundary

Email/password login and signup are implemented. Google and phone authentication, properties, likes, enquiries, profile updates, and image uploads still use Firebase until their PHP endpoints are migrated in subsequent phases.

fhiontrealtyllp@gmail.com
Fhiont@homes@101

database name is:
fhionf96_app

user:
fhionf96_app_user
password:
fhionf96_app_user

https://manage.bigrock.in/servlet/LoginServlet?role=customer&currenturl=https%3A%2F%2Fmanage.bigrock.in&utm_source=obox&utm_medium=email&utm_campaign=domain_success_email&utm_term=login&utm_content=login_button

cPAnel:
fhionf96
Fhiont@homes@10