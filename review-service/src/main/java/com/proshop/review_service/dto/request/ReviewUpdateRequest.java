package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewUpdateRequest {

    @Size(max = 500)
    private String title;

    @Size(min = 10, max = 5000)
    private String content;

    @Min(1) @Max(5)
    private Double rating;

    private List<String> imageUrls;

    private Long categoryId;

    private List<String> tags;
}