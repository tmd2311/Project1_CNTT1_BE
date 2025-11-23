package com.proshop.sale_service.entity;

import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.SaleApplyScope;
import com.proshop.sale_service.util.enums.SaleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Sale Entity - Quản lý chương trình SALE (giảm giá sản phẩm)
 *
 * Features:
 * - Support nhiều loại sale: Percentage, Fixed Amount, Buy X Get Y
 * - Phạm vi áp dụng: All Products, Specific Products, Category, Brand
 * - Auto lifecycle management với Scheduler
 * - Priority system cho multiple sales
 * - Image support (banner & thumbnail)
 */
@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sales_code", columnList = "code"),
    @Index(name = "idx_sales_status", columnList = "status"),
    @Index(name = "idx_sales_dates", columnList = "start_date, end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "code", unique = true, length = 100, nullable = false)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ============ LOẠI SALE ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type", nullable = false, length = 50)
    private SaleType saleType;

    @NotNull
    @Column(name = "sale_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal saleValue;

    // ============ GIỚI HẠN ============

    @Column(name = "min_purchase_quantity")
    private Integer minPurchaseQuantity = 1;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    /**
     * Số lượng sale có thể sử dụng (null = unlimited)
     */
    @Column(name = "quantity")
    private Integer quantity;

    /**
     * Số lượng đã sử dụng
     */
    @Column(name = "used_count")
    private Integer usedCount = 0;

    /**
     * Giá trị đơn hàng tối thiểu để áp dụng sale
     */
    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    // ============ PHẠM VI ÁP DỤNG ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "apply_scope", nullable = false, length = 50)
    private SaleApplyScope applyScope;

    // ============ THỜI GIAN ============

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // ============ TRẠNG THÁI ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PromotionStatus status = PromotionStatus.SCHEDULED;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    // ============ PRIORITY ============

    /**
     * Độ ưu tiên khi 1 sản phẩm có nhiều sale
     * Số càng cao = ưu tiên cao hơn
     */
    @Column(name = "priority")
    private Integer priority = 0;

    // ============ HÌNH ẢNH ============

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

    // ============ TRACKING ============

    /**
     * Tổng số lần sale này đã được apply vào sản phẩm
     */
    @Column(name = "total_applied_count")
    private Integer totalAppliedCount = 0;

    // ============ AUDIT ============

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============ LIFECYCLE HOOKS ============

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isDelete == null) {
            this.isDelete = false;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.totalAppliedCount == null) {
            this.totalAppliedCount = 0;
        }
        if (this.priority == null) {
            this.priority = 0;
        }
        if (this.usedCount == null) {
            this.usedCount = 0;
        }
        if (this.status == null) {
            // Auto set status based on dates
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(this.startDate)) {
                this.status = PromotionStatus.SCHEDULED;
            } else if (now.isAfter(this.endDate)) {
                this.status = PromotionStatus.EXPIRED;
            } else {
                this.status = PromotionStatus.ACTIVE;
            }
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
