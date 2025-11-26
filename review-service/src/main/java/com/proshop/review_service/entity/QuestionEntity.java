package com.proshop.review_service.entity;

import com.proshop.review_service.util.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity cho câu hỏi (Q&A)
 * Không liên quan đến sản phẩm cụ thể
 */
@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_user_id", columnList = "userId"),
        @Index(name = "idx_question_category_id", columnList = "category_id"),
        @Index(name = "idx_question_status", columnList = "status"),
        @Index(name = "idx_question_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEntity {

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
    // NỘI DUNG CÂU HỎI
    // ============================================
    @Column(nullable = false, length = 500)
    private String title; // Tiêu đề câu hỏi - "Laptop nào phù hợp học tập?"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Nội dung chi tiết

    // ============================================
    // DANH MỤC
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
    private Integer viewCount = 0; // Số lượt xem

    @Column(nullable = false)
    private Integer answerCount = 0; // Số câu trả lời

    // ============================================
    // CÁC CÂU TRẢ LỜI
    // ============================================
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<AnswerEntity> answers = new ArrayList<>();

    // ============================================
    // TRẠNG THÁI
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PENDING; // PENDING, APPROVED, REJECTED, CLOSED

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false; // Câu hỏi được xác minh

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false; // Câu hỏi nổi bật

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
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags = new ArrayList<>();
}
