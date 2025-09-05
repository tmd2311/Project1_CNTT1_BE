package com.proshop.product.service.product;

import com.proshop.product.entity.ProductEntity;
import java.util.List;

public interface ProductService {
  List<ProductEntity> findAll();
}
