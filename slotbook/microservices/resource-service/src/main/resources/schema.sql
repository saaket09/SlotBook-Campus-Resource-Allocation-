CREATE TABLE IF NOT EXISTS resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(50),
    location    VARCHAR(100),
    capacity    INT,
    is_active   BOOLEAN DEFAULT TRUE
);
