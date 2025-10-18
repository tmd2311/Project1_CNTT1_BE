package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderDetailResponse;
import com.proshop.order.dto.response.OrderResponse;
import com.proshop.order.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    /**
     * Extract userId from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    // ============================================
    // USER ENDPOINTS (Authenticated)
    // ============================================

    /**
     * Create order for current user (userId from token)
     */
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<OrderResponse>> createOrder(
            @RequestBody OrderCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Creating order for user: {}", userId);

        // Set userId from token to ensure security
        request.setUserId(userId);

        GeneralResponse<OrderResponse> response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders of current user (userId from token)
     */
    @GetMapping("/my-orders")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getMyOrders(
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Getting orders for user: {}", userId);

        GeneralResponse<List<OrderResponse>> response = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get specific order by ID (only if belongs to current user)
     */
    @GetMapping("/my-orders/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> getMyOrderById(
            @PathVariable UUID orderId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Getting order {} for user: {}", orderId, userId);

        GeneralResponse<OrderResponse> response = orderService.getOrderByIdAndUserId(orderId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel order (only if belongs to current user and status is PENDING)
     */
    @PutMapping("/my-orders/{orderId}/cancel")
    public ResponseEntity<GeneralResponse<OrderResponse>> cancelMyOrder(
            @PathVariable UUID orderId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Cancelling order {} for user: {}", orderId, userId);

        GeneralResponse<OrderResponse> response = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    /**
     * Get all orders (Admin only)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getAllOrders(
            HttpServletRequest httpRequest) {

        log.info("Getting all orders (admin)");
        GeneralResponse<List<OrderResponse>> response = orderService.getAllOrders(httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order by ID (Admin only)
     */
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> getOrderById(
            @PathVariable UUID orderId,
            HttpServletRequest httpRequest) {

        log.info("Getting order {} (admin)", orderId);
        GeneralResponse<OrderResponse> response = orderService.getOrderById(httpRequest, orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders of a specific user (Admin only)
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getOrdersByUserId(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {

        log.info("Getting orders for user {} (admin)", userId);
        GeneralResponse<List<OrderResponse>> response = orderService.getOrdersByUserIdAdmin(httpRequest, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status (Admin only)
     */
    @PutMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderRequest request,
            HttpServletRequest httpRequest) {

        log.info("Updating order {} (admin)", orderId);
        GeneralResponse<OrderResponse> response = orderService.updateOrder(httpRequest, orderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete order (Admin only)
     */
    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderDeleteResponse>> deleteOrder(
            @PathVariable UUID orderId,
            HttpServletRequest httpRequest) {

        log.info("Deleting order {} (admin)", orderId);
        GeneralResponse<OrderDeleteResponse> response = orderService.deleteOrder(httpRequest, orderId);
        return ResponseEntity.ok(response);
    }
    // USER ENDPOINTS
    @GetMapping("/detail/{orderId}")
    public GeneralResponse<OrderDetailResponse> getOrderDetail(
            @PathVariable UUID orderId,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        log.info("User {} getting order detail: {}", userId, orderId);
        return orderService.getOrderDetailByIdAndUserId(orderId, userId);
    }

    // ADMIN ENDPOINTS
    @GetMapping("/admin/detail/{orderId}")
    public GeneralResponse<OrderDetailResponse> getOrderDetailAdmin(
            @PathVariable UUID orderId,
            HttpServletRequest request) {
        log.info("Admin getting order detail: {}", orderId);
        return orderService.getOrderDetailById(request, orderId);
    }
}