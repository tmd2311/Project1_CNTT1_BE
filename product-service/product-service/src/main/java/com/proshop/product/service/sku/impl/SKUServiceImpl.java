// SKUServiceImpl.java
package com.proshop.product.service.sku.impl;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
import com.proshop.product.dto.request.UpdateSKUSalePriceRequest;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.SKURepository;
import com.proshop.product.service.sku.SKUService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

        SKUEntity savedSku = skuRepository.save(sku);
        return mapToResponse(savedSku);

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
                .salePrice(sku.getSalePrice())
                .saleId(sku.getSaleId())
                .stock(sku.getStock())
                .barcode(sku.getBarcode())
                .isActive(sku.getIsActive())
                .build();
    }
    @Override
    public SKUResponse updateStockSKU(UUID id, SKUstockRequest request) {
        SKUEntity sku = skuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        if (request.getProductId() != null) {
            ProductEntity product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            sku.setProduct(product);
        }
        sku.setStock(request.getStock());

        skuRepository.save(sku);
        return mapToResponse(sku);
    }

    /**
     * Cập nhật giá sale cho SKU
     * NOTE: Hiện tại sale-service gửi Long ID nhưng SKU entity dùng UUID
     * TODO: Cần refactor để sử dụng SKU code hoặc thêm Long ID field
     */
    @Override
    @Transactional
    public void updateSalePrice(UUID id, UpdateSKUSalePriceRequest request) {
        log.info("Updating sale price for SKU ID: {}", id);

        SKUEntity sku = findSKUById(id);

        // Lưu giá gốc nếu chưa có (lần đầu apply sale)
        if (sku.getSalePrice() == null && request.getOriginalPrice() != null) {
            // Đảm bảo price được lưu đúng
            if (sku.getPrice() == null || !sku.getPrice().equals(request.getOriginalPrice())) {
                sku.setPrice(request.getOriginalPrice());
            }
        }

        // Cập nhật giá sale
        sku.setSalePrice(request.getSalePrice());
        sku.setSaleId(request.getSaleId());
        sku.setUpdatedAt(LocalDateTime.now());

        skuRepository.save(sku);
        log.info("Updated sale price for SKU {}: {} -> {}",
            id, request.getOriginalPrice(), request.getSalePrice());
    }

    /**
     * Revert giá SKU về giá gốc (xóa sale price)
     */
    @Override
    @Transactional
    public void revertPrice(UUID id) {
        log.info("Reverting price for SKU ID: {}", id);

        // WORKAROUND: Convert Long to UUID
        SKUEntity sku = findSKUById(id);

        // Revert về giá gốc
        sku.setSalePrice(null);
        sku.setSaleId(null);
        sku.setUpdatedAt(LocalDateTime.now());

        skuRepository.save(sku);
        log.info("Reverted price for SKU {}", id);
    }

    private SKUEntity findSKUById(UUID id) {
        return skuRepository.findById(id).orElseThrow(() -> new RuntimeException("SKU not found"));
    }

}
