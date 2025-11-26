package com.proshop.review_service.service;

import com.proshop.review_service.dto.request.ReactionRequest;

/**
 * Service interface cho Reactions
 * Hỗ trợ reactions cho Question, ProductReview và Answer
 */
public interface ReactionService {

    void addReaction(ReactionRequest request, Long userId);
}
