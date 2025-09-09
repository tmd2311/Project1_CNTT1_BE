package com.proshop.product.repository;

import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.entity.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {

    @Query("""
        SELECT new com.proshop.product.dto.response.BrandResponse(
            b.id,
            b.name,
            b.logoUrl,
            b.slug
        )
        FROM BrandEntity b
        WHERE (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')))
        """)
    Page<BrandResponse> searchBrands(@Param("name") String name, Pageable pageable);
}