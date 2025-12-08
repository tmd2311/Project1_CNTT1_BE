package com.proshop.review_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity cho ảnh đánh giá sản phẩm
 */
@Entity
@Table(name = "product_review_images", indexes = {
        @Index(name = "idx_product_review_image_review_id", columnList = "product_review_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_review_id", nullable = false)
    private ProductReviewEntity productReview;

    @Column(nullable = false, length = 500)
    private String imageUrl; // URL ảnh

    @Column(nullable = false)
    private Integer displayOrder = 0; // Thứ tự hiển thị

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
