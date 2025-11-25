package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO - Tạo câu hỏi mới (Q&A)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 500, message = "Title must be between 10 and 500 characters")
    private String title; // Tiêu đề câu hỏi

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content; // Nội dung chi tiết

    private Long categoryId; // ID danh mục (Laptop, Gaming, PC...)

    private List<String> tags = new ArrayList<>(); // Tags
}
