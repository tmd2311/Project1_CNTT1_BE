package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Request DTO - Cập nhật đánh giá sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReviewUpdateRequest {

    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating;

    private List<String> imageUrls;

    private List<String> tags;
}
