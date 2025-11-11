package com.proshop.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false, precision = 100, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ← Thêm relationship với OrderItems
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();
}