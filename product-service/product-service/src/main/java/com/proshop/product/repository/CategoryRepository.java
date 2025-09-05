package com.proshop.product.repository;

import com.proshop.product.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}