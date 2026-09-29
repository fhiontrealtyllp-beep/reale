-- Migrations for EXISTING databases.
--
-- schema.sql uses CREATE TABLE IF NOT EXISTS, which never alters a table that
-- already exists. Run this file once in phpMyAdmin (select the fhionf96_app
-- database first, then Import/SQL) to add any `properties` columns that were
-- introduced after the table was originally created.
--
-- Each block checks information_schema before altering, so columns that
-- already exist are skipped and the whole file is safe to re-run.

-- ---------------------------------------------------------------------------
-- properties table columns
-- ---------------------------------------------------------------------------

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'description') = 0,
    'ALTER TABLE `properties` ADD COLUMN `description` TEXT NOT NULL DEFAULT '''' AFTER `title`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'price') = 0,
    'ALTER TABLE `properties` ADD COLUMN `price` DECIMAL(15, 2) NULL AFTER `description`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'city') = 0,
    'ALTER TABLE `properties` ADD COLUMN `city` VARCHAR(120) NOT NULL DEFAULT '''' AFTER `price`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'locality') = 0,
    'ALTER TABLE `properties` ADD COLUMN `locality` VARCHAR(120) NOT NULL DEFAULT '''' AFTER `city`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'pincode') = 0,
    'ALTER TABLE `properties` ADD COLUMN `pincode` VARCHAR(20) NOT NULL DEFAULT '''' AFTER `locality`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'address') = 0,
    'ALTER TABLE `properties` ADD COLUMN `address` TEXT NOT NULL DEFAULT '''' AFTER `pincode`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'latitude') = 0,
    'ALTER TABLE `properties` ADD COLUMN `latitude` DECIMAL(10, 8) NULL AFTER `address`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'longitude') = 0,
    'ALTER TABLE `properties` ADD COLUMN `longitude` DECIMAL(11, 8) NULL AFTER `latitude`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'status') = 0,
    'ALTER TABLE `properties` ADD COLUMN `status` VARCHAR(30) NOT NULL DEFAULT ''live'' AFTER `longitude`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'listing_category') = 0,
    'ALTER TABLE `properties` ADD COLUMN `listing_category` VARCHAR(30) NOT NULL DEFAULT ''NORMAL'' AFTER `status`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'rent_buy') = 0,
    'ALTER TABLE `properties` ADD COLUMN `rent_buy` VARCHAR(30) NULL AFTER `listing_category`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'residential_commercial') = 0,
    'ALTER TABLE `properties` ADD COLUMN `residential_commercial` VARCHAR(30) NULL AFTER `rent_buy`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'property_type') = 0,
    'ALTER TABLE `properties` ADD COLUMN `property_type` VARCHAR(30) NULL AFTER `residential_commercial`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'bedroom_type') = 0,
    'ALTER TABLE `properties` ADD COLUMN `bedroom_type` VARCHAR(30) NULL AFTER `property_type`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'bathrooms') = 0,
    'ALTER TABLE `properties` ADD COLUMN `bathrooms` INT UNSIGNED NULL AFTER `bedroom_type`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'furnishing') = 0,
    'ALTER TABLE `properties` ADD COLUMN `furnishing` VARCHAR(30) NULL AFTER `bathrooms`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'facing') = 0,
    'ALTER TABLE `properties` ADD COLUMN `facing` VARCHAR(30) NULL AFTER `furnishing`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'age') = 0,
    'ALTER TABLE `properties` ADD COLUMN `age` VARCHAR(30) NULL AFTER `facing`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'amenities') = 0,
    'ALTER TABLE `properties` ADD COLUMN `amenities` JSON NULL AFTER `age`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'nearby_places') = 0,
    'ALTER TABLE `properties` ADD COLUMN `nearby_places` JSON NULL AFTER `amenities`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'images') = 0,
    'ALTER TABLE `properties` ADD COLUMN `images` JSON NULL AFTER `nearby_places`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'carpet_area') = 0,
    'ALTER TABLE `properties` ADD COLUMN `carpet_area` DECIMAL(10, 2) NULL AFTER `images`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'built_up_area') = 0,
    'ALTER TABLE `properties` ADD COLUMN `built_up_area` DECIMAL(10, 2) NULL AFTER `carpet_area`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'super_built_up_area') = 0,
    'ALTER TABLE `properties` ADD COLUMN `super_built_up_area` DECIMAL(10, 2) NULL AFTER `built_up_area`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'total_rooms') = 0,
    'ALTER TABLE `properties` ADD COLUMN `total_rooms` INT UNSIGNED NULL AFTER `super_built_up_area`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'sharing_type') = 0,
    'ALTER TABLE `properties` ADD COLUMN `sharing_type` VARCHAR(30) NULL AFTER `total_rooms`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'preferred_tenant') = 0,
    'ALTER TABLE `properties` ADD COLUMN `preferred_tenant` VARCHAR(30) NULL AFTER `sharing_type`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'food_available') = 0,
    'ALTER TABLE `properties` ADD COLUMN `food_available` TINYINT(1) NULL AFTER `preferred_tenant`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'agent_phone') = 0,
    'ALTER TABLE `properties` ADD COLUMN `agent_phone` VARCHAR(30) NOT NULL DEFAULT '''' AFTER `food_available`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'created_at') = 0,
    'ALTER TABLE `properties` ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `agent_phone`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'properties' AND COLUMN_NAME = 'updated_at') = 0,
    'ALTER TABLE `properties` ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `created_at`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ---------------------------------------------------------------------------
-- users.password_set (added for Google-only accounts)
-- ---------------------------------------------------------------------------

SET @sql := IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'password_set') = 0,
    'ALTER TABLE `users` ADD COLUMN `password_set` TINYINT(1) NOT NULL DEFAULT 1 AFTER `image`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
