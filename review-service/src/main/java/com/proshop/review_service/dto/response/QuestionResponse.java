package com.proshop.review_service.dto.response;

import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO - Chi tiết câu hỏi đầy đủ (bao gồm cả answers)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;

    private String title;
    private String content;

    private CategoryResponse category;

    private Integer likeCount;
    private Integer viewCount;
    private Integer answerCount;

    private List<AnswerResponse> answers = new ArrayList<>();

    private ReviewStatus status;
    private Boolean isVerified;
    private Boolean isFeatured;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TagResponse> tags = new ArrayList<>();
}
