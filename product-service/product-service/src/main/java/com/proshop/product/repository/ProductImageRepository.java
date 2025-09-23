package com.proshop.product.repository;

import com.proshop.product.entity.ProductImageEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {

}
