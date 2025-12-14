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

    // ============================================
    // STATISTICS QUERIES
    // ============================================

    /**
     * Count low stock SKUs (stock <= threshold and active)
     */
    @Query("SELECT COUNT(s) FROM SKUEntity s " +
           "WHERE s.stock <= :threshold AND s.isActive = true")
    Long countLowStockSKUs(@Param("threshold") Integer threshold);

    /**
     * Count out of stock SKUs (stock = 0 and active)
     */
    @Query("SELECT COUNT(s) FROM SKUEntity s " +
           "WHERE s.stock = 0 AND s.isActive = true")
    Long countOutOfStockSKUs();

    /**
     * Find low stock SKUs ordered by stock amount
     */
    @Query("SELECT s FROM SKUEntity s " +
           "WHERE s.stock <= :threshold AND s.isActive = true " +
           "ORDER BY s.stock ASC")
    List<SKUEntity> findLowStockSKUs(@Param("threshold") Integer threshold);

    /**
     * Count active SKUs
     */
    @Query("SELECT COUNT(s) FROM SKUEntity s WHERE s.isActive = true")
    Long countActiveSKUs();
}

