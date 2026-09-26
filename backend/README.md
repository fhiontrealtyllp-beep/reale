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
8. Create a Firebase service account with Firebase Cloud Messaging permission and expose its values to PHP as `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`, and `FIREBASE_PRIVATE_KEY`. The private key may contain escaped `\\n` line breaks. PHP must have the `curl` and `openssl` extensions enabled.
9. Existing databases must also run the `device_tokens` table statement and the `users.password_set` `ALTER TABLE` statement from `schema.sql` before deploying push notifications and Google sign-in.

## Endpoints

- `POST /api/register.php`: JSON body with `name`, `email`, and `password`.
- `POST /api/login.php`: JSON body with `email` and `password`.
- `POST /api/google-login.php`: JSON body with `idToken` (Google ID token from Credential Manager). Verifies the token with Google, creates the account on first use, and returns a session token like `login.php`. Configure `google.web_client_id` in `config.php` (or the `GOOGLE_WEB_CLIENT_ID` env var).
- `GET /api/me.php`: `Authorization: Bearer <token>`.
- `POST /api/change-password.php`: authenticated JSON body with `currentPassword` and `newPassword`. Accounts that still have `password_set = 0` (Google-created) skip the current-password check. All other sessions are revoked.
- `POST /api/logout.php`: `Authorization: Bearer <token>`.
- `POST /api/device-token.php`: authenticated JSON body with `token` and `platform`; associates an FCM installation with the signed-in owner.
- `POST /api/enquiry.php`: authenticated enquiry creation; sends an FCM notification to every registered device of the property owner.

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

Email/password login, signup, and Google sign-in are implemented. Phone authentication, profile updates, and the remaining flows still use Firebase until their PHP endpoints are migrated in subsequent phases.

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