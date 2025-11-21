package com.proshop.review_service.service.impl;


import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.dto.request.*;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ReviewResponse;
import com.proshop.review_service.dto.response.ReviewSummaryResponse;
import com.proshop.review_service.entity.*;
import com.proshop.review_service.mapper.ReviewMapper;
import com.proshop.review_service.repository.*;
import com.proshop.review_service.service.ReviewService;
import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import com.proshop.review_service.util.enums.ReviewStatus;
import com.proshop.review_service.util.enums.ReviewType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AnswerRepository answerRepository;
    private final ReviewCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ReviewImageRepository imageRepository;
    private final ReviewReactionRepository reactionRepository;
    private final ReviewMapper mapper;

    // ============================================
    // REVIEW CRUD
    // ============================================

    public ReviewResponse createReview(ReviewCreateRequest request, Long userId, String userName, String userAvatar) {
        log.info("Creating review for user: {}", userId);

        // Convert to entity
        ReviewEntity review = mapper.toEntity(request, userId, userName, userAvatar);
        review.setStatus(ReviewStatus.PENDING);
        review.setAnswerCount(0);
        review.setLikeCount(0);
        review.setViewCount(0);

        // Set category
        if (request.getCategoryId() != null) {
            ReviewCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            review.setCategory(category);
        }

        // Save review
        review = reviewRepository.save(review);

        // Add images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int order = 0;
            for (String imageUrl : request.getImageUrls()) {
                ReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Add tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<TagEntity> tags = getOrCreateTags(request.getTags());
            review.setTags(tags);
            review = reviewRepository.save(review);

            // Increment tag usage
            tags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        return mapper.toResponse(review);
    }

    @Transactional
    public ReviewResponse getReviewById(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Increment view count
        reviewRepository.incrementViewCount(id);

        // Load answers
        List<AnswerEntity> answers = answerRepository.findByReview_IdOrderByCreatedAtAsc(id);

        ReviewResponse response = mapper.toResponse(review);
        response.setAnswers(mapper.toAnswerResponseList(answers));

        return response;
    }

    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request, Long userId) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check ownership
        if (!review.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền cập nhật review này");
        }

        mapper.updateEntity(review, request);

        // Update category
        if (request.getCategoryId() != null) {
            ReviewCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            review.setCategory(category);
        }

        // Update images
        if (request.getImageUrls() != null) {
            imageRepository.deleteByReview_Id(id);
            int order = 0;
            for (String imageUrl : request.getImageUrls()) {
                ReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Update tags
        if (request.getTags() != null) {
            // Decrement old tags
            review.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

            // Add new tags
            List<TagEntity> newTags = getOrCreateTags(request.getTags());
            review.setTags(newTags);
            newTags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        review = reviewRepository.save(review);
        return mapper.toResponse(review);
    }

    public void deleteReview(Long id, Long userId) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy review"));

        if (!review.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền xóa review này");
        }

        // Decrement tag usage
        review.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

        reviewRepository.delete(review);
    }

    // ============================================
    // REVIEW LISTING & SEARCH
    // ============================================

    @Transactional
    public PageResponse<ReviewSummaryResponse> searchReviews(ReviewSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortOrder() != null ? request.getSortOrder() : "DESC"),
                        request.getSortBy() != null ? request.getSortBy() : "createdAt")
        );

        Page<ReviewEntity> page;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            page = reviewRepository.searchByKeyword(request.getKeyword(), pageable);
        } else if (request.getProductId() != null) {
            ReviewType type = request.getType() != null ? ReviewType.valueOf(request.getType()) : ReviewType.PRODUCT_REVIEW;
            page = reviewRepository.findByProductIdAndTypeAndStatus(
                    request.getProductId(), type, ReviewStatus.APPROVED, pageable);
        } else if (request.getCategoryId() != null) {
            page = reviewRepository.findByCategory_Id(request.getCategoryId(), pageable);
        } else if (request.getType() != null) {
            page = reviewRepository.findByType(ReviewType.valueOf(request.getType()), pageable);
        } else {
            page = reviewRepository.findAll(pageable);
        }

        return PageResponse.<ReviewSummaryResponse>builder()
                .content(mapper.toSummaryResponseList(page.getContent()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    @Transactional
    public PageResponse<ReviewSummaryResponse> getReviewsByProduct(Long productId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewEntity> reviewPage = reviewRepository.findByProductIdAndTypeAndStatus(
                productId, ReviewType.PRODUCT_REVIEW, ReviewStatus.APPROVED, pageable);

        return PageResponse.<ReviewSummaryResponse>builder()
                .content(mapper.toSummaryResponseList(reviewPage.getContent()))
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .last(reviewPage.isLast())
                .first(reviewPage.isFirst())
                .build();
    }

    @Transactional
    public List<ReviewSummaryResponse> getHotReviews(String type, Integer limit) {
        ReviewType reviewType = ReviewType.valueOf(type);
        Pageable pageable = PageRequest.of(0, limit);
        Page<ReviewEntity> page = reviewRepository.findHotReviews(reviewType, pageable);
        return mapper.toSummaryResponseList(page.getContent());
    }

    // ============================================
    // ANSWER CRUD
    // ============================================

    public AnswerResponse createAnswer(AnswerCreateRequest request, Long userId, String userName, String userAvatar) {
        ReviewEntity review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        AnswerEntity answer = mapper.toEntity(request, review, userId, userName, userAvatar);
        answer.setLikeCount(0);
        answer.setDislikeCount(0);
        answer.setIsBestAnswer(false);
        answer.setIsVerified(false);
        answer.setIsFromShop(false);
        answer = answerRepository.save(answer);


        // Increment answer count
        reviewRepository.incrementAnswerCount(review.getId());

        return mapper.toAnswerResponse(answer);
    }

    public AnswerResponse updateAnswer(Long id, AnswerUpdateRequest request, Long userId) {
        AnswerEntity answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy câu trả lời"));

        if (!answer.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền cập nhật câu trả lời này");
        }

        mapper.updateAnswerEntity(answer, request);
        answer = answerRepository.save(answer);

        return mapper.toAnswerResponse(answer);
    }

    public void deleteAnswer(Long id, Long userId) {
        AnswerEntity answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy câu trả lời"));

        if (!answer.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền xóa câu trả lời này");
        }

        Long reviewId = answer.getReview().getId();
        answerRepository.delete(answer);

        // Decrement answer count
        reviewRepository.decrementAnswerCount(reviewId);
    }

    @Transactional
    public List<AnswerResponse> getAnswersByReview(Long reviewId) {
        List<AnswerEntity> answers = answerRepository.findByReview_IdOrderByCreatedAtAsc(reviewId);
        return mapper.toAnswerResponseList(answers);
    }

    // ============================================
    // REACTION
    // ============================================
    @Transactional
    public void addReaction(ReactionRequest request, Long userId) {
        ReactionTargetType targetType = ReactionTargetType.valueOf(request.getTargetType());
        ReactionType reactionType = ReactionType.valueOf(request.getType());

        // Check if already reacted
        var existingReaction = reactionRepository.findByTargetTypeAndTargetIdAndUserId(
                targetType, request.getTargetId(), userId);

        if (existingReaction.isPresent()) {
            ReviewReactionEntity reaction = existingReaction.get();

            // If same reaction, remove it (toggle)
            if (reaction.getType() == reactionType) {
                removeReactionCount(targetType, request.getTargetId(), reactionType);
                reactionRepository.delete(reaction);
                return;
            }

            // If different reaction, update it
            removeReactionCount(targetType, request.getTargetId(), reaction.getType());
            reaction.setType(reactionType);
            reactionRepository.save(reaction);
            addReactionCount(targetType, request.getTargetId(), reactionType);
        } else {
            // Create new reaction
            ReviewReactionEntity reaction = ReviewReactionEntity.builder()
                    .targetType(targetType)
                    .targetId(request.getTargetId())
                    .userId(userId)
                    .type(reactionType)
                    .build();

            reactionRepository.save(reaction);
            addReactionCount(targetType, request.getTargetId(), reactionType);
        }
    }

    public void addReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType) {
        if (targetType == ReactionTargetType.REVIEW) {
            if (reactionType == ReactionType.LIKE) {
                reviewRepository.incrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.ANSWER) {
            if (reactionType == ReactionType.LIKE) {
                answerRepository.incrementLikeCount(targetId);
            } else if (reactionType == ReactionType.DISLIKE) {
                answerRepository.incrementDislikeCount(targetId);
            }
        }
    }
    @Override
    @Transactional
    public ReviewResponse updateReviewStatus(Long reviewId, UpdateReviewStatusRequest request) {
        log.info("Updating review status for reviewId: {} to status: {}", reviewId, request.getStatus());

        // Tìm review
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        // Lấy trạng thái hiện tại
        ReviewStatus currentStatus = review.getStatus();
        ReviewStatus newStatus = request.getStatus();

        // Kiểm tra trạng thái hiện tại có giống trạng thái mới không
        if (currentStatus == newStatus) {
            log.warn("Review {} is already in status {}", reviewId, newStatus);
            throw new ResException(ResErrorCode.BAD_REQUEST, "Review đã ở trạng thái: " + newStatus);
        }

        // Validate business rules cho từng trạng thái
        validateStatusTransition(currentStatus, newStatus);

        // Update status
        review.setStatus(newStatus);

        // Xử lý rejection reason
        if (newStatus == ReviewStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
                throw new ResException(ResErrorCode.FIELD_REQUIRED, "Lý do từ chối là bắt buộc khi từ chối review");
            }
            review.setRejectionReason(request.getRejectionReason().trim());
            log.info("Review {} rejected with reason: {}", reviewId, request.getRejectionReason());
        } else {
            // Clear rejection reason nếu không phải REJECTED
            review.setRejectionReason(null);
        }

        // Lưu vào DB
        ReviewEntity updatedReview = reviewRepository.save(review);

        log.info("Successfully updated review {} from {} to {}",
                reviewId, currentStatus, updatedReview.getStatus());

        return mapper.toResponse(updatedReview);
    }

    /**
     * Validate business rules khi chuyển trạng thái
     */
    private void validateStatusTransition(ReviewStatus from, ReviewStatus to) {
        // Không cho phép chuyển từ CLOSED sang bất kỳ trạng thái nào
        if (from == ReviewStatus.CLOSED) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Không thể thay đổi trạng thái của review đã đóng");
        }

        // Không cho phép chuyển từ REJECTED sang CLOSED
        if (from == ReviewStatus.REJECTED && to == ReviewStatus.CLOSED) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Không thể đóng review đã bị từ chối. Vui lòng phê duyệt trước");
        }

        // Có thể thêm các rule khác tùy theo business logic
        // Ví dụ: PENDING -> APPROVED/REJECTED/CLOSED
        //        APPROVED -> REJECTED/CLOSED
        //        REJECTED -> PENDING/APPROVED (cho phép review lại)

        log.debug("Status transition validated: {} -> {}", from, to);
    }

    public void removeReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType) {
        if (targetType == ReactionTargetType.REVIEW) {
            if (reactionType == ReactionType.LIKE) {
                reviewRepository.decrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.ANSWER) {
            if (reactionType == ReactionType.LIKE) {
                answerRepository.decrementLikeCount(targetId);
            } else if (reactionType == ReactionType.DISLIKE) {
                answerRepository.decrementDislikeCount(targetId);
            }
        }
    }

    // ============================================
    // STATISTICS
    // ============================================

//    @Transactional
//    public ProductReviewSummary getProductReviewSummary(Long productId) {
//        List<ReviewEntity> reviews = reviewRepository.findByProductId(productId);
//
//        Double avgRating = reviewRepository.getAverageRatingByProduct(productId);
//
//        Long rating5 = reviewRepository.countByProductIdAndRatingExact(productId, 5.0);
//        Long rating4 = reviewRepository.countByProductIdAndRatingExact(productId, 4.0);
//        Long rating3 = reviewRepository.countByProductIdAndRatingExact(productId, 3.0);
//        Long rating2 = reviewRepository.countByProductIdAndRatingExact(productId, 2.0);
//        Long rating1 = reviewRepository.countByProductIdAndRatingExact(productId, 1.0);
//
//        // Get recent reviews
//        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<ReviewEntity> recentPage = reviewRepository.findByProductIdAndTypeAndStatus(
//                productId, ReviewType.PRODUCT_REVIEW, ReviewStatus.APPROVED, pageable);
//
//        return ProductReviewSummary.builder()
//                .productId(productId)
//                .totalReviews((long) reviews.size())
//                .averageRating(avgRating != null ? avgRating : 0.0)
//                .rating5Count(rating5)
//                .rating4Count(rating4)
//                .rating3Count(rating3)
//                .rating2Count(rating2)
//                .rating1Count(rating1)
//                .recentReviews(mapper.toSummaryResponseList(recentPage.getContent()))
//                .build();
//    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private List<TagEntity> getOrCreateTags(List<String> tagNames) {
        List<TagEntity> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            TagEntity tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        TagEntity newTag = mapper.toEntity(tagName);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }
}