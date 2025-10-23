package com.proshop.product.controller;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.service.product.ProductService;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping("/product")
    public ResponseEntity<GeneralResponse<PageResponse<ProductResponse>>> getAllProducts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "12") int size) {

        GeneralResponse<PageResponse<ProductResponse>> response = productService.getProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<GeneralResponse<ProductResponse>> getProductById(@PathVariable("id") String idStr) {
        return ResponseEntity.ok(productService.getProductById(idStr));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GeneralResponse<ProductDeleteResponse>> deleteProduct(
        @RequestParam(name = "id") String idStr) {

        GeneralResponse<ProductDeleteResponse> response = productService.deleteProduct(idStr);
        if ("404".equals(response.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<GeneralResponse<ProductResponse>> updateProduct(
        @PathVariable("id") UUID id,
        @RequestBody ProductUpdateRequest request) {
        GeneralResponse<ProductResponse> response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/search")
    public ResponseEntity<GeneralResponse<PageResponse<ProductResponse>>> searchProducts(
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "brand", required = false) String brand,
        @RequestParam(name = "category", required = false) String category,
        @RequestParam(name = "minPrice", required = false) Double minPrice,
        @RequestParam(name = "maxPrice", required = false) Double maxPrice,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "12") int size) {

        GeneralResponse<PageResponse<ProductResponse>> response = productService.searchProducts(
            name, brand, category, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product/create")
    public ResponseEntity<GeneralResponse<ProductResponse>> createProduct(
            @RequestBody ProductCreateRequest request) {
        GeneralResponse<ProductResponse> response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
