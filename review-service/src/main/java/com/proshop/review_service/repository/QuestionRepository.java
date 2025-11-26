package com.proshop.review_service.repository;

import com.proshop.review_service.entity.QuestionEntity;
import com.proshop.review_service.util.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho QuestionEntity (Q&A)
 */
@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {

    // Tìm theo category
    Page<QuestionEntity> findByCategory_Id(Long categoryId, Pageable pageable);

    // Tìm theo user
    Page<QuestionEntity> findByUserId(Long userId, Pageable pageable);
    List<QuestionEntity> findByUserId(Long userId);

    // Tìm theo status
    Page<QuestionEntity> findByStatus(ReviewStatus status, Pageable pageable);

    // Tìm question featured
    Page<QuestionEntity> findByIsFeaturedTrue(Pageable pageable);

    // Tìm theo keyword
    @Query("SELECT q FROM QuestionEntity q WHERE " +
            "(LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<QuestionEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Update view count
    @Modifying
    @Query("UPDATE QuestionEntity q SET q.viewCount = q.viewCount + 1 WHERE q.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Update answer count
    @Modifying
    @Query("UPDATE QuestionEntity q SET q.answerCount = q.answerCount + 1 WHERE q.id = :id")
    void incrementAnswerCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE QuestionEntity q SET q.answerCount = q.answerCount - 1 WHERE q.id = :id AND q.answerCount > 0")
    void decrementAnswerCount(@Param("id") Long id);

    // Update like count
    @Modifying
    @Query("UPDATE QuestionEntity q SET q.likeCount = q.likeCount + 1 WHERE q.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE QuestionEntity q SET q.likeCount = q.likeCount - 1 WHERE q.id = :id AND q.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    // Lấy hot questions (nhiều view, like, answer)
    @Query("SELECT q FROM QuestionEntity q WHERE q.status = 'APPROVED' " +
            "ORDER BY (q.viewCount + q.likeCount * 2 + q.answerCount * 3) DESC")
    Page<QuestionEntity> findHotQuestions(Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(q) FROM QuestionEntity q WHERE q.status = :status")
    Long countByStatus(@Param("status") ReviewStatus status);
}
