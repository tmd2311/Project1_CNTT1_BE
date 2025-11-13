package com.proshop.review_service.repository;

import com.proshop.review_service.entity.ReviewReactionEntity;
import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReactionEntity, Long> {

    // Tìm reaction của user
    Optional<ReviewReactionEntity> findByTargetTypeAndTargetIdAndUserId(
            ReactionTargetType targetType,
            Long targetId,
            Long userId
    );

    // Đếm reaction theo type
    Long countByTargetTypeAndTargetIdAndType(
            ReactionTargetType targetType,
            Long targetId,
            ReactionType type
    );

    // Kiểm tra user đã react chưa
    boolean existsByTargetTypeAndTargetIdAndUserId(
            ReactionTargetType targetType,
            Long targetId,
            Long userId
    );

    // Xóa reaction
    void deleteByTargetTypeAndTargetIdAndUserId(
            ReactionTargetType targetType,
            Long targetId,
            Long userId
    );
}