package com.proshop.product.repository;

import com.proshop.product.entity.ProductEntity;


import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>,
    JpaSpecificationExecutor<ProductEntity> {
  List<ProductEntity> findAll();

  ProductEntity findProductById(UUID id);


}
