package com.proshop.order.service.order;

import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {

    // USER METHODS
    GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request);
    GeneralResponse<List<OrderResponse>> getOrdersByUserId(Long userId);
    GeneralResponse<OrderResponse> getOrderByIdAndUserId(UUID orderId, Long userId);
    GeneralResponse<OrderDetailResponse> getOrderDetailByIdAndUserId(UUID orderId, Long userId); // ← NEW
    GeneralResponse<OrderResponse> cancelOrder(UUID orderId, Long userId);
    List<BestSellerResponse> getTopSellingProducts();
    boolean hasUserPurchasedProduct(Long userId, UUID productId);

    // ADMIN METHODS
    GeneralResponse<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest);
    GeneralResponse<OrderResponse> getOrderById(HttpServletRequest httpRequest, UUID orderId);
    GeneralResponse<OrderDetailResponse> getOrderDetailById(HttpServletRequest httpRequest, UUID orderId); // ← NEW
    GeneralResponse<List<OrderResponse>> getOrdersByUserIdAdmin(HttpServletRequest httpRequest, Long userId);
    GeneralResponse<OrderResponse> updateOrder(HttpServletRequest httpRequest, UUID orderId, OrderRequest request);
    GeneralResponse<OrderDeleteResponse> deleteOrder(HttpServletRequest httpRequest, UUID orderId);

    // ============================================
    // STATISTICS METHODS (ADMIN)
    // ============================================

    /**
     * Get revenue summary with percent change comparison (ADMIN only)
     * @param startDate Start date (null = current month start)
     * @param endDate End date (null = today)
     * @param request HTTP request for admin verification
     * @return Revenue summary with comparison to previous period
     */
    RevenueSummaryResponse getRevenueSummary(LocalDate startDate, LocalDate endDate, HttpServletRequest request);

    /**
     * Get count of orders grouped by status (ADMIN only)
     * @param request HTTP request for admin verification
     * @return Map of status -> count
     */
    Map<String, Long> getOrdersByStatus(HttpServletRequest request);

    /**
     * Get today's statistics - orders count, revenue, status breakdown (ADMIN only)
     * @param request HTTP request for admin verification
     * @return Today's stats
     */
    TodayStatsResponse getTodayStats(HttpServletRequest request);

}