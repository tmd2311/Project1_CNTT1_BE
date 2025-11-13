package com.proshop.review_service.repository;

import com.proshop.review_service.entity.ReviewCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewCategoryRepository extends JpaRepository<ReviewCategoryEntity, Long> {

    Optional<ReviewCategoryEntity> findBySlug(String slug);

    Optional<ReviewCategoryEntity> findByName(String name);

    List<ReviewCategoryEntity> findByIsActiveTrueOrderByDisplayOrderAsc();

    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}