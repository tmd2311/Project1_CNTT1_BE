package com.proshop.order.service.order.impl;

import com.proshop.order.client.ProductClient;
import com.proshop.order.client.UserClient;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderStatus;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.service.order.OrderService;
import feign.FeignException;
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

    @Override
    public GeneralResponse<List<OrderResponse>> getAllOrders() {
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

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("Error getting all orders: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy danh sách đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderById(UUID id) {
        try {
            return orderRepository.findById(id)
                    .map(order -> new GeneralResponse<>(
                            ResponseStatus.SUCCESS_STATUS,
                            new OrderResponse(
                                    order.getOrderId(),
                                    order.getUserId(),
                                    order.getTotalAmount(),
                                    order.getStatus().name(),
                                    order.getCreatedAt()
                            ),
                            null
                    ))
                    .orElseGet(() -> new GeneralResponse<>(
                            new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                            null,
                            null
                    ));
        } catch (Exception e) {
            log.error("Error getting order by id {}: {}", id, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi lấy thông tin đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request) {
        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Đơn hàng phải có ít nhất 1 sản phẩm", "Invalid request"),
                    null,
                    null
            );
        }

        // Validate user exists
        UserResponse user;
        try {
            user = userClient.getUserById(request.getUserId());
            if (user == null) {
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", request.getUserId());
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("Error calling user service: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi kiểm tra người dùng", "Error validating user"),
                    null,
                    null
            );
        }

        // Validate all products exist and get product details
        List<ProductResponse> products = new ArrayList<>();
        for (var item : request.getItems()) {
            try {
                ProductResponse product = productClient.getProductById(item.getProductId());
                if (product == null) {
                    return new GeneralResponse<>(
                            new ResponseStatus("404",
                                    "Không tìm thấy sản phẩm với ID: " + item.getProductId(),
                                    "Product not found"),
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
            } catch (FeignException.NotFound e) {
                log.error("Product not found: {}", item.getProductId());
                return new GeneralResponse<>(
                        new ResponseStatus("404",
                                "Không tìm thấy sản phẩm với ID: " + item.getProductId(),
                                "Product not found"),
                        null,
                        null
                );
            } catch (Exception e) {
                log.error("Error calling product service for product {}: {}", item.getProductId(), e.getMessage());
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
        } catch (Exception e) {
            log.error("Error calculating total amount: {}", e.getMessage());
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
                    .userId(user.getId())
                    .totalAmount(totalAmount)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderRepository.save(order);

            log.info("Created order {} for user {} with total amount {}",
                    order.getOrderId(), user.getId(), totalAmount);

            OrderResponse data = new OrderResponse(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    order.getStatus().name(),
                    order.getCreatedAt()
            );

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi tạo đơn hàng", "Error creating order"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> updateOrder(UUID id, OrderRequest request) {
        // Validate request
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Tổng tiền phải lớn hơn 0", "Invalid total amount"),
                    null,
                    null
            );
        }

        try {
            return orderRepository.findById(id)
                    .map(order -> {
                        order.setTotalAmount(request.getTotalAmount());
                        order.setStatus(OrderStatus.PENDING);
                        orderRepository.save(order);

                        log.info("Updated order {} with new total amount {}", id, request.getTotalAmount());

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
                        log.warn("Order not found for update: {}", id);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error updating order {}: {}", id, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi cập nhật đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional
    public GeneralResponse<OrderDeleteResponse> deleteOrder(UUID id) {
        try {
            return orderRepository.findById(id)
                    .map(order -> {
                        orderRepository.delete(order);

                        log.info("Deleted order {} for user {}", order.getOrderId(), order.getUserId());

                        return new GeneralResponse<>(
                                ResponseStatus.SUCCESS_STATUS,
                                new OrderDeleteResponse(order.getOrderId(), order.getUserId()),
                                null
                        );
                    })
                    .orElseGet(() -> {
                        log.warn("Order not found for deletion: {}", id);
                        return new GeneralResponse<>(
                                new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                                null,
                                null
                        );
                    });
        } catch (Exception e) {
            log.error("Error deleting order {}: {}", id, e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi khi xóa đơn hàng", "Internal Server Error"),
                    null,
                    null
            );
        }
    }
}