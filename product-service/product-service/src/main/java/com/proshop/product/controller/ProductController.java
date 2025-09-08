package com.proshop.product.controller;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
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

    @PutMapping("/product/{id}")
    public ResponseEntity<GeneralResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductUpdateRequest request) {
        GeneralResponse<ProductResponse> response = productService.updateProduct(id, request);
        if (response.getStatus().getCode().equals("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (response.getStatus().getCode().equals("400")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/search")
    public ResponseEntity<GeneralResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        GeneralResponse<Page<ProductResponse>> response = productService.searchProducts(name, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product/create")
    public ResponseEntity<GeneralResponse<ProductResponse>> createProduct(
            @RequestBody ProductCreateRequest request) {
        GeneralResponse<ProductResponse> response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
