package com.proshop.product.repository;

import com.proshop.product.entity.ProductImageEntity;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ProductImageEntity p WHERE p.product.id = :productId")
    void deleteAllByProductId(@Param("productId") UUID productId);
}
