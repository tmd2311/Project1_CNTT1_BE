package com.proshop.review_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers", indexes = {
        @Index(name = "idx_review_id", columnList = "review_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review; // Câu hỏi/Review gốc

    // ============================================
    // THÔNG TIN NGƯỜI TRẢ LỜI
    // ============================================
    @Column(nullable = false)
    private Long userId; // ID người trả lời

    @Column(nullable = false, length = 100)
    private String userName; // Trần Thị Bình

    @Column(length = 255)
    private String userAvatar; // URL avatar

    // ============================================
    // NỘI DUNG
    // ============================================
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung câu trả lời

    // ============================================
    // TƯƠNG TÁC
    // ============================================
    @Column(name = "dislike_count", nullable = false)
    private Integer dislikeCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    // ============================================
    // ĐÁNH DẤU ĐẶC BIỆT
    // ============================================
    @Column(name = "is_best_answer", nullable = false)
    private Boolean isBestAnswer = false; // Câu trả lời được chọn

    @Column(name = "is_from_shop", nullable = false)
    private Boolean isVerified = false; // Từ chuyên gia

    @Column(name = "is_verified", nullable = false)
    private Boolean isFromShop = false; // Phản hồi từ shop

    // ============================================
    // TIMESTAMPS
    // ============================================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 2024-01-15

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}