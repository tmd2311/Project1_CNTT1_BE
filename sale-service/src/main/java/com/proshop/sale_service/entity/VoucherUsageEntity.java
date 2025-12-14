package com.proshop.sale_service.entity;

import com.proshop.sale_service.util.enums.VoucherUsageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Voucher Usage Entity - Tracking việc sử dụng voucher
 */
@Entity
@Table(name = "voucher_usage", indexes = {
    @Index(name = "idx_voucher_usage_voucher_id", columnList = "voucher_id"),
    @Index(name = "idx_voucher_usage_user_id", columnList = "user_id"),
    @Index(name = "idx_voucher_usage_order_id", columnList = "order_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID của đơn hàng sử dụng voucher
     */
    @Column(name = "order_id")
    private UUID orderId;

    // ============ THÔNG TIN GIẢM GIÁ ============

    /**
     * Tổng giá trị đơn hàng
     */
    @Column(name = "order_value", precision = 10, scale = 2)
    private BigDecimal orderValue;

    /**
     * Số tiền được giảm
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // ============ TRẠNG THÁI ============

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private VoucherUsageStatus status = VoucherUsageStatus.USED;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @PrePersist
    public void prePersist() {
        if (this.usedAt == null) {
            this.usedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = VoucherUsageStatus.USED;
        }
    }
}
