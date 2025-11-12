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
    ('U1', 'user1', 'user1', 'user1@example.com', 'User 1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password1
    ('U2', 'user2', 'user2', 'user2@example.com', 'User 2', '$2a$10$DU6G.VJCdJVZIlELrHKCTeDnJl2FGHn1gJWLyJnKkqMcYfpV6pAFW', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password2
    ('U3', 'user3', 'user3', 'user3@example.com', 'User 3', '$2a$10$WQ5c6lyKrKJBxmM2mQfW2OlzS9EJQMvLmjTnYp2hYxLKVZx5BPKoS', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password3
    ('U4', 'user4', 'user4', 'user4@example.com', 'User 4', '$2a$10$XH0HwJvF3V3HnBmKvN2XGOEz8qgD1pLfN3wH5R7C5f7nY6bV4tZXm', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password4
    ('U5', 'user5', 'user5', 'user5@example.com', 'User 5', '$2a$10$YI1IxKvG4W4IoC/mW3pYHPF0a/rE2qMgO4xI6S8D6g8oZ7cW5uaYn', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password5
    ('U6', 'user6', 'user6', 'user6@example.com', 'User 6', '$2a$10$Kx3w6kyQ5aFTiyojHt6ZvumgIxoKUq.Yx7H83StW5MckQeeCGpJya', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password6
    ('U7', 'user7', 'user7', 'user7@example.com', 'User 7', '$2a$10$AK3KzMwI6Y6KqEoY5rAJRH2c/tG4sOiQ6zK8U0F8i0qB9eY7wcAp', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password7
    ('U8', 'user8', 'user8', 'user8@example.com', 'User 8', '$2a$10$BL4L0NxJ7Z7LrFpZ6sBKSI3d/uH5tPjR7AL9V1G9j1rC0fZ8xdBq', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password8
    ('U9', 'user9', 'user9', 'user9@example.com', 'User 9', '$2a$10$CM5M1OyK8A8MsGqA7tCLTJ4e/vI6uQkS8BM0W2H0k2sD1gA9yeCr', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- password: password9
    ('U10', 'user10', 'user10', 'user10@example.com', 'User 10', '$2a$10$DN6N2PzL9B9NtHrB8uDMUK5f/wJ7vRlT9CN1X3I1l3tE2hB0zfDs', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) -- password: password10
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

