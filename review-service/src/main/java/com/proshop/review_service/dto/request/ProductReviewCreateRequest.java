package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO - Tạo đánh giá sản phẩm mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReviewCreateRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId; // ID sản phẩm

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content; // Nội dung đánh giá

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating; // Số sao (1-5)

    private List<String> imageUrls = new ArrayList<>(); // URLs ảnh upload

    private List<String> tags = new ArrayList<>(); // Tags
}
