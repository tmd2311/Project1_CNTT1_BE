package com.proshop.review_service.repository;

import com.proshop.review_service.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    // Tìm theo review
    List<AnswerEntity> findByReview_IdOrderByCreatedAtAsc(Long reviewId);
    Page<AnswerEntity> findByReview_Id(Long reviewId, Pageable pageable);

    // Tìm theo user
    Page<AnswerEntity> findByUserId(Long userId, Pageable pageable);
    List<AnswerEntity> findByUserId(Long userId);

    // Đếm số answer của review
    Long countByReview_Id(Long reviewId);

    // Tìm best answer
    Optional<AnswerEntity> findByReview_IdAndIsBestAnswerTrue(Long reviewId);

    // Tìm verified answers
    List<AnswerEntity> findByReview_IdAndIsVerifiedTrue(Long reviewId);

    // Update like count
    @Modifying
    @Query("UPDATE AnswerEntity a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE AnswerEntity a SET a.likeCount = a.likeCount - 1 WHERE a.id = :id AND a.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE AnswerEntity a SET a.dislikeCount = a.dislikeCount + 1 WHERE a.id = :id")
    void incrementDislikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE AnswerEntity a SET a.dislikeCount = a.dislikeCount - 1 WHERE a.id = :id AND a.dislikeCount > 0")
    void decrementDislikeCount(@Param("id") Long id);
}