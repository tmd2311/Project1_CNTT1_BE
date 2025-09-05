package com.proshop.product.service.product.impl;

import com.proshop.product.entity.ProductEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  @Override
  public List<ProductEntity> findAll() {
    return productRepository.findAll();
  }
}
