-- V1.1__seed_initial_data.sql
-- Seed initial roles and users for authentication service

-- Insert roles
INSERT INTO roles (code, name, status, created_date, modified_date)
VALUES
    ('ADMIN', 'Administrator', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CUSTOMER', 'Customer', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (code) DO NOTHING;

-- Insert 10 users
INSERT INTO users (code, account, username, email, full_name, password_hash, status, created_date, modified_date)
VALUES
    ('U1', 'user1', 'user1', 'user1@example.com', 'User 1', '$2a$10$tcE0J0pgeyDUAymxFvGbP.X0B817ndvB0g.H0lCAddQUpoA0Up0U.', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password1
    ('U2', 'user2', 'user2', 'user2@example.com', 'User 2', '$2a$10$aZeATsV0WC9IZoEZUMF/IeCNgEMo9wxczSNrzFzl0zDmA1kB.J.QS', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password2
    ('U3', 'user3', 'user3', 'user3@example.com', 'User 3', '$2a$10$mt5s6vzpgzqbXD48ditPm.FxADsXuZCy8NT41rEM96/q/u9JWhdr2', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password3
    ('U4', 'user4', 'user4', 'user4@example.com', 'User 4', '$2a$10$Pt9SKe5BUWbpWTq.RBhrdeenvfyfqySJARl3I7WKzxS0BK3R.xw7W', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password4
    ('U5', 'user5', 'user5', 'user5@example.com', 'User 5', '$2a$10$Kx3w6kyQ5aFTiyojHt6ZvumgIxoKUq.Yx7H83StW5MckQeeCGpJya', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password5
    ('U6', 'user6', 'user6', 'user6@example.com', 'User 6', '$2a$10$KdxDyELwKFTqleV8KAca0.DCpHzvvltBD1no.a8g0TBQLuChrYCxu', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password6
    ('U7', 'user7', 'user7', 'user7@example.com', 'User 7', '$2a$10$AK3KzMwI6Y6KqEoY5rAJRH2c/tG4sOiQ6zK8U0F8i0qB9eY7wcAp', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password7
    ('U8', 'user8', 'user8', 'user8@example.com', 'User 8', '$2a$10$YWr4mM9g5f5p0PZ9mMOKcecRjzA5.cp38ZenWHMB/YWIR53s8lAsu', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password8
    ('U9', 'user9', 'user9', 'user9@example.com', 'User 9', '$2a$10$RHLLEMAjE0gaWIrkOpluEOGOrbEeMc8eaJ1aTbICywtISOsiNwlF2', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password9
    ('U10', 'user10', 'user10', 'user10@example.com', 'User 10', '$2a$10$peMBvDNH4v7divXeWVCuUu1knLY9KY4fuJfZOrY0eX41/cxdM5.Aq', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) -- password: password10
    ON CONFLICT (code) DO NOTHING;

-- Assign roles to users
-- Users 1-2: ADMIN
-- Users 3-4: CUSTOMER
-- Users 5-10: Alternating (odd=CUSTOMER, even=ADMIN)
INSERT INTO user_role (user_id, role_id, context_data, created_date, modified_date)
SELECT
    u.id,
    r.id,
    '{}'::jsonb,
        CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
         CROSS JOIN roles r
WHERE
    (u.code IN ('U1', 'U2') AND r.code = 'ADMIN') OR
    (u.code IN ('U3', 'U4') AND r.code = 'CUSTOMER') OR
    (u.code = 'U5' AND r.code = 'CUSTOMER') OR
    (u.code = 'U6' AND r.code = 'ADMIN') OR
    (u.code = 'U7' AND r.code = 'CUSTOMER') OR
    (u.code = 'U8' AND r.code = 'ADMIN') OR
    (u.code = 'U9' AND r.code = 'CUSTOMER') OR
    (u.code = 'U10' AND r.code = 'ADMIN')
    ON CONFLICT DO NOTHING;

