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

    // Tìm theo question
    List<AnswerEntity> findByQuestion_IdOrderByCreatedAtAsc(Long questionId);
    Page<AnswerEntity> findByQuestion_Id(Long questionId, Pageable pageable);

    // Tìm theo user
    Page<AnswerEntity> findByUserId(Long userId, Pageable pageable);
    List<AnswerEntity> findByUserId(Long userId);

    // Đếm số answer của question
    Long countByQuestion_Id(Long questionId);

    // Tìm best answer
    Optional<AnswerEntity> findByQuestion_IdAndIsBestAnswerTrue(Long questionId);

    // Tìm verified answers
    List<AnswerEntity> findByQuestion_IdAndIsVerifiedTrue(Long questionId);

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