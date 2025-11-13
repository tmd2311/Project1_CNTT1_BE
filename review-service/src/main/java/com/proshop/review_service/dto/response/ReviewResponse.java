package com.proshop.review_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private String type;
    private String title;
    private String content;

    // User info
    private Long userId;
    private String userName;
    private String userAvatar;

    // Product info (nếu là Product Review)
    private Long productId;
    private String productName;
    private Double rating;
    private List<ReviewImageResponse> images;

    // Category (nếu là Q&A)
    private CategoryResponse category;

    // Stats
    private Integer likeCount;
    private Integer viewCount;
    private Integer answerCount;

    // Status
    private String status;
    private Boolean isVerified;
    private Boolean isFeatured;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tags
    private List<TagResponse> tags;

    // Answers (optional, có thể load riêng)
    private List<AnswerResponse> answers;
}