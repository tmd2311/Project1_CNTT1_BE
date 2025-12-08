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
public class ReviewSummaryResponse {

    private Long id;
    private String type;
    private String title;
    private String content; // Có thể truncate

    private Long userId;
    private String userName;
    private String userAvatar;

    private Long productId;
    private String productName;
    private Double rating;

    private CategoryResponse category;

    private Integer likeCount;
    private Integer viewCount;
    private Integer answerCount;

    private LocalDateTime createdAt;

    private List<TagResponse> tags;
}