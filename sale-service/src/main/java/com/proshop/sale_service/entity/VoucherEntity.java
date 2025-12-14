package com.proshop.sale_service.entity;

import com.proshop.sale_service.util.enums.DiscountType;
import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.VoucherType;
import com.proshop.sale_service.util.enums.VoucherUserScope;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Voucher Entity - Quản lý mã giảm giá cho đơn hàng
 */
@Entity
@Table(name = "vouchers", indexes = {
    @Index(name = "idx_vouchers_code", columnList = "code"),
    @Index(name = "idx_vouchers_status", columnList = "status"),
    @Index(name = "idx_vouchers_dates", columnList = "start_date, end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherEntity {

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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ============ LOẠI VOUCHER ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false, length = 50)
    private VoucherType voucherType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType;

    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    // ============ ĐIỀU KIỆN ÁP DỤNG ============

    /**
     * Giá trị đơn hàng tối thiểu để sử dụng voucher
     */
    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    /**
     * Số tiền giảm tối đa (cho voucher %)
     */
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    // ============ GIỚI HẠN SỬ DỤNG ============

    /**
     * Tổng số lượng voucher có thể sử dụng
     */
    @NotNull
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    /**
     * Số lượng đã sử dụng
     */
    @Column(name = "used_count")
    private Integer usedCount = 0;

    /**
     * Giới hạn số lần mỗi user có thể sử dụng
     */
    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;

    // ============ PHẠM VI USER ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_scope", nullable = false, length = 50)
    private VoucherUserScope userScope;

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
    private PromotionStatus status = PromotionStatus.ACTIVE;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    // ============ HÌNH ẢNH ============

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

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
        if (this.usedCount == null) {
            this.usedCount = 0;
        }
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

    /**
     * Check xem voucher còn available không
     */
    public boolean isAvailable() {
        return this.isActive
            && !this.isDelete
            && this.status == PromotionStatus.ACTIVE
            && this.usedCount < this.totalQuantity
            && LocalDateTime.now().isAfter(this.startDate)
            && LocalDateTime.now().isBefore(this.endDate);
    }
}
