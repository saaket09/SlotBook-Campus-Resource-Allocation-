CREATE TABLE IF NOT EXISTS bookings (
    booking_id        INT AUTO_INCREMENT PRIMARY KEY,
    resource_id       INT NOT NULL,
    resource_name     VARCHAR(100),
    resource_category VARCHAR(50),
    resource_location VARCHAR(100),
    user_id           INT NOT NULL,
    user_name         VARCHAR(100),
    booking_date      DATE NOT NULL,
    start_time        TIME NOT NULL,
    end_time          TIME NOT NULL,
    status            VARCHAR(20) DEFAULT 'CONFIRMED',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
