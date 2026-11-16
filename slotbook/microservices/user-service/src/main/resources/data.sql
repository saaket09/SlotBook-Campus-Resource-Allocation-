MERGE INTO users (user_id, name, email, password, role)
KEY (email)
VALUES (1, 'Admin User', 'admin@campus.edu', 'admin123', 'ADMIN');

MERGE INTO users (user_id, name, email, password, role)
KEY (email)
VALUES (2, 'Rahul Sharma', 'rahul@campus.edu', 'password123', 'STUDENT');

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 100;
