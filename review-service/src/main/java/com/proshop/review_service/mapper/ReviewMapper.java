package com.proshop.review_service.mapper;


import com.proshop.review_service.dto.request.*;
import com.proshop.review_service.dto.response.*;
import com.proshop.review_service.entity.*;
import com.proshop.review_service.util.enums.ReviewType;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.Collectors;


@Component
public class ReviewMapper {

    // ============================================
    // REVIEW MAPPINGS
    // ============================================

    public ReviewEntity toEntity(ReviewCreateRequest request, Long userId, String userName, String userAvatar) {
        ReviewEntity review = ReviewEntity.builder()
                .type(ReviewType.valueOf(request.getType()))
                .title(request.getTitle())
                .content(request.getContent())
                .userId(userId)
                .userName(userName)
                .userAvatar(userAvatar)
                .productId(request.getProductId() != null ? request.getProductId() : 0L)
                .rating(request.getRating())
                .build();

        return review;
    }

    public ReviewResponse toResponse(ReviewEntity review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .type(review.getType().name())
                .title(review.getTitle())
                .content(review.getContent())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .productId(review.getProductId())
                .productName(review.getProductName())
                .rating(review.getRating())
                .images(review.getImages() != null ?
                        review.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) : null)
                .category(review.getCategory() != null ? toCategoryResponse(review.getCategory()) : null)
                .likeCount(review.getLikeCount())
                .viewCount(review.getViewCount())
                .answerCount(review.getAnswerCount())
                .status(review.getStatus().name())
                .isVerified(review.getIsVerified())
                .isFeatured(review.getIsFeatured())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .tags(review.getTags() != null ?
                        review.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : null)
                .build();
    }

    public ReviewSummaryResponse toSummaryResponse(ReviewEntity review) {
        String truncatedContent = review.getContent();
        if (truncatedContent != null && truncatedContent.length() > 200) {
            truncatedContent = truncatedContent.substring(0, 197) + "...";
        }

        return ReviewSummaryResponse.builder()
                .id(review.getId())
                .type(review.getType().name())
                .title(review.getTitle())
                .content(truncatedContent)
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .productId(review.getProductId())
                .productName(review.getProductName())
                .rating(review.getRating())
                .category(review.getCategory() != null ? toCategoryResponse(review.getCategory()) : null)
                .likeCount(review.getLikeCount())
                .viewCount(review.getViewCount())
                .answerCount(review.getAnswerCount())
                .createdAt(review.getCreatedAt())
                .tags(review.getTags() != null ?
                        review.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : null)
                .build();
    }

    public void updateEntity(ReviewEntity review, ReviewUpdateRequest request) {
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
    }

    // ============================================
    // ANSWER MAPPINGS
    // ============================================

    public AnswerEntity toEntity(AnswerCreateRequest request, ReviewEntity review, Long userId, String userName, String userAvatar) {
        return AnswerEntity.builder()
                .review(review)
                .content(request.getContent())
                .userId(userId)
                .userName(userName)
                .userAvatar(userAvatar)
                .build();
    }

    public AnswerResponse toAnswerResponse(AnswerEntity answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .reviewId(answer.getReview().getId())
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

    // ============================================
    // IMAGE MAPPINGS
    // ============================================

    public ReviewImageEntity toImageEntity(String imageUrl, ReviewEntity review, int order) {
        return ReviewImageEntity.builder()
                .imageUrl(imageUrl)
                .review(review)
                .displayOrder(order)
                .build();
    }

    public ReviewImageResponse toImageResponse(ReviewImageEntity image) {
        return ReviewImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .build();
    }

    // ============================================
    // LIST MAPPINGS
    // ============================================

    public List<ReviewResponse> toResponseList(List<ReviewEntity> reviews) {
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryResponse> toSummaryResponseList(List<ReviewEntity> reviews) {
        return reviews.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<AnswerResponse> toAnswerResponseList(List<AnswerEntity> answers) {
        return answers.stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> toCategoryResponseList(List<ReviewCategoryEntity> categories) {
        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
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