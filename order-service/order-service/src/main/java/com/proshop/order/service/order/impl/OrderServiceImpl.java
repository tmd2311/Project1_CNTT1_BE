package com.proshop.order.service.order.impl;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.client.UserClient;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderItemEntity;
import com.proshop.order.entity.OrderStatus;
import com.proshop.order.repository.OrderItemRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.service.order.OrderService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private OrderItemRepository orderItemRepository;
    // ============================================
    // USER METHODS
    // ============================================

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Đơn hàng phải có ít nhất 1 sản phẩm", "Invalid request"),
                    null,
                    null
            );
        }
        // Validate all products exist and get product details
        List<ProductResponse> products = new ArrayList<>();
        for (var item : request.getItems()) {
            try {
                log.info("📞 Calling Product Service to validate product: {}", item.getProductId());

                // Lấy wrapped response và extract data
                GeneralResponse<ProductResponse> productResponse = productClient.getProductById(item.getProductId());
                ProductResponse product = productResponse.getData(); // ← Lấy data từ wrapper

                if (product == null) {
                    return new GeneralResponse<>(
                            new ResponseStatus("404",
                                    "Không tìm thấy sản phẩm với ID: " + item.getProductId(),
                                    "Product not found"),
                            null,
                            null
                    );
                }

                // Validate price
                if (product.getPrice() == null || product.getPrice() <= 0) {
                    return new GeneralResponse<>(
                            new ResponseStatus("400",
                                    "Giá sản phẩm không hợp lệ",
                                    "Invalid product price"),
                            null,
                            null
                    );
                }

                // Validate quantity
                if (item.getQuantity() <= 0) {
                    return new GeneralResponse<>(
                            new ResponseStatus("400",
                                    "Số lượng sản phẩm phải lớn hơn 0",
                                    "Invalid quantity"),
                            null,
                            null
                    );
                }

                products.add(product);
                log.info("✅ Successfully validated product: {} (price: {})",
                        product.getId(), product.getPrice());

            } catch (FeignException.NotFound e) {
                log.error("❌ Product not found (404): {}", item.getProductId());
                return new GeneralResponse<>(
                        new ResponseStatus("404",
                                "Không tìm thấy sản phẩm với ID: " + item.getProductId(),
                                "Product not found"),
                        null,
                        null
                );
            } catch (Exception e) {
                log.error("❌ Error calling product service for product {}: {}",
                        item.getProductId(), e.getMessage(), e);
                return new GeneralResponse<>(
                        new ResponseStatus("500",
                                "Lỗi khi kiểm tra sản phẩm",
                                "Error validating product"),
                        null,
                        null
                );
            }
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        try {
            for (int i = 0; i < request.getItems().size(); i++) {
                var item = request.getItems().get(i);
                ProductResponse product = products.get(i);

                BigDecimal itemPrice = BigDecimal.valueOf(product.getPrice());
                BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal itemTotal = itemPrice.multiply(itemQuantity);

                totalAmount = totalAmount.add(itemTotal);
            }

            log.info("💰 Calculated total amount: {}", totalAmount);

        } catch (Exception e) {
            log.error("❌ Error calculating total amount: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi tính tổng tiền", "Error calculating total"),
                    null,
                    null
            );
        }

        // Validate total amount
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Tổng tiền đơn hàng phải lớn hơn 0", "Invalid total amount"),
                    null,
                    null
            );
        }

        // Create order entity
        try {
            OrderEntity order = OrderEntity.builder()
                    .userId(request.getUserId())
                    .totalAmount(totalAmount)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Save order first to get orderId
            order = orderRepository.save(order);

            // ← NEW: Create and save order items
            List<OrderItemEntity> orderItems = new ArrayList<>();
            for (int i = 0; i < request.getItems().size(); i++) {
                var item = request.getItems().get(i);
                ProductResponse product = products.get(i);

                BigDecimal itemPrice = BigDecimal.valueOf(product.getPrice());
                BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal itemSubtotal = itemPrice.multiply(itemQuantity);

                OrderItemEntity orderItem = OrderItemEntity.builder()
                        .order(order)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(itemPrice)
                        .subtotal(itemSubtotal)
                        .createdAt(LocalDateTime.now())
                        .build();

                orderItems.add(orderItem);
            }

            orderItemRepository.saveAll(orderItems);
            // ← END NEW

            log.info("✅ Created order {} for user {} with {} items and total amount {}",
                    order.getOrderId(), request.getUserId(), orderItems.size(), totalAmount);

            OrderResponse data = new OrderResponse(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    order.getStatus().name(),
                    order.getCreatedAt()
            );

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("❌ Error creating order: {}", e.getMessage(), e);
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi tạo đơn hàng", "Error creating order"),
                    null,
                    null
            );
        }
    }

    @Override
    public GeneralResponse<List<OrderResponse>> getOrdersByUserId(Long userId) {
        log.info("Getting orders for user: {}", userId);

        try {
            List<OrderEntity> orders = orderRepository.findByUserId(userId);

            List<OrderResponse> data = orders.stream()
                    .map(o -> new OrderResponse(
                            o.getOrderId(),
                            o.getUserId(),
                            o.getTotalAmount(),
                            o.getStatus().name(),
                            o.getCreatedAt()
                    ))
                    .toList();

            log.info("✅ Found {} orders for user {}", data.size(), userId);

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("Error getting orders for user {}: {}", userId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy danh sách đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderByIdAndUserId(UUID orderId, Long userId) {
        log.info("Getting order {} for user {}", orderId, userId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        // Check if order belongs to user
                        if (order.getUserId() != userId) {
                            log.warn("User {} attempted to access order {} which belongs to user {}",
                                    userId, orderId, order.getUserId());
                            return new GeneralResponse<OrderResponse>(
                                    new ResponseStatus("403", "Bạn không có quyền truy cập đơn hàng này", "Forbidden"),
                                    null,
                                    null
                            );
                        }

                        OrderResponse data = new OrderResponse(
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus().name(),
                                order.getCreatedAt()
                        );

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error getting order {} for user {}: {}", orderId, userId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy thông tin đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> cancelOrder(UUID orderId, Long userId) {
        log.info("Cancelling order {} for user {}", orderId, userId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        // Check if order belongs to user
                        if (order.getUserId() != userId) {
                            log.warn("User {} attempted to cancel order {} which belongs to user {}",
                                    userId, orderId, order.getUserId());
                            return new GeneralResponse<OrderResponse>(
                                    new ResponseStatus("403", "Bạn không có quyền hủy đơn hàng này", "Forbidden"),
                                    null,
                                    null
                            );
                        }

                        // Check if order can be cancelled (only PENDING orders)
                        if (order.getStatus() != OrderStatus.PENDING) {
                            log.warn("Cannot cancel order {} with status {}", orderId, order.getStatus());
                            return new GeneralResponse<OrderResponse>(
                                    new ResponseStatus("400",
                                            "Chỉ có thể hủy đơn hàng đang chờ xử lý",
                                            "Cannot cancel order"),
                                    null,
                                    null
                            );
                        }

                        // Cancel order
                        order.setStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);

                        log.info("✅ Cancelled order {} for user {}", orderId, userId);

                        OrderResponse data = new OrderResponse(
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus().name(),
                                order.getCreatedAt()
                        );

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error cancelling order {} for user {}: {}", orderId, userId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi hủy đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    // ============================================
    // ADMIN METHODS
    // ============================================

    @Override
    public GeneralResponse<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest) {
        checkAdminRole(httpRequest);
        log.info("Getting all orders (admin)");

        try {
            List<OrderResponse> data = orderRepository.findAll().stream()
                    .map(o -> new OrderResponse(
                            o.getOrderId(),
                            o.getUserId(),
                            o.getTotalAmount(),
                            o.getStatus().name(),
                            o.getCreatedAt()
                    ))
                    .toList();

            log.info("✅ Found {} orders (admin)", data.size());

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("Error getting all orders (admin): {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy danh sách đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderById(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Getting order {} (admin)", orderId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        OrderResponse data = new OrderResponse(
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus().name(),
                                order.getCreatedAt()
                        );

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error getting order {} (admin): {}", orderId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy thông tin đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    public GeneralResponse<List<OrderResponse>> getOrdersByUserIdAdmin(HttpServletRequest httpRequest, Long userId) {
        checkAdminRole(httpRequest);
        log.info("Getting orders for user {} (admin)", userId);

        try {
            List<OrderEntity> orders = orderRepository.findByUserId(userId);

            List<OrderResponse> data = orders.stream()
                    .map(o -> new OrderResponse(
                            o.getOrderId(),
                            o.getUserId(),
                            o.getTotalAmount(),
                            o.getStatus().name(),
                            o.getCreatedAt()
                    ))
                    .toList();

            log.info("✅ Found {} orders for user {} (admin)", data.size(), userId);

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("Error getting orders for user {} (admin): {}", userId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy danh sách đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> updateOrder(HttpServletRequest httpRequest, UUID orderId, OrderRequest request) {
        checkAdminRole(httpRequest);
        log.info("Updating order {} (admin)", orderId);

        // Validate request
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Tổng tiền phải lớn hơn 0", "Invalid total amount"),
                    null,
                    null
            );
        }

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        order.setTotalAmount(request.getTotalAmount());
                        order.setStatus(OrderStatus.PENDING);
                        orderRepository.save(order);

                        log.info("✅ Updated order {} with new total amount {} (admin)", orderId, request.getTotalAmount());

                        OrderResponse data = new OrderResponse(
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus().name(),
                                order.getCreatedAt()
                        );

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found for update: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error updating order {} (admin): {}", orderId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi cập nhật đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderDeleteResponse> deleteOrder(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Deleting order {} (admin)", orderId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        orderRepository.delete(order);

                        log.info("✅ Deleted order {} for user {} (admin)", order.getOrderId(), order.getUserId());

                        return new GeneralResponse<>(
                                ResponseStatus.SUCCESS_STATUS,
                                new OrderDeleteResponse(order.getOrderId(), order.getUserId()),
                                null
                        );
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found for deletion: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error deleting order {} (admin): {}", orderId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi xóa đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    // ============================================
    // HELPER METHODS
    // ============================================

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
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            List<String> roles = jwtUtil.extractRoles(token);
            log.info("✅ Successfully extracted roles from token: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("❌ Failed to extract roles from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
    @Override
    public GeneralResponse<OrderDetailResponse> getOrderDetailByIdAndUserId(UUID orderId, Long userId) {
        log.info("Getting order detail {} for user {}", orderId, userId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        // Check if order belongs to user
                        if (!order.getUserId().equals(userId)) {
                            log.warn("User {} attempted to access order {} which belongs to user {}",
                                    userId, orderId, order.getUserId());
                            return new GeneralResponse<OrderDetailResponse>(
                                    new ResponseStatus("403", "Bạn không có quyền truy cập đơn hàng này", "Forbidden"),
                                    null,
                                    null
                            );
                        }

                        // Get order items
                        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderOrderId(orderId);

                        // Build order detail response
                        List<OrderDetailResponse.OrderItemDetail> itemDetails = new ArrayList<>();

                        for (OrderItemEntity item : orderItems) {
                            // Try to get product info from Product Service
                            String productName = "Unknown Product";
                            Double productPrice = item.getPrice().doubleValue();

                            try {
                                GeneralResponse<ProductResponse> productResponse = productClient.getProductById(item.getProductId());
                                ProductResponse product = productResponse.getData();
                                if (product != null) {
                                    productName = product.getName();
                                    // Use current price from Product Service (optional)
                                    // productPrice = product.getPrice();
                                }
                            } catch (Exception e) {
                                log.warn("Could not fetch product info for productId: {}", item.getProductId());
                            }

                            OrderDetailResponse.OrderItemDetail itemDetail = OrderDetailResponse.OrderItemDetail.builder()
                                    .productId(item.getProductId())
                                    .productName(productName)
                                    .productPrice(productPrice)
                                    .quantity(item.getQuantity())
                                    .subtotal(item.getSubtotal())
                                    .build();

                            itemDetails.add(itemDetail);
                        }

                        OrderDetailResponse data = OrderDetailResponse.builder()
                                .orderId(order.getOrderId())
                                .userId(order.getUserId())
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus().name())
                                .createdAt(order.getCreatedAt())
                                .items(itemDetails)
                                .build();

                        log.info("✅ Found order detail for order {} with {} items", orderId, itemDetails.size());

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error getting order detail {} for user {}: {}", orderId, userId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy chi tiết đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

// ============================================
// ADMIN METHOD - Get Order Detail
// ============================================

    @Override
    public GeneralResponse<OrderDetailResponse> getOrderDetailById(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Getting order detail {} (admin)", orderId);

        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        // Get order items
                        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderOrderId(orderId);

                        // Build order detail response
                        List<OrderDetailResponse.OrderItemDetail> itemDetails = new ArrayList<>();

                        for (OrderItemEntity item : orderItems) {
                            // Try to get product info from Product Service
                            String productName = "Unknown Product";
                            Double productPrice = item.getPrice().doubleValue();

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
                                    .productPrice(productPrice)
                                    .quantity(item.getQuantity())
                                    .subtotal(item.getSubtotal())
                                    .build();

                            itemDetails.add(itemDetail);
                        }

                        OrderDetailResponse data = OrderDetailResponse.builder()
                                .orderId(order.getOrderId())
                                .userId(order.getUserId())
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus().name())
                                .createdAt(order.getCreatedAt())
                                .items(itemDetails)
                                .build();

                        log.info("✅ Found order detail for order {} with {} items (admin)", orderId, itemDetails.size());

                        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found: {} (admin)", orderId);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error getting order detail {} (admin): {}", orderId, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy chi tiết đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }
}