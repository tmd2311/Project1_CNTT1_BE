package com.proshop.review_service.repository;

import com.proshop.review_service.entity.ReviewImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {

    List<ReviewImageEntity> findByReview_IdOrderByDisplayOrderAsc(Long reviewId);

    List<ReviewImageEntity> findByReview_Id(Long reviewId);

    void deleteByReview_Id(Long reviewId);
}