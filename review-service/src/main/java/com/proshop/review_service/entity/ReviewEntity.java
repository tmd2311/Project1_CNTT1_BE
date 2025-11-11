package com.proshop.review_service.entity;

import com.proshop.review_service.util.enums.ReviewStatus;
import com.proshop.review_service.util.enums.ReviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ============================================
// 1. REVIEW ENTITY (Hỗ trợ cả Q&A và Product Review)
// ============================================
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_product_id", columnList = "productId"),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // PHÂN LOẠI: Q&A hay Product Review
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType type; // QA hoặc PRODUCT_REVIEW

    // ============================================
    // THÔNG TIN NGƯỜI DÙNG
    // ============================================
    @Column(nullable = false)
    private Long userId; // ID người đăng

    @Column(nullable = false, length = 100)
    private String userName; // Nguyễn Văn Dũng

    @Column(length = 255)
    private String userAvatar; // URL avatar

    // ============================================
    // NỘI DUNG REVIEW
    // ============================================
    @Column(length = 500)
    private String title; // Tiêu đề (cho Q&A) - "Laptop nào phù hợp học tập?"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung chi tiết

    // ============================================
    // ĐÁNH GIÁ SẢN PHẨM (Product Review only)
    // ============================================
    @Column(nullable = false)
    private Long productId = 0L; // ID sản phẩm (0 nếu là Q&A)

    private String productName; // Tên sản phẩm (cache)

    @Column(precision = 2, scale = 1)
    private Double rating; // Số sao: 1.0 - 5.0 (null nếu là Q&A)

    // Ảnh thực tế sản phẩm (Product Review only)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImageEntity> images = new ArrayList<>();

    // ============================================
    // DANH MỤC (cho Q&A)
    // ============================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ReviewCategoryEntity category; // Laptop, Gaming, PC...

    // ============================================
    // TƯƠNG TÁC
    // ============================================
    @Column(nullable = false)
    private Integer likeCount = 0; // Số like

    @Column(nullable = false)
    private Integer viewCount = 0; // Số lượt xem (245 lượt xem)

    @Column(nullable = false)
    private Integer answerCount = 0; // Số câu trả lời (2)

    // ============================================
    // CÁC CÂU TRẢ LỜI
    // ============================================
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<AnswerEntity> answers = new ArrayList<>();

    // ============================================
    // TRẠNG THÁI
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PENDING; // PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private Boolean isVerified = false; // Review đã xác thực (mua hàng)

    @Column(nullable = false)
    private Boolean isFeatured = false; // Review nổi bật

    // ============================================
    // TIMESTAMPS
    // ============================================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 2024-01-15

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ============================================
    // TAGS (optional)
    // ============================================
    @ManyToMany
    @JoinTable(
            name = "review_tags",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags = new ArrayList<>();
}