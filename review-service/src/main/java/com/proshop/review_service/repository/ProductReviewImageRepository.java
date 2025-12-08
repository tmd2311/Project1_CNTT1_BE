package com.proshop.review_service.repository;

import com.proshop.review_service.entity.ProductReviewImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho ProductReviewImageEntity
 */
@Repository
public interface ProductReviewImageRepository extends JpaRepository<ProductReviewImageEntity, Long> {

    // Tìm tất cả ảnh của product review
    List<ProductReviewImageEntity> findByProductReview_Id(Long productReviewId);

    // Xóa tất cả ảnh của product review
    @Modifying
    @Query("DELETE FROM ProductReviewImageEntity pri WHERE pri.productReview.id = :productReviewId")
    void deleteByProductReview_Id(@Param("productReviewId") Long productReviewId);

    // Đếm số ảnh của product review
    Long countByProductReview_Id(Long productReviewId);
}
