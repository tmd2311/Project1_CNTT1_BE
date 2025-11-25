package com.proshop.review_service.dto.response;

import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO - Chi tiết đánh giá sản phẩm đầy đủ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;

    private String content;

    private UUID productId;
    private String productName;
    private Double rating;

    private List<ReviewImageResponse> images = new ArrayList<>();

    private Integer likeCount;
    private Integer viewCount;

    private ReviewStatus status;
    private Boolean isVerified;
    private Boolean isFeatured;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TagResponse> tags = new ArrayList<>();
}
