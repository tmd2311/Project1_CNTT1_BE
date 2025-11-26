-- =========================================================
-- DATABASE: proshop_product
-- Author: Ta Manh Dung
-- Description: Schema cho module sản phẩm
-- =========================================================

-- =========================================================
-- 3️⃣ Bảng thương hiệu (brand)
-- =========================================================
CREATE TABLE IF NOT EXISTS brand (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    logo_url TEXT
    );

-- =========================================================
-- 4️⃣ Bảng danh mục (category)
-- =========================================================
CREATE TABLE IF NOT EXISTS category (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    image_url TEXT,
    parent_id UUID,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE SET NULL
    );

-- =========================================================
-- 5️⃣ Bảng sản phẩm (product)
-- =========================================================
CREATE TABLE IF NOT EXISTS product (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    category_id UUID,
    brand_id UUID,
    description TEXT,
    specs JSONB,
    thumbnail_url TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brand (id) ON DELETE SET NULL
    );

-- =========================================================
-- 6️⃣ Bảng ảnh sản phẩm (product_image)
-- =========================================================
CREATE TABLE IF NOT EXISTS product_image (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID,
    url TEXT,
    is_primary BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
    );

-- =========================================================
-- 7️⃣ Bảng SKU (stock keeping unit)
-- =========================================================
CREATE TABLE IF NOT EXISTS sku (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID,
    sku_code VARCHAR(255) UNIQUE,
    specs JSONB,
    price DOUBLE PRECISION,
    discount_price DOUBLE PRECISION,
    sale_price DOUBLE PRECISION,
    sale_id BIGINT,
    stock INTEGER,
    barcode VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_sku_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
    );

-- =========================================================
-- 8️⃣ Index tối ưu
-- =========================================================
CREATE INDEX IF NOT EXISTS idx_category_slug ON category(slug);
CREATE INDEX IF NOT EXISTS idx_brand_slug ON brand(slug);
CREATE INDEX IF NOT EXISTS idx_product_name ON product(name);
CREATE INDEX IF NOT EXISTS idx_sku_code ON sku(sku_code);
CREATE INDEX IF NOT EXISTS idx_sku_sale_id ON sku(sale_id);

-- =========================================================
-- 9️⃣ Comments
-- =========================================================
COMMENT ON COLUMN sku.sale_price IS 'Giá sau khi áp dụng sale (nếu có)';
COMMENT ON COLUMN sku.sale_id IS 'ID của chương trình sale đang được áp dụng';

-- =========================================================
-- ✅ Hoàn tất schema cho module sản phẩm
-- =========================================================
