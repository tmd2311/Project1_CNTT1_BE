package com.proshop.review_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Request DTO - Cập nhật câu hỏi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionUpdateRequest {

    @Size(min = 10, max = 500, message = "Title must be between 10 and 500 characters")
    private String title;

    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    private Long categoryId;

    private List<String> tags;
}
