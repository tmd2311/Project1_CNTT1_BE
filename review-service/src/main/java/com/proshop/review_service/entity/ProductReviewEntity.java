package com.proshop.review_service.entity;

import com.proshop.review_service.util.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity cho đánh giá sản phẩm (Product Review)
 * Liên quan đến sản phẩm cụ thể
 */
@Entity
@Table(name = "product_reviews", indexes = {
        @Index(name = "idx_product_review_product_id", columnList = "productId"),
        @Index(name = "idx_product_review_user_id", columnList = "userId"),
        @Index(name = "idx_product_review_status", columnList = "status"),
        @Index(name = "idx_product_review_rating", columnList = "rating"),
        @Index(name = "idx_product_review_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    // NỘI DUNG ĐÁNH GIÁ
    // ============================================
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung đánh giá

    // ============================================
    // THÔNG TIN SẢN PHẨM
    // ============================================
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID productId; // ID sản phẩm

    @Column(length = 255)
    private String productName; // Tên sản phẩm (cache)

    @Column(nullable = false)
    private Double rating; // Số sao: 1.0 - 5.0

    // ============================================
    // ẢNH THỰC TẾ SẢN PHẨM
    // ============================================
    @OneToMany(mappedBy = "productReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<ProductReviewImageEntity> images = new ArrayList<>();

    // ============================================
    // TƯƠNG TÁC
    // ============================================
    @Column(nullable = false)
    private Integer likeCount = 0; // Số like

    @Column(nullable = false)
    private Integer viewCount = 0; // Số lượt xem

    // ============================================
    // TRẠNG THÁI
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PENDING; // PENDING, APPROVED, REJECTED

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false; // Đánh giá được xác minh (đã mua hàng)

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false; // Đánh giá nổi bật

    @Column(length = 500)
    private String rejectionReason; // Lý do từ chối

    // ============================================
    // TIMESTAMPS
    // ============================================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ============================================
    // TAGS
    // ============================================
    @ManyToMany
    @JoinTable(
            name = "product_review_tags",
            joinColumns = @JoinColumn(name = "product_review_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags = new ArrayList<>();
}
