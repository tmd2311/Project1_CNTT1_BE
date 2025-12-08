-- Bảng roles
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGSERIAL PRIMARY KEY,
                                     code VARCHAR(100) UNIQUE,
    name VARCHAR(255),
    description TEXT,
    status VARCHAR(20),
    created_date TIMESTAMP,
    created_by VARCHAR(255),
    modified_date TIMESTAMP,
    modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
    );

-- Bảng users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     code VARCHAR(100) UNIQUE NOT NULL,
    account VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(100),
    email VARCHAR(255),
    full_name VARCHAR(255),
    password_hash VARCHAR(255),
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    current_address TEXT,
    birthday DATE,
    last_login TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_date TIMESTAMP,
    created_by VARCHAR(255),
    modified_date TIMESTAMP,
    modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
    );

-- Bảng user_role
CREATE TABLE IF NOT EXISTS user_role (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    context_data JSONB,
    created_date TIMESTAMP,
    created_by VARCHAR(255),
    modified_date TIMESTAMP,
    modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
    );

-- Bảng social_provider
CREATE TABLE IF NOT EXISTS social_provider (
                                               id BIGSERIAL PRIMARY KEY,
                                               provider_name VARCHAR(50),
    provider_code VARCHAR(100),
    base VARCHAR(100),
    user_id BIGINT REFERENCES users(id),
    created_date TIMESTAMP,
    created_by VARCHAR(255),
    modified_date TIMESTAMP,
    modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
    );

-- Index có thể thêm nếu cần
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role(role_id);
CREATE INDEX IF NOT EXISTS idx_social_provider_user_id ON social_provider(user_id);
