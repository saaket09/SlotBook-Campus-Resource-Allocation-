-- SlotBook Seed Data
-- Inserts sample resources and an admin user on every fresh startup.
-- Uses MERGE (H2 upsert) so re-runs are idempotent.

-- ── Users (admin123 / password123) ──────────────────────────────────────────
MERGE INTO users (user_id, name, email, password, role)
KEY (email)
VALUES (1, 'Admin User', 'admin@campus.edu', 'admin123', 'ADMIN');

MERGE INTO users (user_id, name, email, password, role)
KEY (email)
VALUES (2, 'Rahul Sharma', 'rahul@campus.edu', 'password123', 'STUDENT');

-- ── Sample resources ─────────────────────────────────────────────────────────

-- Halls
MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (1, 'Seminar Hall A', 'HALL', 'Main Block, Floor 1', 120, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (2, 'Seminar Hall B', 'HALL', 'Main Block, Floor 2', 80, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (3, 'Conference Room 1', 'HALL', 'Admin Block, Ground Floor', 30, TRUE);

-- Sports
MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (4, 'Badminton Court 1', 'SPORTS', 'Sports Complex, Indoor Arena', 4, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (5, 'Badminton Court 2', 'SPORTS', 'Sports Complex, Indoor Arena', 4, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (6, 'Basketball Court', 'SPORTS', 'Sports Complex, Outdoor', 10, TRUE);

-- Labs
MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (7, 'Computer Lab A', 'LAB', 'Tech Block, Floor 2', 40, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (8, 'Physics Lab', 'LAB', 'Science Block, Floor 1', 30, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (9, 'Chemistry Lab', 'LAB', 'Science Block, Floor 2', 25, TRUE);

-- Equipment
MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (10, 'Projector Kit A', 'EQUIPMENT', 'AV Room, Main Block', 1, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (11, 'Portable PA System', 'EQUIPMENT', 'AV Room, Main Block', 1, TRUE);

MERGE INTO resources (resource_id, name, category, location, capacity, is_active)
KEY (resource_id)
VALUES (12, 'Video Camera Set', 'EQUIPMENT', 'Media Lab, Tech Block', 1, TRUE);

-- ── Reset auto-increment counters past seeded IDs ───────────────────────────
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 100;
ALTER TABLE resources ALTER COLUMN resource_id RESTART WITH 100;
