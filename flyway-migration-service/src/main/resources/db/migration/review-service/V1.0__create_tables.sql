-- ============================================
-- REVIEW SERVICE - Database Migration V1.0
-- Tách ReviewEntity thành 2 entities: QuestionEntity và ProductReviewEntity
-- ============================================

-- ============================================
-- 1. QUESTIONS TABLE (Q&A)
-- ============================================
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_avatar VARCHAR(255),

    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,

    category_id BIGINT,

    like_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    answer_count INT NOT NULL DEFAULT 0,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    rejection_reason VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN questions.user_id IS 'ID người đăng câu hỏi';
COMMENT ON COLUMN questions.user_name IS 'Tên người đăng';
COMMENT ON COLUMN questions.user_avatar IS 'URL avatar';
COMMENT ON COLUMN questions.title IS 'Tiêu đề câu hỏi';
COMMENT ON COLUMN questions.content IS 'Nội dung chi tiết';
COMMENT ON COLUMN questions.category_id IS 'ID danh mục (Laptop, Gaming, PC...)';
COMMENT ON COLUMN questions.like_count IS 'Số like';
COMMENT ON COLUMN questions.view_count IS 'Số lượt xem';
COMMENT ON COLUMN questions.answer_count IS 'Số câu trả lời';
COMMENT ON COLUMN questions.status IS 'PENDING, APPROVED, REJECTED, CLOSED';
COMMENT ON COLUMN questions.is_verified IS 'Câu hỏi được xác minh';
COMMENT ON COLUMN questions.is_featured IS 'Câu hỏi nổi bật';
COMMENT ON COLUMN questions.rejection_reason IS 'Lý do từ chối';

CREATE INDEX IF NOT EXISTS idx_question_user_id ON questions(user_id);
CREATE INDEX IF NOT EXISTS idx_question_category_id ON questions(category_id);
CREATE INDEX IF NOT EXISTS idx_question_status ON questions(status);
CREATE INDEX IF NOT EXISTS idx_question_created_at ON questions(created_at);

-- ============================================
-- 2. PRODUCT_REVIEWS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_avatar VARCHAR(255),

    content TEXT NOT NULL,

    product_id UUID NOT NULL,
    product_name VARCHAR(255),
    rating DOUBLE PRECISION NOT NULL,

    like_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    rejection_reason VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments for product_reviews
COMMENT ON COLUMN product_reviews.user_id IS 'ID người đăng review';
COMMENT ON COLUMN product_reviews.user_name IS 'Tên người đăng';
COMMENT ON COLUMN product_reviews.user_avatar IS 'URL avatar';
COMMENT ON COLUMN product_reviews.content IS 'Nội dung đánh giá';
COMMENT ON COLUMN product_reviews.product_id IS 'ID sản phẩm (UUID)';
COMMENT ON COLUMN product_reviews.product_name IS 'Tên sản phẩm (cache)';
COMMENT ON COLUMN product_reviews.rating IS 'Số sao: 1.0 - 5.0';
COMMENT ON COLUMN product_reviews.like_count IS 'Số like';
COMMENT ON COLUMN product_reviews.view_count IS 'Số lượt xem';
COMMENT ON COLUMN product_reviews.status IS 'PENDING, APPROVED, REJECTED';
COMMENT ON COLUMN product_reviews.is_verified IS 'Đánh giá được xác minh (đã mua hàng)';
COMMENT ON COLUMN product_reviews.is_featured IS 'Đánh giá nổi bật';
COMMENT ON COLUMN product_reviews.rejection_reason IS 'Lý do từ chối';

-- Indexes for product_reviews
CREATE INDEX idx_product_review_product_id ON product_reviews(product_id);
CREATE INDEX idx_product_review_user_id ON product_reviews(user_id);
CREATE INDEX idx_product_review_status ON product_reviews(status);
CREATE INDEX idx_product_review_rating ON product_reviews(rating);
CREATE INDEX idx_product_review_created_at ON product_reviews(created_at);

-- ============================================
-- 3. PRODUCT_REVIEW_IMAGES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS product_review_images (
    id BIGSERIAL PRIMARY KEY,
    product_review_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE
);

COMMENT ON COLUMN product_review_images.product_review_id IS 'ID product review';
COMMENT ON COLUMN product_review_images.image_url IS 'URL ảnh';
COMMENT ON COLUMN product_review_images.display_order IS 'Thứ tự hiển thị';

CREATE INDEX IF NOT EXISTS idx_product_review_image_review_id ON product_review_images(product_review_id);

-- ============================================
-- 4. REVIEW_CATEGORIES TABLE (shared)
-- ============================================
CREATE TABLE IF NOT EXISTS review_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100),
    description VARCHAR(255),
    icon VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0
);

COMMENT ON COLUMN review_categories.name IS 'Tên danh mục';
COMMENT ON COLUMN review_categories.slug IS 'Slug';
COMMENT ON COLUMN review_categories.description IS 'Mô tả';
COMMENT ON COLUMN review_categories.icon IS 'Icon URL hoặc emoji';

CREATE INDEX IF NOT EXISTS idx_category_slug ON review_categories(slug);

-- ============================================
-- 5. TAGS TABLE (shared)
-- ============================================
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50),
    usage_count INT NOT NULL DEFAULT 0
);

COMMENT ON COLUMN tags.name IS 'Tên tag';
COMMENT ON COLUMN tags.slug IS 'Slug';
COMMENT ON COLUMN tags.usage_count IS 'Số lần sử dụng';

CREATE INDEX IF NOT EXISTS idx_tag_slug ON tags(slug);
CREATE INDEX IF NOT EXISTS idx_tag_usage_count ON tags(usage_count);

-- ============================================
-- 6. QUESTION_TAGS TABLE (join table)
-- ============================================
CREATE TABLE IF NOT EXISTS question_tags (
    question_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- ============================================
-- 7. PRODUCT_REVIEW_TAGS TABLE (join table)
-- ============================================
CREATE TABLE IF NOT EXISTS product_review_tags (
    product_review_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (product_review_id, tag_id),
    FOREIGN KEY (product_review_id) REFERENCES product_reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- ============================================
-- 8. ANSWERS TABLE (updated - link to questions)
-- ============================================
CREATE TABLE IF NOT EXISTS answers (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_avatar VARCHAR(255),

    content TEXT NOT NULL,

    like_count INT NOT NULL DEFAULT 0,
    dislike_count INT NOT NULL DEFAULT 0,

    is_best_answer BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_from_shop BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

COMMENT ON COLUMN answers.question_id IS 'ID câu hỏi';
COMMENT ON COLUMN answers.user_id IS 'ID người trả lời';
COMMENT ON COLUMN answers.user_name IS 'Tên người trả lời';
COMMENT ON COLUMN answers.user_avatar IS 'URL avatar';
COMMENT ON COLUMN answers.content IS 'Nội dung câu trả lời';
COMMENT ON COLUMN answers.is_best_answer IS 'Câu trả lời được chọn';
COMMENT ON COLUMN answers.is_verified IS 'Từ chuyên gia';
COMMENT ON COLUMN answers.is_from_shop IS 'Phản hồi từ shop';

CREATE INDEX IF NOT EXISTS idx_question_id ON answers(question_id);

-- ============================================
-- 9. REVIEW_REACTIONS TABLE (updated - support QUESTION, PRODUCT_REVIEW, ANSWER)
-- ============================================
CREATE TABLE IF NOT EXISTS review_reactions (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (target_type, target_id, user_id)
);

COMMENT ON COLUMN review_reactions.target_type IS 'QUESTION, PRODUCT_REVIEW, ANSWER';
COMMENT ON COLUMN review_reactions.target_id IS 'ID của target';
COMMENT ON COLUMN review_reactions.user_id IS 'ID người react';
COMMENT ON COLUMN review_reactions.type IS 'LIKE, DISLIKE, HELPFUL';

CREATE INDEX IF NOT EXISTS idx_reaction_target ON review_reactions(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_reaction_user ON review_reactions(user_id);

-- ============================================
-- SEED DATA for categories
-- ============================================
INSERT INTO review_categories (name, slug, description, icon, is_active, display_order) VALUES
('Laptop', 'laptop', 'Câu hỏi về laptop', '💻', TRUE, 1),
('PC Gaming', 'pc-gaming', 'Câu hỏi về PC Gaming', '🎮', TRUE, 2),
('Phần cứng', 'phan-cung', 'Câu hỏi về linh kiện phần cứng', '🔧', TRUE, 3),
('Phần mềm', 'phan-mem', 'Câu hỏi về phần mềm', '💿', TRUE, 4),
('Tư vấn', 'tu-van', 'Tư vấn chung', '💡', TRUE, 5)
ON CONFLICT (name) DO NOTHING;
