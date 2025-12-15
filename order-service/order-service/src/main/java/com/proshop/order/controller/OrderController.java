package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.dto.request.ApplyVoucherRequest;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.request.UpdateOrderStatusRequest;
import com.proshop.order.dto.response.BestSellerResponse;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderDetailResponse;
import com.proshop.order.dto.response.OrderResponse;
import com.proshop.order.dto.response.ResponseStatus;
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
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }

    // ============================================
    // USER ENDPOINTS (Authenticated)
    // ============================================

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<OrderResponse>> createOrder(
        @RequestBody OrderCreateRequest request,
        HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Creating order for user: {}", userId);
        request.setUserId(userId);

        GeneralResponse<OrderResponse> response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getMyOrders(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        log.info("Getting orders for user: {}", userId);

        GeneralResponse<List<OrderResponse>> response = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> getMyOrderById(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        log.info("Getting order {} for user: {}", orderId, userId);

        GeneralResponse<OrderResponse> response = orderService.getOrderByIdAndUserId(orderId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/my-orders/{orderId}/cancel")
    public ResponseEntity<GeneralResponse<OrderResponse>> cancelMyOrder(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        log.info("Cancelling order {} for user: {}", orderId, userId);

        GeneralResponse<OrderResponse> response = orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getAllOrders(HttpServletRequest httpRequest) {
        log.info("Getting all orders (admin)");
        GeneralResponse<List<OrderResponse>> response = orderService.getAllOrders(httpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> getOrderById(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest httpRequest) {
        log.info("Getting order {} (admin)", orderId);
        GeneralResponse<OrderResponse> response = orderService.getOrderById(httpRequest, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<GeneralResponse<List<OrderResponse>>> getOrdersByUserId(
        @PathVariable("userId") Long userId,
        HttpServletRequest httpRequest) {
        log.info("Getting orders for user {} (admin)", userId);
        GeneralResponse<List<OrderResponse>> response = orderService.getOrdersByUserIdAdmin(httpRequest, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderResponse>> updateOrder(
        @PathVariable("orderId") UUID orderId,
        @RequestBody OrderRequest request,
        HttpServletRequest httpRequest) {
        log.info("Updating order {} (user)", orderId);
        GeneralResponse<OrderResponse> response = orderService.updateOrder(httpRequest, orderId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<GeneralResponse<OrderResponse>> updateOrderStatus(
        @PathVariable("orderId") UUID orderId,
        @RequestBody UpdateOrderStatusRequest request,
        HttpServletRequest httpRequest) {
        log.info("Updating order {} status to {} (admin)", orderId, request.getStatus());
        GeneralResponse<OrderResponse> response = orderService.updateOrderStatus(httpRequest, orderId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{orderId}")
    public ResponseEntity<GeneralResponse<OrderDeleteResponse>> deleteOrder(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest httpRequest) {
        log.info("Deleting order {} (admin)", orderId);
        GeneralResponse<OrderDeleteResponse> response = orderService.deleteOrder(httpRequest, orderId);
        return ResponseEntity.ok(response);
    }
    // USER ENDPOINTS
    @GetMapping("/detail/{orderId}")
    public GeneralResponse<OrderDetailResponse> getOrderDetail(
        @PathVariable("orderId") UUID orderId,
        HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
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

    @GetMapping("/best-sellers")
    public ResponseEntity<List<BestSellerResponse>> getBestSellers() {
        List<BestSellerResponse> list = orderService.getTopSellingProducts();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/check-purchase")
    public ResponseEntity<GeneralResponse<Boolean>> checkUserPurchase(
            @RequestParam UUID productId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        log.info("Checking if user {} has purchased product {}", userId, productId);

        boolean hasPurchased = orderService.hasUserPurchasedProduct(userId, productId);
        return ResponseEntity.ok(new GeneralResponse<Boolean>(
            ResponseStatus.SUCCESS_STATUS,
            hasPurchased,
            null
        ));
    }

    // ============================================
    // STATISTICS ENDPOINTS (ADMIN ONLY)
    // ============================================

    @GetMapping("/statistics/revenue-summary")
    public ResponseEntity<GeneralResponse<com.proshop.order.dto.response.RevenueSummaryResponse>> getRevenueSummary(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            HttpServletRequest request) {
        log.info("Getting revenue summary (admin)");

        com.proshop.order.dto.response.RevenueSummaryResponse response =
            orderService.getRevenueSummary(startDate, endDate, request);

        return ResponseEntity.ok(new GeneralResponse<>(
            ResponseStatus.SUCCESS_STATUS,
            response,
            null
        ));
    }

    @GetMapping("/statistics/orders-by-status")
    public ResponseEntity<GeneralResponse<java.util.Map<String, Long>>> getOrdersByStatus(
            HttpServletRequest request) {
        log.info("Getting orders by status (admin)");

        java.util.Map<String, Long> response = orderService.getOrdersByStatus(request);

        return ResponseEntity.ok(new GeneralResponse<>(
            ResponseStatus.SUCCESS_STATUS,
            response,
            null
        ));
    }

    @GetMapping("/statistics/today")
    public ResponseEntity<GeneralResponse<com.proshop.order.dto.response.TodayStatsResponse>> getTodayStats(
            HttpServletRequest request) {
        log.info("Getting today's statistics (admin)");

        com.proshop.order.dto.response.TodayStatsResponse response = orderService.getTodayStats(request);

        return ResponseEntity.ok(new GeneralResponse<>(
            ResponseStatus.SUCCESS_STATUS,
            response,
            null
        ));
    }

    // ============================================
    // INTERNAL SERVICE ENDPOINTS (for sale-service)
    // ============================================

    @PutMapping("/{orderId}/apply-voucher")
    public ResponseEntity<GeneralResponse<OrderResponse>> applyVoucherToOrder(
        @PathVariable("orderId") UUID orderId,
        @RequestBody ApplyVoucherRequest request) {
        log.info("Applying voucher {} to order {} (internal service call)", request.getVoucherCode(), orderId);
        GeneralResponse<OrderResponse> response = orderService.applyVoucherToOrder(orderId, request);
        return ResponseEntity.ok(response);
    }
}