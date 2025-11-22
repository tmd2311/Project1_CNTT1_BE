package com.proshop.sale_service.client;

import com.proshop.sale_service.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign Client để gọi Product Service
 */
@FeignClient(name = "product-service", url = "${product.service.url:http://localhost:8082}")
public interface ProductClient {

    /**
     * Lấy thông tin SKU
     */
    @GetMapping("/api/v1/skus/{id}")
    GeneralResponse<SKUResponse> getSKU(@PathVariable("id") Long id);

    /**
     * Cập nhật giá sale cho SKU
     */
    @PutMapping("/api/v1/skus/{id}/sale-price")
    GeneralResponse<Void> updateSKUSalePrice(
        @PathVariable("id") Long skuId,
        @RequestBody UpdateSKUSalePriceRequest request
    );

    /**
     * Revert giá SKU về giá gốc
     */
    @PutMapping("/api/v1/skus/{id}/revert-price")
    GeneralResponse<Void> revertSKUPrice(@PathVariable("id") Long skuId);

    /**
     * DTO cho SKU Response
     */
    class SKUResponse {
        private Long id;
        private Long productId;
        private String skuCode;
        private Double price;
        private Double salePrice;
        private Long saleId;
        private Integer stockQuantity;

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Double getSalePrice() { return salePrice; }
        public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

        public Long getSaleId() { return saleId; }
        public void setSaleId(Long saleId) { this.saleId = saleId; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    }

    /**
     * DTO cho Update SKU Sale Price Request
     */
    class UpdateSKUSalePriceRequest {
        private Double originalPrice;
        private Double salePrice;
        private Long saleId;

        public UpdateSKUSalePriceRequest() {}

        public UpdateSKUSalePriceRequest(Double originalPrice, Double salePrice, Long saleId) {
            this.originalPrice = originalPrice;
            this.salePrice = salePrice;
            this.saleId = saleId;
        }

        // Getters & Setters
        public Double getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }

        public Double getSalePrice() { return salePrice; }
        public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

        public Long getSaleId() { return saleId; }
        public void setSaleId(Long saleId) { this.saleId = saleId; }
    }
}
