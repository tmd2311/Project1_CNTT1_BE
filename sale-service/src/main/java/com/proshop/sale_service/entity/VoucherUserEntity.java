package com.proshop.sale_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Voucher User Entity - Gán voucher cho user cụ thể
 * Dùng khi VoucherUserScope = SPECIFIC_USERS
 */
@Entity
@Table(name = "voucher_users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"voucher_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_voucher_users_voucher_id", columnList = "voucher_id"),
        @Index(name = "idx_voucher_users_user_id", columnList = "user_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUserEntity {

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

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
