package com.proshop.review_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionRequest {

    @NotNull(message = "Target type is required")
    private String targetType; // "REVIEW" hoặc "ANSWER"

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "Reaction type is required")
    private String type; // "LIKE", "DISLIKE", "HELPFUL"
}