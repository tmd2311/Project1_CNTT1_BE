package com.proshop.review_service.repository;

import com.proshop.review_service.entity.*;
import com.proshop.review_service.util.enums.ReviewStatus;
import com.proshop.review_service.util.enums.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


// ============================================
// REVIEW REPOSITORY
// ============================================
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {

    // Tìm theo type
    Page<ReviewEntity> findByType(ReviewType type, Pageable pageable);

    // Tìm theo product
    Page<ReviewEntity> findByProductId(Long productId, Pageable pageable);
    List<ReviewEntity> findByProductId(Long productId);

    // Tìm theo category
    Page<ReviewEntity> findByCategory_Id(Long categoryId, Pageable pageable);

    // Tìm theo user
    Page<ReviewEntity> findByUserId(Long userId, Pageable pageable);
    List<ReviewEntity> findByUserId(Long userId);

    // Tìm theo status
    Page<ReviewEntity> findByStatus(ReviewStatus status, Pageable pageable);

    // Tìm Product Review theo rating
    Page<ReviewEntity> findByTypeAndRatingGreaterThanEqual(ReviewType type, Double rating, Pageable pageable);

    // Tìm review featured
    Page<ReviewEntity> findByIsFeaturedTrue(Pageable pageable);

    // Tìm theo keyword
    @Query("SELECT r FROM ReviewEntity r WHERE " +
            "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ReviewEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Tìm theo product và type
    @Query("SELECT r FROM ReviewEntity r WHERE r.productId = :productId AND r.type = :type AND r.status = :status")
    Page<ReviewEntity> findByProductIdAndTypeAndStatus(
            @Param("productId") Long productId,
            @Param("type") ReviewType type,
            @Param("status") ReviewStatus status,
            Pageable pageable
    );

    // Đếm số review của product
    Long countByProductIdAndType(Long productId, ReviewType type);

    // Đếm theo rating
    Long countByProductIdAndRating(Long productId, Double rating);

    // Tính average rating
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.productId = :productId AND r.type = 'PRODUCT_REVIEW' AND r.status = 'APPROVED'")
    Double getAverageRatingByProduct(@Param("productId") Long productId);

    // Update view count
    @Modifying
    @Query("UPDATE ReviewEntity r SET r.viewCount = r.viewCount + 1 WHERE r.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Update answer count
    @Modifying
    @Query("UPDATE ReviewEntity r SET r.answerCount = r.answerCount + 1 WHERE r.id = :id")
    void incrementAnswerCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ReviewEntity r SET r.answerCount = r.answerCount - 1 WHERE r.id = :id AND r.answerCount > 0")
    void decrementAnswerCount(@Param("id") Long id);

    // Update like count
    @Modifying
    @Query("UPDATE ReviewEntity r SET r.likeCount = r.likeCount + 1 WHERE r.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ReviewEntity r SET r.likeCount = r.likeCount - 1 WHERE r.id = :id AND r.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    // Lấy review hot (nhiều view, like, answer)
    @Query("SELECT r FROM ReviewEntity r WHERE r.type = :type AND r.status = 'APPROVED' " +
            "ORDER BY (r.viewCount + r.likeCount * 2 + r.answerCount * 3) DESC")
    Page<ReviewEntity> findHotReviews(@Param("type") ReviewType type, Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.type = :type")
    Long countByType(@Param("type") ReviewType type);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.productId = :productId AND r.rating = :rating")
    Long countByProductIdAndRatingExact(@Param("productId") Long productId, @Param("rating") Double rating);
}