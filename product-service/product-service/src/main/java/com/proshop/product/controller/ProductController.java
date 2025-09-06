package com.proshop.product.controller;

import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.service.product.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;


  @GetMapping("/product")
  public ResponseEntity<Page<ProductResponse>> getAllProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "12") int size) {
    return ResponseEntity.ok(productService.getProducts(page, size));
  }
  @GetMapping("/product/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }
    @DeleteMapping("/delete")
    public ResponseEntity<GeneralResponse<ProductDeleteResponse>> deleteProduct(@RequestParam("id") UUID id) {
        GeneralResponse<ProductDeleteResponse> response = productService.deleteProduct(id);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }


}
