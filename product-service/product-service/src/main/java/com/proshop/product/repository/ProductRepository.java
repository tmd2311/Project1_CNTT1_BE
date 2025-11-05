package com.proshop.product.repository;

import com.proshop.product.entity.ProductEntity;


import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>,
    JpaSpecificationExecutor<ProductEntity> {
  List<ProductEntity> findAll();

  ProductEntity findProductById(UUID id);

  @Query("SELECT p FROM ProductEntity p ORDER BY p.createdAt DESC")
  List<ProductEntity> findLatestProducts(Pageable pageable);

}
