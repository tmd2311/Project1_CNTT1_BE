package com.proshop.review_service.service.impl;

import com.proshop.review_service.dto.request.ReactionRequest;
import com.proshop.review_service.entity.ReviewReactionEntity;
import com.proshop.review_service.repository.AnswerRepository;
import com.proshop.review_service.repository.ProductReviewRepository;
import com.proshop.review_service.repository.QuestionRepository;
import com.proshop.review_service.repository.ReviewReactionRepository;
import com.proshop.review_service.service.ReactionService;
import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReactionServiceImpl implements ReactionService {

    private final ReviewReactionRepository reactionRepository;
    private final QuestionRepository questionRepository;
    private final ProductReviewRepository productReviewRepository;
    private final AnswerRepository answerRepository;

    @Override
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

    private void addReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType) {
        if (targetType == ReactionTargetType.QUESTION) {
            if (reactionType == ReactionType.LIKE) {
                questionRepository.incrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.PRODUCT_REVIEW) {
            if (reactionType == ReactionType.LIKE) {
                productReviewRepository.incrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.ANSWER) {
            if (reactionType == ReactionType.LIKE) {
                answerRepository.incrementLikeCount(targetId);
            } else if (reactionType == ReactionType.DISLIKE) {
                answerRepository.incrementDislikeCount(targetId);
            }
        }
    }

    private void removeReactionCount(ReactionTargetType targetType, Long targetId, ReactionType reactionType) {
        if (targetType == ReactionTargetType.QUESTION) {
            if (reactionType == ReactionType.LIKE) {
                questionRepository.decrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.PRODUCT_REVIEW) {
            if (reactionType == ReactionType.LIKE) {
                productReviewRepository.decrementLikeCount(targetId);
            }
        } else if (targetType == ReactionTargetType.ANSWER) {
            if (reactionType == ReactionType.LIKE) {
                answerRepository.decrementLikeCount(targetId);
            } else if (reactionType == ReactionType.DISLIKE) {
                answerRepository.decrementDislikeCount(targetId);
            }
        }
    }
}
