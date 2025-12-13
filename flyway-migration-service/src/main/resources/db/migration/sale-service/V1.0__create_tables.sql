-- ============================================
-- SALE SERVICE - Database Migration V1.0
-- Tạo bảng cho Sale và Voucher Service
-- ============================================

-- ============================================
-- 1. SALES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Loại sale
    sale_type VARCHAR(50) NOT NULL,
    sale_value DECIMAL(10, 2) NOT NULL,

    -- Giới hạn
    min_purchase_quantity INT DEFAULT 1,
    max_discount_amount DECIMAL(10, 2),
    quantity INT,
    used_count INT DEFAULT 0,
    min_order_value DECIMAL(10, 2),

    -- Phạm vi áp dụng
    apply_scope VARCHAR(50) NOT NULL,

    -- Thời gian
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,

    -- Trạng thái
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    is_active BOOLEAN DEFAULT TRUE,
    is_delete BOOLEAN DEFAULT FALSE,

    -- Priority
    priority INT DEFAULT 0,

    -- Hình ảnh
    banner_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),

    -- Tracking
    total_applied_count INT DEFAULT 0,

    -- Audit
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

COMMENT ON COLUMN sales.code IS 'Mã sale (unique)';
COMMENT ON COLUMN sales.name IS 'Tên chương trình sale';
COMMENT ON COLUMN sales.sale_type IS 'PERCENTAGE, FIXED_AMOUNT, BUY_X_GET_Y';
COMMENT ON COLUMN sales.sale_value IS 'Giá trị giảm (%, số tiền)';
COMMENT ON COLUMN sales.apply_scope IS 'ALL_PRODUCTS, SPECIFIC_PRODUCTS, CATEGORY, BRAND';
COMMENT ON COLUMN sales.status IS 'SCHEDULED, ACTIVE, EXPIRED, PAUSED, CANCELLED';
COMMENT ON COLUMN sales.priority IS 'Độ ưu tiên (số càng cao càng ưu tiên)';

CREATE INDEX IF NOT EXISTS idx_sales_code ON sales(code);
CREATE INDEX IF NOT EXISTS idx_sales_status ON sales(status);
CREATE INDEX IF NOT EXISTS idx_sales_dates ON sales(start_date, end_date);

-- ============================================
-- 2. SALE_PRODUCTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS sale_products (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,

    -- Target (Product/SKU/Category/Brand)
    product_id UUID,
    sku_id UUID,
    category_id UUID,
    brand_id UUID,

    -- Giá (Snapshot)
    original_price DECIMAL(10, 2),
    sale_price DECIMAL(10, 2),
    discount_amount DECIMAL(10, 2),

    -- Trạng thái
    is_applied BOOLEAN DEFAULT FALSE,
    applied_at TIMESTAMP,
    reverted_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (sale_id, product_id, sku_id)
);

COMMENT ON COLUMN sale_products.sale_id IS 'ID chương trình sale';
COMMENT ON COLUMN sale_products.product_id IS 'ID sản phẩm (UUID)';
COMMENT ON COLUMN sale_products.sku_id IS 'ID SKU cụ thể (UUID)';
COMMENT ON COLUMN sale_products.category_id IS 'ID danh mục (UUID)';
COMMENT ON COLUMN sale_products.brand_id IS 'ID thương hiệu (UUID)';
COMMENT ON COLUMN sale_products.original_price IS 'Giá gốc trước khi sale';
COMMENT ON COLUMN sale_products.sale_price IS 'Giá sau khi sale';
COMMENT ON COLUMN sale_products.is_applied IS 'Đã apply sale vào SKU chưa';

CREATE INDEX IF NOT EXISTS idx_sale_products_sale_id ON sale_products(sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_products_product_id ON sale_products(product_id);
CREATE INDEX IF NOT EXISTS idx_sale_products_sku_id ON sale_products(sku_id);
CREATE INDEX IF NOT EXISTS idx_sale_products_category_id ON sale_products(category_id);
CREATE INDEX IF NOT EXISTS idx_sale_products_brand_id ON sale_products(brand_id);

-- ============================================
-- 3. VOUCHERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS vouchers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Loại voucher
    voucher_type VARCHAR(50) NOT NULL,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,

    -- Điều kiện
    min_order_value DECIMAL(10, 2) DEFAULT 0,
    max_discount_amount DECIMAL(10, 2),

    -- Giới hạn sử dụng
    total_quantity INT NOT NULL,
    used_count INT DEFAULT 0,
    usage_limit_per_user INT DEFAULT 1,

    -- Phạm vi user
    user_scope VARCHAR(50) NOT NULL,

    -- Thời gian
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,

    -- Trạng thái
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    is_active BOOLEAN DEFAULT TRUE,
    is_delete BOOLEAN DEFAULT FALSE,

    -- Hình ảnh
    banner_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),

    -- Audit
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

COMMENT ON COLUMN vouchers.code IS 'Mã voucher (unique)';
COMMENT ON COLUMN vouchers.name IS 'Tên chương trình voucher';
COMMENT ON COLUMN vouchers.voucher_type IS 'SHIPPING, ORDER_DISCOUNT, PRODUCT_DISCOUNT';
COMMENT ON COLUMN vouchers.discount_type IS 'PERCENTAGE, FIXED_AMOUNT';
COMMENT ON COLUMN vouchers.discount_value IS 'Giá trị giảm (%, số tiền)';
COMMENT ON COLUMN vouchers.user_scope IS 'ALL_USERS, SPECIFIC_USERS, NEW_USERS, VIP_USERS';
COMMENT ON COLUMN vouchers.status IS 'SCHEDULED, ACTIVE, EXPIRED, PAUSED, CANCELLED';

CREATE INDEX IF NOT EXISTS idx_vouchers_code ON vouchers(code);
CREATE INDEX IF NOT EXISTS idx_vouchers_status ON vouchers(status);
CREATE INDEX IF NOT EXISTS idx_vouchers_dates ON vouchers(start_date, end_date);

-- ============================================
-- 4. VOUCHER_USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS voucher_users (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (voucher_id, user_id)
);

COMMENT ON COLUMN voucher_users.voucher_id IS 'ID voucher';
COMMENT ON COLUMN voucher_users.user_id IS 'ID user được gán voucher';

CREATE INDEX IF NOT EXISTS idx_voucher_users_voucher_id ON voucher_users(voucher_id);
CREATE INDEX IF NOT EXISTS idx_voucher_users_user_id ON voucher_users(user_id);

-- ============================================
-- 5. VOUCHER_USAGE TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS voucher_usage (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,

    -- Thông tin giảm giá
    order_value DECIMAL(10, 2),
    discount_amount DECIMAL(10, 2),

    -- Trạng thái
    status VARCHAR(50) NOT NULL DEFAULT 'USED',
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN voucher_usage.voucher_id IS 'ID voucher đã sử dụng';
COMMENT ON COLUMN voucher_usage.user_id IS 'ID user sử dụng';
COMMENT ON COLUMN voucher_usage.order_id IS 'ID đơn hàng';
COMMENT ON COLUMN voucher_usage.status IS 'USED, REFUNDED, CANCELLED';

CREATE INDEX IF NOT EXISTS idx_voucher_usage_voucher_id ON voucher_usage(voucher_id);
CREATE INDEX IF NOT EXISTS idx_voucher_usage_user_id ON voucher_usage(user_id);
CREATE INDEX IF NOT EXISTS idx_voucher_usage_order_id ON voucher_usage(order_id);

-- ============================================
-- FOREIGN KEYS
-- ============================================
ALTER TABLE sale_products
    ADD CONSTRAINT fk_sale_products_sale
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE;

ALTER TABLE voucher_users
    ADD CONSTRAINT fk_voucher_users_voucher
    FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE;

ALTER TABLE voucher_usage
    ADD CONSTRAINT fk_voucher_usage_voucher
    FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE;
