package com.proshop.sale_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity lưu danh sách sản phẩm tham gia Sale
 * Quan hệ Many-to-Many giữa Sale và Product/SKU
 */
@Entity
@Table(name = "sale_products",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sale_id", "product_id", "sku_id"})
    },
    indexes = {
        @Index(name = "idx_sale_products_sale_id", columnList = "sale_id"),
        @Index(name = "idx_sale_products_product_id", columnList = "product_id"),
        @Index(name = "idx_sale_products_sku_id", columnList = "sku_id"),
        @Index(name = "idx_sale_products_category_id", columnList = "category_id"),
        @Index(name = "idx_sale_products_brand_id", columnList = "brand_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    // ============ TARGET (có thể áp dụng cho Product hoặc SKU cụ thể) ============

    /**
     * ID của product (nếu apply cho cả product)
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * ID của SKU cụ thể (nếu chỉ apply cho 1 SKU)
     */
    @Column(name = "sku_id")
    private Long skuId;

    /**
     * ID của category (nếu apply theo category)
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * ID của brand (nếu apply theo brand)
     */
    @Column(name = "brand_id")
    private Long brandId;

    // ============ GIÁ (Snapshot khi apply) ============

    /**
     * Giá gốc của SKU trước khi apply sale
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * Giá sau khi apply sale
     */
    @Column(name = "sale_price", precision = 10, scale = 2)
    private BigDecimal salePrice;

    /**
     * Số tiền được giảm
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // ============ TRẠNG THÁI ============

    /**
     * Sale đã được apply vào SKU chưa?
     */
    @Column(name = "is_applied")
    private Boolean isApplied = false;

    /**
     * Thời điểm apply sale
     */
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    /**
     * Thời điểm revert về giá gốc
     */
    @Column(name = "reverted_at")
    private LocalDateTime revertedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isApplied == null) {
            this.isApplied = false;
        }
    }
}
