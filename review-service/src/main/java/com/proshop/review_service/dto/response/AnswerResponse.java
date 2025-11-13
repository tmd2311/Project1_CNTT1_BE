package com.proshop.review_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long id;
    private Long reviewId;

    private Long userId;
    private String userName;
    private String userAvatar;

    private String content;

    private Integer likeCount;
    private Integer dislikeCount;

    private Boolean isBestAnswer;
    private Boolean isVerified;
    private Boolean isFromShop;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}