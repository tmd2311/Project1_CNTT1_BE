package com.proshop.review_service.mapper;

import com.proshop.review_service.dto.request.ProductReviewCreateRequest;
import com.proshop.review_service.dto.request.ProductReviewUpdateRequest;
import com.proshop.review_service.dto.response.*;
import com.proshop.review_service.entity.ProductReviewEntity;
import com.proshop.review_service.entity.ProductReviewImageEntity;
import com.proshop.review_service.entity.TagEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper cho ProductReviewEntity
 */
@Component
public class ProductReviewMapper {

    // ============================================
    // PRODUCT REVIEW MAPPINGS
    // ============================================

    public ProductReviewEntity toEntity(ProductReviewCreateRequest request, Long userId, String userName, String userAvatar) {
        return ProductReviewEntity.builder()
                .content(request.getContent())
                .productId(request.getProductId())
                .rating(request.getRating())
                .userId(userId)
                .userName(userName)
                .userAvatar(userAvatar)
                .build();
    }

    public ProductReviewResponse toResponse(ProductReviewEntity review) {
        return ProductReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .content(review.getContent())
                .productId(review.getProductId())
                .productName(review.getProductName())
                .rating(review.getRating())
                .images(review.getImages() != null ?
                        review.getImages().stream().map(this::toImageResponse).collect(Collectors.toList()) : List.of())
                .likeCount(review.getLikeCount())
                .viewCount(review.getViewCount())
                .status(review.getStatus())
                .isVerified(review.getIsVerified())
                .isFeatured(review.getIsFeatured())
                .rejectionReason(review.getRejectionReason())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .tags(review.getTags() != null ?
                        review.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : List.of())
                .build();
    }

    public ProductReviewSummaryResponse toSummaryResponse(ProductReviewEntity review) {
        String truncatedContent = review.getContent();
        if (truncatedContent != null && truncatedContent.length() > 200) {
            truncatedContent = truncatedContent.substring(0, 197) + "...";
        }

        return ProductReviewSummaryResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .userAvatar(review.getUserAvatar())
                .content(truncatedContent)
                .productId(review.getProductId())
                .productName(review.getProductName())
                .rating(review.getRating())
                .images(review.getImages() != null && !review.getImages().isEmpty() ?
                        review.getImages().stream().limit(3).map(this::toImageResponse).collect(Collectors.toList()) : List.of())
                .likeCount(review.getLikeCount())
                .viewCount(review.getViewCount())
                .status(review.getStatus())
                .isVerified(review.getIsVerified())
                .isFeatured(review.getIsFeatured())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .tags(review.getTags() != null ?
                        review.getTags().stream().map(this::toTagResponse).collect(Collectors.toList()) : List.of())
                .build();
    }

    public void updateEntity(ProductReviewEntity review, ProductReviewUpdateRequest request) {
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
    }

    public List<ProductReviewSummaryResponse> toSummaryResponseList(List<ProductReviewEntity> reviews) {
        return reviews.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // IMAGE MAPPINGS
    // ============================================

    public ProductReviewImageEntity toImageEntity(String imageUrl, ProductReviewEntity review, int order) {
        return ProductReviewImageEntity.builder()
                .imageUrl(imageUrl)
                .productReview(review)
                .displayOrder(order)
                .build();
    }

    private ReviewImageResponse toImageResponse(ProductReviewImageEntity image) {
        return ReviewImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .build();
    }

    // ============================================
    // HELPER MAPPINGS
    // ============================================

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
