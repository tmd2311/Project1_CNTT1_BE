package com.proshop.product.repository;

import com.proshop.product.entity.ProductEntity;


import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>,
  JpaSpecificationExecutor<ProductEntity> {
  List<ProductEntity> findAll();

  ProductEntity findProductById(UUID id);


}
