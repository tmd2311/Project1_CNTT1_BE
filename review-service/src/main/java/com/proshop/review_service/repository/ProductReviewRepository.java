package com.proshop.review_service.repository;

import com.proshop.review_service.entity.ProductReviewEntity;
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
import java.util.UUID;

/**
 * Repository cho ProductReviewEntity (Đánh giá sản phẩm)
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Long>, JpaSpecificationExecutor<ProductReviewEntity> {

    // Tìm theo product
    Page<ProductReviewEntity> findByProductId(UUID productId, Pageable pageable);
    List<ProductReviewEntity> findByProductId(UUID productId);

    // Tìm theo user
    Page<ProductReviewEntity> findByUserId(Long userId, Pageable pageable);
    List<ProductReviewEntity> findByUserId(Long userId);

    // Tìm theo status
    Page<ProductReviewEntity> findByStatus(ReviewStatus status, Pageable pageable);

    // Tìm Product Review theo rating
    Page<ProductReviewEntity> findByRatingGreaterThanEqual(Double rating, Pageable pageable);

    // Tìm review featured
    Page<ProductReviewEntity> findByIsFeaturedTrue(Pageable pageable);

    // Tìm theo keyword
    @Query("SELECT pr FROM ProductReviewEntity pr WHERE " +
            "LOWER(pr.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ProductReviewEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Tìm theo product và status
    @Query("SELECT pr FROM ProductReviewEntity pr WHERE pr.productId = :productId AND pr.status = :status")
    Page<ProductReviewEntity> findByProductIdAndStatus(
            @Param("productId") UUID productId,
            @Param("status") ReviewStatus status,
            Pageable pageable
    );

    // Đếm số review của product
    Long countByProductId(UUID productId);

    // Đếm theo rating
    Long countByProductIdAndRating(UUID productId, Double rating);

    // Tính average rating
    @Query("SELECT AVG(pr.rating) FROM ProductReviewEntity pr WHERE pr.productId = :productId AND pr.status = 'APPROVED'")
    Double getAverageRatingByProduct(@Param("productId") UUID productId);

    // Update view count
    @Modifying
    @Query("UPDATE ProductReviewEntity pr SET pr.viewCount = pr.viewCount + 1 WHERE pr.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Update like count
    @Modifying
    @Query("UPDATE ProductReviewEntity pr SET pr.likeCount = pr.likeCount + 1 WHERE pr.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ProductReviewEntity pr SET pr.likeCount = pr.likeCount - 1 WHERE pr.id = :id AND pr.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    // Lấy review hot (nhiều view, like)
    @Query("SELECT pr FROM ProductReviewEntity pr WHERE pr.status = 'APPROVED' " +
            "ORDER BY (pr.viewCount + pr.likeCount * 2) DESC")
    Page<ProductReviewEntity> findHotProductReviews(Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(pr) FROM ProductReviewEntity pr WHERE pr.status = :status")
    Long countByStatus(@Param("status") ReviewStatus status);

    @Query("SELECT COUNT(pr) FROM ProductReviewEntity pr WHERE pr.productId = :productId AND pr.rating = :rating")
    Long countByProductIdAndRatingExact(@Param("productId") UUID productId, @Param("rating") Double rating);

    // Lấy review theo product và rating
    @Query("SELECT pr FROM ProductReviewEntity pr WHERE pr.productId = :productId AND pr.rating >= :minRating AND pr.status = 'APPROVED'")
    Page<ProductReviewEntity> findByProductIdAndRatingGreaterThanEqual(
            @Param("productId") UUID productId,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}
