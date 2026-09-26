USE `fhionf96_app`;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL DEFAULT '',
    status VARCHAR(30) NOT NULL DEFAULT 'active',
    city VARCHAR(120) NOT NULL DEFAULT '',
    location VARCHAR(255) NOT NULL DEFAULT '',
    address TEXT NOT NULL,
    image VARCHAR(500) NULL,
    -- 1 = user can log in with email/password, 0 = Google-only account with a random unusable hash.
    password_set TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY users_email_unique (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS auth_tokens (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY auth_tokens_hash_unique (token_hash),
    KEY auth_tokens_user_id_index (user_id),
    KEY auth_tokens_expires_at_index (expires_at),
    CONSTRAINT auth_tokens_user_foreign FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS properties (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    price DECIMAL(15, 2) NULL,
    city VARCHAR(120) NOT NULL DEFAULT '',
    locality VARCHAR(120) NOT NULL DEFAULT '',
    pincode VARCHAR(20) NOT NULL DEFAULT '',
    address TEXT NOT NULL DEFAULT '',
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'live',
    listing_category VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    rent_buy VARCHAR(30) NULL,
    residential_commercial VARCHAR(30) NULL,
    property_type VARCHAR(30) NULL,
    bedroom_type VARCHAR(30) NULL,
    bathrooms INT UNSIGNED NULL,
    furnishing VARCHAR(30) NULL,
    facing VARCHAR(30) NULL,
    age VARCHAR(30) NULL,
    amenities JSON NULL,
    nearby_places JSON NULL,
    images JSON NULL,
    carpet_area DECIMAL(10, 2) NULL,
    built_up_area DECIMAL(10, 2) NULL,
    super_built_up_area DECIMAL(10, 2) NULL,
    agent_phone VARCHAR(30) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY properties_user_id_index (user_id),
    KEY properties_city_index (city),
    KEY properties_locality_index (locality),
    KEY properties_listing_category_index (listing_category),
    CONSTRAINT properties_user_foreign FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cities (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY cities_name_unique (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS localities (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    city VARCHAR(120) NOT NULL,
    locality VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY localities_city_locality_unique (city, locality),
    KEY localities_city_index (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS likes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    property_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY likes_user_property_unique (user_id, property_id),
    KEY likes_property_id_index (property_id),
    CONSTRAINT likes_user_foreign FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS enquiries (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    property_id VARCHAR(255) NOT NULL,
    property_title VARCHAR(255) NOT NULL DEFAULT '',
    property_location VARCHAR(255) NOT NULL DEFAULT '',
    property_image VARCHAR(500) NOT NULL DEFAULT '',
    agent_phone VARCHAR(30) NOT NULL DEFAULT '',
    message TEXT NOT NULL DEFAULT '',
    user_id BIGINT UNSIGNED NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'new',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY enquiries_user_id_index (user_id),
    KEY enquiries_property_id_index (property_id),
    CONSTRAINT enquiries_user_foreign FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS device_tokens (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    token VARCHAR(255) NOT NULL,
    platform VARCHAR(30) NOT NULL DEFAULT 'android',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY device_tokens_token_unique (token),
    KEY device_tokens_user_id_index (user_id),
    CONSTRAINT device_tokens_user_foreign FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS enquiry_messages (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    enquiry_id BIGINT UNSIGNED NOT NULL,
    sender_id BIGINT UNSIGNED NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY enquiry_messages_enquiry_index (enquiry_id),
    KEY enquiry_messages_sender_index (sender_id),
    CONSTRAINT enquiry_messages_enquiry_foreign FOREIGN KEY (enquiry_id) REFERENCES enquiries (id) ON DELETE CASCADE,
    CONSTRAINT enquiry_messages_sender_foreign FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migration for databases created before the password_set column existed:
-- ALTER TABLE users ADD COLUMN password_set TINYINT(1) NOT NULL DEFAULT 1 AFTER image;
