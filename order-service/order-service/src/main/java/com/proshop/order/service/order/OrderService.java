package com.proshop.order.service.order;

import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    // USER METHODS
    GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request);
    GeneralResponse<List<OrderResponse>> getOrdersByUserId(Long userId);
    GeneralResponse<OrderResponse> getOrderByIdAndUserId(UUID orderId, Long userId);
    GeneralResponse<OrderDetailResponse> getOrderDetailByIdAndUserId(UUID orderId, Long userId); // ← NEW
    GeneralResponse<OrderResponse> cancelOrder(UUID orderId, Long userId);
    List<BestSellerResponse> getTopSellingProducts();

    // ADMIN METHODS
    GeneralResponse<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest);
    GeneralResponse<OrderResponse> getOrderById(HttpServletRequest httpRequest, UUID orderId);
    GeneralResponse<OrderDetailResponse> getOrderDetailById(HttpServletRequest httpRequest, UUID orderId); // ← NEW
    GeneralResponse<List<OrderResponse>> getOrdersByUserIdAdmin(HttpServletRequest httpRequest, Long userId);
    GeneralResponse<OrderResponse> updateOrder(HttpServletRequest httpRequest, UUID orderId, OrderRequest request);
    GeneralResponse<OrderDeleteResponse> deleteOrder(HttpServletRequest httpRequest, UUID orderId);

}