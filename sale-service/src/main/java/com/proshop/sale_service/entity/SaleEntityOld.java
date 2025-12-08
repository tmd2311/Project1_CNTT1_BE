package com.proshop.sale_service.entity;

import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.SaleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DEPRECATED - SaleEntity cũ (đã được thay thế bởi SaleEntity mới)
 * File này chỉ giữ lại để backup, KHÔNG sử dụng nữa
 */
// @Entity - Commented out to avoid conflict
// @Table(name = "sales_old")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleEntityOld {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 100)
    @NotNull
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    @NotNull
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type", nullable = false)
    private SaleType saleType;

    @NotNull
    @Column(name = "sale_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal saleValue;

    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // Thêm field status cho scheduler
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PromotionStatus status = PromotionStatus.SCHEDULED;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isDelete = false;
        this.isActive = true;
        this.usedCount = 0;

        // Auto set status based on dates
        if (this.status == null) {
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
