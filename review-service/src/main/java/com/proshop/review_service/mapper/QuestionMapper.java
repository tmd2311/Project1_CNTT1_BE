package com.proshop.review_service.mapper;

import com.proshop.review_service.dto.request.QuestionCreateRequest;
import com.proshop.review_service.dto.request.QuestionUpdateRequest;
import com.proshop.review_service.dto.response.*;
import com.proshop.review_service.entity.QuestionEntity;
import com.proshop.review_service.entity.ReviewCategoryEntity;
import com.proshop.review_service.entity.TagEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper cho QuestionEntity (Q&A)
 */
@Component
public class QuestionMapper {

    // ============================================
    // QUESTION MAPPINGS
    // ============================================

    public QuestionEntity toEntity(QuestionCreateRequest request, Long userId, String userName, String userAvatar) {
        return QuestionEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(userId)
                .userName(userName)
                .userAvatar(userAvatar)
                .build();
    }

    public QuestionResponse toResponse(QuestionEntity question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .userId(question.getUserId())
                .userName(question.getUserName())
                .userAvatar(question.getUserAvatar())
                .title(question.getTitle())
                .content(question.getContent())
                .category(question.getCategory() != null ? toCategoryResponse(question.getCategory()) : null)
                .likeCount(question.getLikeCount())
                .viewCount(question.getViewCount())
                .answerCount(question.getAnswerCount())
                .status(question.getStatus())
                .isVerified(question.getIsVerified())
                .isFeatured(question.getIsFeatured())
                .rejectionReason(question.getRejectionReason())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .tags(question.getTags() != null ?
                        question.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : List.of())
                .build();
    }

    public QuestionSummaryResponse toSummaryResponse(QuestionEntity question) {
        String truncatedContent = question.getContent();
        if (truncatedContent != null && truncatedContent.length() > 200) {
            truncatedContent = truncatedContent.substring(0, 197) + "...";
        }

        return QuestionSummaryResponse.builder()
                .id(question.getId())
                .userId(question.getUserId())
                .userName(question.getUserName())
                .userAvatar(question.getUserAvatar())
                .title(question.getTitle())
                .content(truncatedContent)
                .category(question.getCategory() != null ? toCategoryResponse(question.getCategory()) : null)
                .likeCount(question.getLikeCount())
                .viewCount(question.getViewCount())
                .answerCount(question.getAnswerCount())
                .status(question.getStatus())
                .isVerified(question.getIsVerified())
                .isFeatured(question.getIsFeatured())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .tags(question.getTags() != null ?
                        question.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : List.of())
                .build();
    }

    public void updateEntity(QuestionEntity question, QuestionUpdateRequest request) {
        if (request.getTitle() != null) {
            question.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
    }

    public List<QuestionSummaryResponse> toSummaryResponseList(List<QuestionEntity> questions) {
        return questions.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // HELPER MAPPINGS
    // ============================================

    private CategoryResponse toCategoryResponse(ReviewCategoryEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .icon(category.getIcon())
                .build();
    }

    private TagResponse toTagResponse(TagEntity tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .usageCount(tag.getUsageCount())
                .build();
    }

    public TagEntity toEntity(String tagName) {
        return TagEntity.builder()
                .name(tagName)
                .usageCount(0)
                .build();
    }
}
