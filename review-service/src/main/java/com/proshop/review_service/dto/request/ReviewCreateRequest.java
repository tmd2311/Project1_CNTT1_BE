package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

// ============================================
// REVIEW DTOs
// ============================================

// Request DTO - Tạo Review mới
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewCreateRequest {

    @NotNull(message = "Review type is required")
    private String type; // "QA" hoặc "PRODUCT_REVIEW"

    // Cho Q&A
    @Size(max = 500, message = "Title must be less than 500 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    // Cho Product Review
    private Long productId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating;

    private List<String> imageUrls = new ArrayList<>(); // URLs ảnh upload

    // Category (cho Q&A)
    private Long categoryId;

    // Tags
    private List<String> tags = new ArrayList<>();
}
