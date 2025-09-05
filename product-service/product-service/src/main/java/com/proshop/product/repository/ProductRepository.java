package com.proshop.product.repository;

import com.proshop.product.entity.ProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  @Override
  List<ProductEntity> findAll();
}
