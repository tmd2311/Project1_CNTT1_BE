package com.proshop.product.controller;

import com.proshop.product.entity.ProductEntity;
import com.proshop.product.service.product.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/product")
  public ResponseEntity<List<ProductEntity>> getAllProducts() {
    List<ProductEntity> products = productService.findAll();
    return ResponseEntity.ok(products);
  }

}
