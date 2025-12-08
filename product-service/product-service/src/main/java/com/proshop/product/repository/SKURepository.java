package com.proshop.product.repository;

import com.proshop.product.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SKURepository extends JpaRepository<SKUEntity, UUID> {

    /**
     * Find all SKUs by product ID
     */
    List<SKUEntity> findByProductId(UUID productId);

    /**
     * Find all SKUs by category ID
     */
    @Query("SELECT s FROM SKUEntity s WHERE s.product.category.id = :categoryId")
    List<SKUEntity> findByProductCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find all SKUs by brand ID
     */
    @Query("SELECT s FROM SKUEntity s WHERE s.product.brand.id = :brandId")
    List<SKUEntity> findByProductBrandId(@Param("brandId") UUID brandId);
}

