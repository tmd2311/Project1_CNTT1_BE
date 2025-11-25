package com.proshop.review_service.mapper;

import com.proshop.review_service.dto.request.AnswerCreateRequest;
import com.proshop.review_service.dto.request.AnswerUpdateRequest;
import com.proshop.review_service.dto.request.CategoryRequest;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.CategoryResponse;
import com.proshop.review_service.dto.response.TagResponse;
import com.proshop.review_service.entity.AnswerEntity;
import com.proshop.review_service.entity.QuestionEntity;
import com.proshop.review_service.entity.ReviewCategoryEntity;
import com.proshop.review_service.entity.TagEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper cho Answer (câu trả lời cho Question)
 * Note: ReviewMapper được giữ lại để map Answer, Category, Tag - không còn map Review nữa
 */
@Component
public class ReviewMapper {

    // ============================================
    // ANSWER MAPPINGS (for Question)
    // ============================================

    public AnswerEntity toEntity(AnswerCreateRequest request, QuestionEntity question, Long userId, String userName, String userAvatar) {
        return AnswerEntity.builder()
                .question(question)
                .content(request.getContent())
                .userId(userId)
                .userName(userName)
                .userAvatar(userAvatar)
                .build();
    }

    public AnswerResponse toAnswerResponse(AnswerEntity answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .reviewId(answer.getQuestion().getId()) // questionId mapped to reviewId for backward compatibility
                .userId(answer.getUserId())
                .userName(answer.getUserName())
                .userAvatar(answer.getUserAvatar())
                .content(answer.getContent())
                .likeCount(answer.getLikeCount())
                .dislikeCount(answer.getDislikeCount())
                .isBestAnswer(answer.getIsBestAnswer())
                .isVerified(answer.getIsVerified())
                .isFromShop(answer.getIsFromShop())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }

    public void updateAnswerEntity(AnswerEntity answer, AnswerUpdateRequest request) {
        if (request.getContent() != null) {
            answer.setContent(request.getContent());
        }
    }

    public List<AnswerResponse> toAnswerResponseList(List<AnswerEntity> answers) {
        return answers.stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // CATEGORY MAPPINGS
    // ============================================

    public ReviewCategoryEntity toEntity(CategoryRequest request) {
        return ReviewCategoryEntity.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .icon(request.getIcon())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(true)
                .build();
    }

    public CategoryResponse toCategoryResponse(ReviewCategoryEntity category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .icon(category.getIcon())
                .isActive(category.getIsActive())
                .displayOrder(category.getDisplayOrder())
                .build();
    }

    public void updateCategoryEntity(ReviewCategoryEntity category, CategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getSlug() != null) {
            category.setSlug(request.getSlug());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public List<CategoryResponse> toCategoryResponseList(List<ReviewCategoryEntity> categories) {
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // TAG MAPPINGS
    // ============================================

    public TagEntity toEntity(String name) {
        return TagEntity.builder()
                .name(name)
                .slug(generateSlug(name))
                .usageCount(0)
                .build();
    }

    public TagResponse toTagResponse(TagEntity tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .usageCount(tag.getUsageCount())
                .build();
    }

    public List<TagResponse> toTagResponseList(List<TagEntity> tags) {
        return tags.stream()
                .map(this::toTagResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
