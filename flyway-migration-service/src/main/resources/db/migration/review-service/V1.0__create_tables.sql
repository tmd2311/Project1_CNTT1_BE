-- ============================================
-- REVIEW SERVICE - Database Migration V1.0
-- Tách ReviewEntity thành 2 entities: QuestionEntity và ProductReviewEntity
-- ============================================

-- ============================================
-- 1. QUESTIONS TABLE (Q&A)
-- ============================================
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'ID người đăng câu hỏi',
    user_name VARCHAR(100) NOT NULL COMMENT 'Tên người đăng',
    user_avatar VARCHAR(255) COMMENT 'URL avatar',

    title VARCHAR(500) NOT NULL COMMENT 'Tiêu đề câu hỏi',
    content TEXT NOT NULL COMMENT 'Nội dung chi tiết',

    category_id BIGINT COMMENT 'ID danh mục (Laptop, Gaming, PC...)',

    like_count INT NOT NULL DEFAULT 0 COMMENT 'Số like',
    view_count INT NOT NULL DEFAULT 0 COMMENT 'Số lượt xem',
    answer_count INT NOT NULL DEFAULT 0 COMMENT 'Số câu trả lời',

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, CLOSED',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Câu hỏi được xác minh',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Câu hỏi nổi bật',
    rejection_reason VARCHAR(500) COMMENT 'Lý do từ chối',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_question_user_id (user_id),
    INDEX idx_question_category_id (category_id),
    INDEX idx_question_status (status),
    INDEX idx_question_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. PRODUCT_REVIEWS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'ID người đăng review',
    user_name VARCHAR(100) NOT NULL COMMENT 'Tên người đăng',
    user_avatar VARCHAR(255) COMMENT 'URL avatar',

    content TEXT NOT NULL COMMENT 'Nội dung đánh giá',

    product_id BIGINT NOT NULL COMMENT 'ID sản phẩm',
    product_name VARCHAR(255) COMMENT 'Tên sản phẩm (cache)',
    rating DOUBLE NOT NULL COMMENT 'Số sao: 1.0 - 5.0',

    like_count INT NOT NULL DEFAULT 0 COMMENT 'Số like',
    view_count INT NOT NULL DEFAULT 0 COMMENT 'Số lượt xem',

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Đánh giá được xác minh (đã mua hàng)',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Đánh giá nổi bật',
    rejection_reason VARCHAR(500) COMMENT 'Lý do từ chối',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_product_review_product_id (product_id),
    INDEX idx_product_review_user_id (user_id),
    INDEX idx_product_review_status (status),
    INDEX idx_product_review_rating (rating),
    INDEX idx_product_review_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. PRODUCT_REVIEW_IMAGES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS product_review_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_review_id BIGINT NOT NULL COMMENT 'ID product review',
    image_url VARCHAR(500) NOT NULL COMMENT 'URL ảnh',
    display_order INT NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_product_review_image_review_id (product_review_id),
    FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. REVIEW_CATEGORIES TABLE (shared)
-- ============================================
CREATE TABLE IF NOT EXISTS review_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên danh mục',
    slug VARCHAR(100) COMMENT 'Slug',
    description VARCHAR(255) COMMENT 'Mô tả',
    icon VARCHAR(255) COMMENT 'Icon URL hoặc emoji',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,

    INDEX idx_category_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. TAGS TABLE (shared)
-- ============================================
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên tag',
    slug VARCHAR(100) COMMENT 'Slug',
    usage_count INT NOT NULL DEFAULT 0 COMMENT 'Số lần sử dụng',

    INDEX idx_tag_slug (slug),
    INDEX idx_tag_usage_count (usage_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. QUESTION_TAGS TABLE (join table)
-- ============================================
CREATE TABLE IF NOT EXISTS question_tags (
    question_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. PRODUCT_REVIEW_TAGS TABLE (join table)
-- ============================================
CREATE TABLE IF NOT EXISTS product_review_tags (
    product_review_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (product_review_id, tag_id),
    FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. ANSWERS TABLE (updated - link to questions)
-- ============================================
CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT 'ID câu hỏi',

    user_id BIGINT NOT NULL COMMENT 'ID người trả lời',
    user_name VARCHAR(100) NOT NULL COMMENT 'Tên người trả lời',
    user_avatar VARCHAR(255) COMMENT 'URL avatar',

    content TEXT NOT NULL COMMENT 'Nội dung câu trả lời',

    like_count INT NOT NULL DEFAULT 0,
    dislike_count INT NOT NULL DEFAULT 0,

    is_best_answer BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Câu trả lời được chọn',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Từ chuyên gia',
    is_from_shop BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Phản hồi từ shop',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_question_id (question_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 9. REVIEW_REACTIONS TABLE (updated - support QUESTION, PRODUCT_REVIEW, ANSWER)
-- ============================================
CREATE TABLE IF NOT EXISTS review_reactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL COMMENT 'QUESTION, PRODUCT_REVIEW, ANSWER',
    target_id BIGINT NOT NULL COMMENT 'ID của target',
    user_id BIGINT NOT NULL COMMENT 'ID người react',
    type VARCHAR(20) NOT NULL COMMENT 'LIKE, DISLIKE, HELPFUL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_reaction (target_type, target_id, user_id),
    INDEX idx_reaction_target (target_type, target_id),
    INDEX idx_reaction_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- SEED DATA for categories
-- ============================================
INSERT INTO review_categories (name, slug, description, icon, is_active, display_order) VALUES
('Laptop', 'laptop', 'Câu hỏi về laptop', '💻', TRUE, 1),
('PC Gaming', 'pc-gaming', 'Câu hỏi về PC Gaming', '🎮', TRUE, 2),
('Phần cứng', 'phan-cung', 'Câu hỏi về linh kiện phần cứng', '🔧', TRUE, 3),
('Phần mềm', 'phan-mem', 'Câu hỏi về phần mềm', '💿', TRUE, 4),
('Tư vấn', 'tu-van', 'Tư vấn chung', '💡', TRUE, 5)
ON DUPLICATE KEY UPDATE name=name;
