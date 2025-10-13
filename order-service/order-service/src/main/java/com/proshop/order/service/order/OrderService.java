package com.proshop.order.service.order;

import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    // ============================================
    // USER METHODS (Authenticated users)
    // ============================================

    /**
     * Create order for authenticated user
     */
    GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request);

    /**
     * Get all orders of specific user
     */
    GeneralResponse<List<OrderResponse>> getOrdersByUserId(Long userId);

    /**
     * Get specific order by ID and userId (ensures user owns the order)
     */
    GeneralResponse<OrderResponse> getOrderByIdAndUserId(UUID orderId, Long userId);

    /**
     * Cancel order (only if user owns it and status is PENDING)
     */
    GeneralResponse<OrderResponse> cancelOrder(UUID orderId, Long userId);

    // ============================================
    // ADMIN METHODS (Admin only)
    // ============================================

    /**
     * Get all orders (Admin only)
     */
    GeneralResponse<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest);

    /**
     * Get order by ID (Admin only)
     */
    GeneralResponse<OrderResponse> getOrderById(HttpServletRequest httpRequest, UUID orderId);

    /**
     * Get all orders of specific user (Admin only)
     */
    GeneralResponse<List<OrderResponse>> getOrdersByUserIdAdmin(HttpServletRequest httpRequest, Long userId);

    /**
     * Update order (Admin only)
     */
    GeneralResponse<OrderResponse> updateOrder(HttpServletRequest httpRequest, UUID orderId, OrderRequest request);

    /**
     * Delete order (Admin only)
     */
    GeneralResponse<OrderDeleteResponse> deleteOrder(HttpServletRequest httpRequest, UUID orderId);
}