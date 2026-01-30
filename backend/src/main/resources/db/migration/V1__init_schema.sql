-- =====================================================
-- Hexavolt - Initial database schema (Flyway V1)
-- =====================================================

-- =====================
-- Reference tables
-- =====================

CREATE TABLE city (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_city_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE power (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kva_power DECIMAL(6,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE day_of_week (
    id BIGINT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE status_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    CONSTRAINT uk_status_reservation_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notification_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(64) NOT NULL,
    CONSTRAINT uk_notification_type_label UNIQUE (label)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================
-- Users & security
-- =====================

CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    birthdate DATE NOT NULL,
    city BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email_is_valid BOOLEAN NOT NULL,
    CONSTRAINT uq_app_user_email UNIQUE (email),
    CONSTRAINT fk_app_user_city FOREIGN KEY (city) REFERENCES city(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    CONSTRAINT fk_user_token_user FOREIGN KEY (user_id) REFERENCES app_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================
-- Stations & locations
-- =====================

CREATE TABLE station_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    city BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_station_location_city FOREIGN KEY (city) REFERENCES city(id),
    CONSTRAINT fk_station_location_user FOREIGN KEY (user_id) REFERENCES app_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE charging_station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hourly_rate DECIMAL(4,2) NOT NULL,
    photo_name VARCHAR(255),
    video_name VARCHAR(255),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    instruction TEXT,
    power BIGINT NOT NULL,
    location BIGINT NOT NULL,
    is_custom BOOLEAN NOT NULL,
    CONSTRAINT fk_charging_station_power FOREIGN KEY (power) REFERENCES power(id),
    CONSTRAINT fk_charging_station_location FOREIGN KEY (location) REFERENCES station_location(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE nickname_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    station_location BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT uk_nickname_location UNIQUE (user_id, station_location),
    CONSTRAINT fk_nickname_location_station FOREIGN KEY (station_location) REFERENCES station_location(id),
    CONSTRAINT fk_nickname_location_user FOREIGN KEY (user_id) REFERENCES app_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================
-- Reservations
-- =====================

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    amount DECIMAL(5,2) NOT NULL,
    receipt VARCHAR(255),
    user_id BIGINT NOT NULL,
    status_reservation BIGINT NOT NULL,
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_reservation_status FOREIGN KEY (status_reservation) REFERENCES status_reservation(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE charging_station_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    charging_station BIGINT NOT NULL,
    reservation BIGINT NOT NULL,
    CONSTRAINT fk_csr_station FOREIGN KEY (charging_station) REFERENCES charging_station(id),
    CONSTRAINT fk_csr_reservation FOREIGN KEY (reservation) REFERENCES reservation(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================
-- Schedules
-- =====================

CREATE TABLE weekly_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week BIGINT NOT NULL,
    charging_station BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_weekly_schedule_day FOREIGN KEY (day_of_week) REFERENCES day_of_week(id),
    CONSTRAINT fk_weekly_schedule_station FOREIGN KEY (charging_station) REFERENCES charging_station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE modified_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    charging_station BIGINT NOT NULL,
    CONSTRAINT fk_modified_station FOREIGN KEY (charging_station) REFERENCES charging_station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE closed_day (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    charging_station BIGINT NOT NULL,
    CONSTRAINT fk_closed_station FOREIGN KEY (charging_station) REFERENCES charging_station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================
-- Notifications
-- =====================

CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    seen BOOLEAN NOT NULL,
    timestamp DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    type BIGINT NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_notification_type FOREIGN KEY (type) REFERENCES notification_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
