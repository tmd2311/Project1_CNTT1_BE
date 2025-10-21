package com.proshop.order.repository;

import com.proshop.order.entity.PaymentEntity;
import com.proshop.order.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByStatus(PaymentStatus status);
    List<PaymentEntity> findByOrderOrderIdIn(List<UUID> orderIds);
    List<PaymentEntity> findByOrderOrderId(UUID orderId);
}
