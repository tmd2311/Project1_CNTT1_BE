package com.proshop.review_service.service;

import com.proshop.review_service.dto.request.*;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ReviewResponse;
import com.proshop.review_service.dto.response.ReviewSummaryResponse;
import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, Long userId, String userName, String userAvatar, List<MultipartFile> images);
    ReviewResponse getReviewById(Long id);
    ReviewResponse updateReview(Long id, ReviewUpdateRequest request, Long userId, List<MultipartFile> images);
    void deleteReview(Long id, Long userId);
    PageResponse<ReviewSummaryResponse> searchReviews(ReviewSearchRequest request);
    PageResponse<ReviewSummaryResponse> getReviewsByProduct(Long productId, Integer page, Integer size);
    PageResponse<ReviewSummaryResponse> getAllReviews(Integer page, Integer size);
    List<ReviewSummaryResponse> getHotReviews(String type, Integer limit);
    AnswerResponse createAnswer(AnswerCreateRequest request, Long userId, String userName, String userAvatar);
    AnswerResponse updateAnswer(Long id, AnswerUpdateRequest request, Long userId);
    void deleteAnswer(Long id, Long userId);
    List<AnswerResponse> getAnswersByReview(Long reviewId);
    void addReaction(ReactionRequest request, Long userId);
    void addReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType);
    void removeReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType);
    ReviewResponse updateReviewStatus(Long reviewId, UpdateReviewStatusRequest request);

}
