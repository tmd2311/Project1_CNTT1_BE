package com.proshop.review_service.dto.response;

import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO - Tóm tắt câu hỏi (không bao gồm answers)
 * Dùng cho danh sách câu hỏi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSummaryResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;

    private String title;
    private String content; // Có thể truncate trong service

    private CategoryResponse category;

    private Integer likeCount;
    private Integer viewCount;
    private Integer answerCount;

    private ReviewStatus status;
    private Boolean isVerified;
    private Boolean isFeatured;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TagResponse> tags = new ArrayList<>();
}
