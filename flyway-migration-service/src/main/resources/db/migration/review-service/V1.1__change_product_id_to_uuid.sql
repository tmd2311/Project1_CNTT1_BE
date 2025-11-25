-- ============================================
-- REVIEW SERVICE - Database Migration V1.1
-- Đổi product_id từ BIGINT sang UUID (PostgreSQL)
-- ============================================
-- ⚠️ WARNING: Script này sẽ XÓA toàn bộ data trong product_reviews
-- Nếu có data quan trọng, backup trước khi chạy!
-- ============================================

-- Bước 1: Xóa các bảng liên quan (do foreign key)
DROP TABLE IF EXISTS product_review_images CASCADE;
DROP TABLE IF EXISTS product_review_tags CASCADE;

-- Bước 2: Xóa index cũ
DROP INDEX IF EXISTS idx_product_review_product_id;

-- Bước 3: Xóa toàn bộ data trong product_reviews
TRUNCATE TABLE product_reviews CASCADE;

-- Bước 4: Đổi kiểu dữ liệu của product_id từ BIGINT sang UUID
ALTER TABLE product_reviews
    ALTER COLUMN product_id TYPE UUID USING product_id::text::uuid;

-- Bước 5: Cập nhật comment
COMMENT ON COLUMN product_reviews.product_id IS 'ID sản phẩm (UUID)';

-- Bước 6: Tạo lại index
CREATE INDEX idx_product_review_product_id ON product_reviews(product_id);

-- Bước 7: Tạo lại bảng product_review_images
CREATE TABLE IF NOT EXISTS product_review_images (
    id BIGSERIAL PRIMARY KEY,
    product_review_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_review_images_review
        FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_review_image_review_id ON product_review_images(product_review_id);

-- Bước 8: Tạo lại bảng product_review_tags
CREATE TABLE IF NOT EXISTS product_review_tags (
    product_review_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (product_review_id, tag_id),
    CONSTRAINT fk_product_review_tags_review
        FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_review_tags_tag
        FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
