package com.proshop.order.service.order.impl;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.client.UserClient;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.request.UpdateOrderStatusRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderItemEntity;
import com.proshop.order.entity.OrderStatus;
import com.proshop.order.repository.OrderItemRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.service.order.OrderService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final JwtUtil jwtUtil;
    private final OrderItemRepository orderItemRepository;

    // ============================================
    // USER METHODS
    // ============================================


    @Override
    @Transactional
    public GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResException(ResErrorCode.ORDER_ITEMS_REQUIRED);
        }

        // Validate all products exist and get product details
        List<ProductResponse> products = validateAndGetProducts(request);

        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(request, products);

        // Validate total amount
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResException(ResErrorCode.ORDER_INVALID_TOTAL);
        }


        if(request.getShippingAddress() == null || request.getShippingAddress().isEmpty()) {
            UserInfoResponse user=userClient.getUserById(request.getUserId()).getData();
            if (user.getCurrentAddress() == null || user.getCurrentAddress().isEmpty()) {
                throw new ResException(ResErrorCode.ORDER_SHIPPINGADDRESS_REQUIRED);
            }
        }

        // Create order entity
        try {
            OrderEntity order = OrderEntity.builder()
                    .userId(request.getUserId())
                    .totalAmount(totalAmount)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            String address;
            if(request.getShippingAddress() == null || request.getShippingAddress().isEmpty()) {
                UserInfoResponse user=userClient.getUserById(request.getUserId()).getData();
                if (user.getCurrentAddress() == null || user.getCurrentAddress().isEmpty()) {
                    throw new ResException(ResErrorCode.ORDER_SHIPPINGADDRESS_REQUIRED);
                }
                address=user.getCurrentAddress();
            }else{
                address=request.getShippingAddress();
            }
            order.setShippingAddress(address);
            // Save order first to get orderId
            order = orderRepository.save(order);

            // Create and save order items
            List<OrderItemEntity> orderItems = createOrderItems(order, request, products);
            orderItemRepository.saveAll(orderItems);

            log.info("Created order {} for user {} with {} items and total amount {}, address: {}",
                    order.getOrderId(), request.getUserId(), orderItems.size(), totalAmount, request.getShippingAddress());

            OrderResponse data = new OrderResponse(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    order.getStatus().name(),
                    order.getCreatedAt(),
                    order.getShippingAddress()
            );

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.ORDER_CREATION_ERROR);
        }
    }

    @Override
    public GeneralResponse<List<OrderResponse>> getOrdersByUserId(Long userId) {
        log.info("Getting orders for user: {}", userId);

        List<OrderEntity> orders = orderRepository.findByUserId(userId);

        List<OrderResponse> data = orders.stream()
                .map(o -> new OrderResponse(
                        o.getOrderId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus().name(),
                        o.getCreatedAt(),
                        o.getShippingAddress()
                ))
                .toList();

        log.info("Found {} orders for user {}", data.size(), userId);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderByIdAndUserId(UUID orderId, Long userId) {
        log.info("Getting order {} for user {}", orderId, userId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        // Check if order belongs to user
        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to access order {} which belongs to user {}",
                    userId, orderId, order.getUserId());
            throw new ResException(ResErrorCode.ORDER_ACCESS_DENIED);
        }

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> cancelOrder(UUID orderId, Long userId) {
        log.info("Cancelling order {} for user {}", orderId, userId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        // Check if order belongs to user
        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to cancel order {} which belongs to user {}",
                    userId, orderId, order.getUserId());
            throw new ResException(ResErrorCode.ORDER_ACCESS_DENIED);
        }

        // Check if order can be cancelled (only PENDING orders)
        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("Cannot cancel order {} with status {}", orderId, order.getStatus());
            throw new ResException(ResErrorCode.ORDER_CANNOT_CANCEL);
        }

        // Cancel order
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Cancelled order {} for user {}", orderId, userId);

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public List<BestSellerResponse> getTopSellingProducts() {
        List<BestSellerResponse> all = orderItemRepository.findTopSellingProducts();
        return all.stream().limit(10).toList();
    }

    // ============================================
    // ADMIN METHODS
    // ============================================

    @Override
    public GeneralResponse<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest) {
        checkAdminRole(httpRequest);
        log.info("Getting all orders (admin)");

        List<OrderResponse> data = orderRepository.findAll().stream()
                .map(o -> new OrderResponse(
                        o.getOrderId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus().name(),
                        o.getCreatedAt(),
                        o.getShippingAddress()
                ))
                .toList();

        log.info("Found {} orders (admin)", data.size());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderById(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Getting order {} (admin)", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<List<OrderResponse>> getOrdersByUserIdAdmin(HttpServletRequest httpRequest, Long userId) {
        checkAdminRole(httpRequest);
        log.info("Getting orders for user {} (admin)", userId);

        List<OrderEntity> orders = orderRepository.findByUserId(userId);

        List<OrderResponse> data = orders.stream()
                .map(o -> new OrderResponse(
                        o.getOrderId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus().name(),
                        o.getCreatedAt(),
                        o.getShippingAddress()
                ))
                .toList();

        log.info("Found {} orders for user {} (admin)", data.size(), userId);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> updateOrder(HttpServletRequest httpRequest, UUID orderId, OrderRequest request) {

        log.info("Updating order {} (user)", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));


        order.setShippingAddress(request.getShippingAddress());

        orderRepository.save(order);

        log.info("Updated order {} (admin)", orderId);

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<OrderDeleteResponse> deleteOrder(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Deleting order {} (admin)", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        orderRepository.delete(order);

        log.info("Deleted order {} for user {} (admin)", order.getOrderId(), order.getUserId());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                new OrderDeleteResponse(order.getOrderId(), order.getUserId()),
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> updateOrderStatus(HttpServletRequest httpRequest, UUID orderId, UpdateOrderStatusRequest request) {
        checkAdminRole(httpRequest);
        log.info("Updating order {} status to {} (admin)", orderId, request.getStatus());

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        orderRepository.save(order);

        if (request.getNote() != null && !request.getNote().isEmpty()) {
            log.info("Order {} status changed from {} to {} - Note: {}", orderId, oldStatus, request.getStatus(), request.getNote());
        } else {
            log.info("Order {} status changed from {} to {}", orderId, oldStatus, request.getStatus());
        }

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getShippingAddress()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<OrderDetailResponse> getOrderDetailByIdAndUserId(UUID orderId, Long userId) {
        log.info("Getting order detail {} for user {}", orderId, userId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        // Check if order belongs to user
        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to access order {} which belongs to user {}",
                    userId, orderId, order.getUserId());
            throw new ResException(ResErrorCode.ORDER_ACCESS_DENIED);
        }

        OrderDetailResponse data = buildOrderDetailResponse(order);

        log.info("Found order detail for order {} with {} items", orderId, data.getItems().size());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<OrderDetailResponse> getOrderDetailById(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Getting order detail {} (admin)", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        OrderDetailResponse data = buildOrderDetailResponse(order);

        log.info("Found order detail for order {} with {} items (admin)", orderId, data.getItems().size());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Validate products and get product details
     */
    private List<ProductResponse> validateAndGetProducts(OrderCreateRequest request) {
        List<ProductResponse> products = new ArrayList<>();

        for (var item : request.getItems()) {
            try {
                log.info("Calling Product Service to validate product: {}", item.getProductId());

                GeneralResponse<ProductResponse> productResponse = productClient.getProductById(item.getProductId());
                ProductResponse product = productResponse.getData();

                if (product == null) {
                    throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND,
                            "Không tìm thấy sản phẩm với ID: " + item.getProductId());
                }

                // Validate price
                if (product.getPrice() == null || product.getPrice() <= 0) {
                    throw new ResException(ResErrorCode.ORDER_INVALID_PRICE);
                }

                // Validate quantity
                if (item.getQuantity() <= 0) {
                    throw new ResException(ResErrorCode.ORDER_INVALID_QUANTITY);
                }

                products.add(product);
                log.info("Successfully validated product: {} (price: {})",
                        product.getId(), product.getPrice());

            } catch (FeignException.NotFound e) {
                log.error("Product not found (404): {}", item.getProductId());
                throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND,
                        "Không tìm thấy sản phẩm với ID: " + item.getProductId());
            } catch (ResException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error calling product service for product {}: {}",
                        item.getProductId(), e.getMessage(), e);
                throw new ResException(ResErrorCode.PRODUCT_VALIDATION_ERROR);
            }
        }

        return products;
    }

    /**
     * Calculate total amount for order
     */
    private BigDecimal calculateTotalAmount(OrderCreateRequest request, List<ProductResponse> products) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            for (int i = 0; i < request.getItems().size(); i++) {
                var item = request.getItems().get(i);

                // Thêm try-catch riêng cho từng SKU
                try {
                    GeneralResponse<SKUResponse> skuResponse = productClient.getSkuById(item.getSkuId());

                    if (skuResponse == null || skuResponse.getData() == null) {
                        log.error("Không tìm thấy SKU với ID: {}", item.getSkuId());
                        throw new ResException(ResErrorCode.SKU_NOT_FOUND,
                                "Không tìm thấy SKU với ID: " + item.getSkuId());
                    }

                    BigDecimal itemPrice = BigDecimal.valueOf(skuResponse.getData().getPrice());
                    BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
                    BigDecimal itemTotal = itemPrice.multiply(itemQuantity);

                    totalAmount = totalAmount.add(itemTotal);

                } catch (FeignException.NotFound e) {
                    log.error("SKU not found (404): {}", item.getSkuId());
                    throw new ResException(ResErrorCode.SKU_NOT_FOUND,
                            "Không tìm thấy SKU với ID: " + item.getSkuId());
                }
            }

            log.info("Calculated total amount: {}", totalAmount);
            return totalAmount;

        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calculating total amount: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.ORDER_CALCULATION_ERROR);
        }
    }

    private List<OrderItemEntity> createOrderItems(OrderEntity order, OrderCreateRequest request,
                                                   List<ProductResponse> products) {
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (int i = 0; i < request.getItems().size(); i++) {
            var item = request.getItems().get(i);

            // Sửa: Dùng item.getSkuId() thay vì item.getProductId()
            GeneralResponse<SKUResponse> skuResponse = productClient.getSkuById(item.getSkuId());
            BigDecimal itemPrice = BigDecimal.valueOf(skuResponse.getData().getPrice());
            BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal itemSubtotal = itemPrice.multiply(itemQuantity);

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .skuId(item.getSkuId())
                    .quantity(item.getQuantity())
                    .price(itemPrice)
                    .subtotal(itemSubtotal)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    /**
     * Build order detail response with items
     */
    private OrderDetailResponse buildOrderDetailResponse(OrderEntity order) {
        // Get order items
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

        // Build order detail response
        List<OrderDetailResponse.OrderItemDetail> itemDetails = new ArrayList<>();

        for (OrderItemEntity item : orderItems) {
            // Try to get product info from Product Service
            String productName = "Unknown Product";
            double productPrice = Double.valueOf(productClient.getSkuById(item.getSkuId()).getData().getPrice());

            try {
                GeneralResponse<ProductResponse> productResponse = productClient.getProductById(item.getProductId());
                ProductResponse product = productResponse.getData();
                if (product != null) {
                    productName = product.getName();
                }
            } catch (Exception e) {
                log.warn("Could not fetch product info for productId: {}", item.getProductId());
            }

            OrderDetailResponse.OrderItemDetail itemDetail = OrderDetailResponse.OrderItemDetail.builder()
                    .productId(item.getProductId())
                    .productName(productName)
                    .skuId(item.getSkuId())
                    .productPrice(productPrice)
                    .quantity(item.getQuantity())
                    .subtotal(item.getSubtotal())
                    .build();

            itemDetails.add(itemDetail);
        }

        return OrderDetailResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .shippingAddress(order.getShippingAddress())
                .items(itemDetails)
                .build();
    }

    /**
     * Check if user has Admin role from JWT token
     */
    private void checkAdminRole(HttpServletRequest httpRequest) {
        List<String> roles = getRoleFromToken(httpRequest);

        boolean isAdmin = roles.stream().anyMatch(role -> role.equalsIgnoreCase("Admin"));
        if (!isAdmin) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED,
                    "Bạn không có quyền truy cập tài nguyên này (Admin only)");
        }
    }

    /**
     * Extract roles from JWT token
     */
    private List<String> getRoleFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            List<String> roles = jwtUtil.extractRoles(token);
            log.info("Successfully extracted roles from token: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("Failed to extract roles from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean hasUserPurchasedProduct(Long userId, UUID productId) {
        log.info("Checking if user {} has purchased product {}", userId, productId);

        List<OrderStatus> validStatuses = List.of(
            OrderStatus.DELIVERED,
            OrderStatus.COMPLETED
        );

        boolean hasPurchased = orderItemRepository.existsByUserIdAndProductIdAndOrderStatus(
            userId, productId, validStatuses
        );

        log.info("User {} has{} purchased product {}",
            userId, hasPurchased ? "" : " not", productId);

        return hasPurchased;
    }

    // ============================================
    // STATISTICS METHODS IMPLEMENTATION
    // ============================================

    @Override
    public RevenueSummaryResponse getRevenueSummary(LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        log.info("Getting revenue summary from {} to {}", startDate, endDate);

        // Check admin role
        checkAdminRole(request);

        // Default to current month if not specified
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now();
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        // Get current period revenue (only COMPLETED orders)
        BigDecimal currentRevenue = orderRepository.sumRevenueByStatusAndDateRange(
            OrderStatus.COMPLETED, start, end);

        if (currentRevenue == null) {
            currentRevenue = BigDecimal.ZERO;
        }

        // Get previous period revenue (same duration)
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate prevStartDate = startDate.minusDays(days + 1);
        LocalDate prevEndDate = endDate.minusDays(days + 1);

        BigDecimal previousRevenue = orderRepository.sumRevenueByStatusAndDateRange(
            OrderStatus.COMPLETED,
            prevStartDate.atStartOfDay(),
            prevEndDate.atTime(23, 59, 59));

        if (previousRevenue == null) {
            previousRevenue = BigDecimal.ZERO;
        }

        // Calculate percent change
        Double percentChange = 0.0;
        if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = currentRevenue.subtract(previousRevenue)
                .divide(previousRevenue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
        } else if (currentRevenue.compareTo(BigDecimal.ZERO) > 0) {
            percentChange = 100.0; // 100% increase if previous was 0
        }

        // Count orders in current period
        List<OrderEntity> currentOrders = orderRepository.findOrdersBetween(start, end);
        long orderCount = currentOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .count();

        log.info("Revenue summary: current={}, previous={}, change={}%",
            currentRevenue, previousRevenue, percentChange);

        return RevenueSummaryResponse.builder()
            .currentRevenue(currentRevenue)
            .previousRevenue(previousRevenue)
            .percentChange(percentChange)
            .orderCount(orderCount)
            .comparedTo("previous_period")
            .build();
    }

    @Override
    public java.util.Map<String, Long> getOrdersByStatus(HttpServletRequest request) {
        log.info("Getting orders count by status");

        // Check admin role
        checkAdminRole(request);

        List<Object[]> results = orderRepository.countOrdersByStatus();

        java.util.Map<String, Long> statusMap = new java.util.HashMap<>();

        for (Object[] row : results) {
            OrderStatus status = (OrderStatus) row[0];
            Long count = (Long) row[1];
            statusMap.put(status.name(), count);
        }

        // Ensure all statuses are present (even if count is 0)
        for (OrderStatus status : OrderStatus.values()) {
            statusMap.putIfAbsent(status.name(), 0L);
        }

        log.info("Orders by status: {}", statusMap);
        return statusMap;
    }

    @Override
    public TodayStatsResponse getTodayStats(HttpServletRequest request) {
        log.info("Getting today's statistics");

        // Check admin role
        checkAdminRole(request);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        // Get all orders from today
        List<OrderEntity> todayOrders = orderRepository.findOrdersBetween(startOfDay, endOfDay);

        // Calculate revenue (only COMPLETED orders)
        BigDecimal todayRevenue = todayOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .map(OrderEntity::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Count orders by status
        java.util.Map<String, Long> statusCounts = todayOrders.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                order -> order.getStatus().name(),
                java.util.stream.Collectors.counting()
            ));

        // Ensure all statuses are present
        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.putIfAbsent(status.name(), 0L);
        }

        log.info("Today's stats: {} orders, revenue={}", todayOrders.size(), todayRevenue);

        return TodayStatsResponse.builder()
            .orderCount(todayOrders.size())
            .revenue(todayRevenue)
            .ordersByStatus(statusCounts)
            .build();
    }
}