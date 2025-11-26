package com.proshop.review_service.dto.response;

import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO - Tóm tắt đánh giá sản phẩm
 * Dùng cho danh sách đánh giá
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewSummaryResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;

    private String content; // Có thể truncate trong service

    private UUID productId;
    private String productName;
    private Double rating;

    private List<ReviewImageResponse> images = new ArrayList<>();

    private Integer likeCount;
    private Integer viewCount;

    private ReviewStatus status;
    private Boolean isVerified;
    private Boolean isFeatured;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TagResponse> tags = new ArrayList<>();
}
