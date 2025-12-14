package com.proshop.order.repository;

import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(long userId);

    List<OrderEntity> findByStatus(OrderStatus status);

    // ============================================
    // STATISTICS QUERIES
    // ============================================

    /**
     * Count orders grouped by status
     */
    @Query("SELECT o.status, COUNT(o) FROM OrderEntity o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    /**
     * Sum revenue by status and date range
     */
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumRevenueByStatusAndDateRange(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Get all orders in date range (for calculations)
     */
    @Query("SELECT o FROM OrderEntity o " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "ORDER BY o.createdAt ASC")
    List<OrderEntity> findOrdersBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Count orders by status and date
     */
    @Query(value = "SELECT COUNT(*) FROM orders o " +
            "WHERE o.status = :status " +
            "AND DATE(o.created_at) = CURRENT_DATE", nativeQuery = true)
    Long countTodayOrdersByStatus(@Param("status") OrderStatus status);

    // ============================================
    // REVENUE CALCULATION QUERIES
    // ============================================

    /**
     * Find completed orders that haven't been included in revenue calculation yet
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.status = 'COMPLETED' AND o.includedInRevenue = false ORDER BY o.createdAt ASC")
    List<OrderEntity> findCompletedOrdersNotInRevenue();
}
