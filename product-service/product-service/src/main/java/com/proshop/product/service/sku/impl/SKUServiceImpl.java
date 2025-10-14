// SKUServiceImpl.java
package com.proshop.product.service.sku.impl;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.SKURepository;
import com.proshop.product.service.sku.SKUService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SKUServiceImpl implements SKUService {

    private final SKURepository skuRepository;
    private final ProductRepository productRepository;

    @Override
    public SKUResponse createSKU(SKURequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        SKUEntity sku = SKUEntity.builder()
                .product(product)
                .skuCode(request.getSkuCode())
                .specs(request.getSpecs())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .stock(request.getStock())
                .barcode(request.getBarcode())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        skuRepository.save(sku);
        return mapToResponse(sku);
    }

    @Override
    public SKUResponse updateSKU(UUID id, SKURequest request) {
        SKUEntity sku = skuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        if (request.getProductId() != null) {
            ProductEntity product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            sku.setProduct(product);
        }

        sku.setSkuCode(request.getSkuCode());
        sku.setSpecs(request.getSpecs());
        sku.setPrice(request.getPrice());
        sku.setDiscountPrice(request.getDiscountPrice());
        sku.setStock(request.getStock());
        sku.setBarcode(request.getBarcode());
        sku.setIsActive(request.getIsActive());
        sku.setUpdatedAt(LocalDateTime.now());

        skuRepository.save(sku);
        return mapToResponse(sku);
    }

    @Override
    public void deleteSKU(UUID id) {
        skuRepository.deleteById(id);
    }

    @Override
    public SKUResponse getById(UUID id) {
        SKUEntity sku = skuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SKU not found"));
        return mapToResponse(sku);
    }

    @Override
    public List<SKUResponse> getAll() {
        return skuRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    private SKUResponse mapToResponse(SKUEntity sku) {
        return SKUResponse.builder()
                .id(sku.getId())
                .productId(sku.getProduct().getId())
                .skuCode(sku.getSkuCode())
                .specs(sku.getSpecs())
                .price(sku.getPrice())
                .discountPrice(sku.getDiscountPrice())
                .stock(sku.getStock())
                .barcode(sku.getBarcode())
                .isActive(sku.getIsActive())
                .build();
    }
}
