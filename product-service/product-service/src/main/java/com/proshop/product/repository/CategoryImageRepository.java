package com.proshop.product.repository;

import com.proshop.product.entity.CategoryImageEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryImageRepository extends JpaRepository<CategoryImageEntity, UUID> {
}
