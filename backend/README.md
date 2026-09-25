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

## Admin panel

A static admin site lives in `backend/admin/` (`index.html`, `styles.css`, `app.js`). It is a plain
HTML/CSS/JS single-page app — no build step — that talks to the same PHP endpoints in `api/`.

Deploy: copy the whole backend directory to `public_html/api` so the panel is reachable at
`https://your-domain.com/api/admin/` and calls `https://your-domain.com/api/api/*` (relative `../api`).

Features:

- Sign in with any active app account (`login.php`); the session token is stored in `localStorage`.
- Top sections: All / Featured / Promotional / My listings / Enquiries.
- Filter bar: search, city, locality, buy/rent, listing category, property type, bedrooms, sort.
- Stats row (total, featured, promo, buy, rent, cities) plus per-card enquiry counts
  (`enquiry-counts.php`).
- Card grid; clicking a card opens a detail modal with image gallery, all property fields,
  amenities, and the property's enquiries (`enquiries.php?type=property`).
- Delete button appears only on properties owned by the signed-in user (`delete-property.php`
  enforces ownership server-side too).
- If the API lives at a non-sibling path, override it with `?api=https://host/path/api` or
  `localStorage.setItem('fhiont_admin_api', 'https://host/path/api')`.

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

web site:
http://fhiont.com/fhiont-api/admin/