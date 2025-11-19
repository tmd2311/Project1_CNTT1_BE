package com.proshop.review_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSearchRequest {

    private String keyword; // Tìm trong title và content
    private String type; // QA hoặc PRODUCT_REVIEW
    private Long productId;
    private Long categoryId;
    private List<String> tags;
    private Double minRating;
    private Double maxRating;
    private String status;
    private String sortBy; // createdAt, likeCount, viewCount, answerCount
    private String sortOrder; // ASC, DESC

    @Min(0)
    private Integer page = 0;

    @Min(1) @Max(100)
    private Integer size = 20;
}