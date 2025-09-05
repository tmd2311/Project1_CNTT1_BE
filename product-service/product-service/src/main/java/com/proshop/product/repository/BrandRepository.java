package com.proshop.product.repository;

import com.proshop.product.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {
}
