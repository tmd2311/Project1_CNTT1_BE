package com.proshop.review_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerCreateRequest {

    @NotNull(message = "Review ID is required")
    private Long reviewId;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 3000, message = "Content must be between 10 and 3000 characters")
    private String content;
}
