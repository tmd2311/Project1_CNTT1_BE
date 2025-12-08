package com.proshop.order.repository;

import com.proshop.order.dto.response.BestSellerResponse;
import com.proshop.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
    List<OrderItemEntity> findByOrderOrderId(UUID orderId);

    @Query("SELECT new com.proshop.order.dto.response.BestSellerResponse(oi.productId, SUM(oi.quantity)) " +
        "FROM OrderItemEntity oi " +
        "GROUP BY oi.productId " +
        "ORDER BY SUM(oi.quantity) DESC")
    List<BestSellerResponse> findTopSellingProducts();
}