package com.proshop.order.repository;

import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(UUID userId);
    List<OrderEntity> findByStatus(OrderStatus status);
}
