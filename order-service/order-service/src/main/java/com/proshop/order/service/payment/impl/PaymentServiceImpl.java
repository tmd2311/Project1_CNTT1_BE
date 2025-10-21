package com.proshop.order.service.payment.impl;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.request.ProductUpdateStockRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.*;
import com.proshop.order.repository.OrderItemRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.PaymentRepository;
import com.proshop.order.service.payment.PaymentService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final JwtUtil jwtUtil;

    // ============================================
    // USER METHODS
    // ============================================

    @Override
    public GeneralResponse<List<PaymentResponse>> getPaymentsByUserId(Long userId) {
        log.info("Getting payments for user: {}", userId);

        // Get all orders for user
        List<OrderEntity> userOrders = orderRepository.findByUserId(userId);
        List<UUID> orderIds = userOrders.stream()
                .map(OrderEntity::getOrderId)
                .toList();

        // Get payments for these orders
        List<PaymentEntity> payments = paymentRepository.findByOrderOrderIdIn(orderIds);

        List<PaymentResponse> data = payments.stream()
                .map(this::toPaymentResponse)
                .toList();

        log.info("✅ Found {} payments for user {}", data.size(), userId);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<PaymentResponse> getPaymentByIdAndUserId(UUID paymentId, Long userId) {
        log.info("Getting payment {} for user {}", paymentId, userId);

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResException(ResErrorCode.PAYMENT_NOT_FOUND));

        // Check if payment belongs to user's order
        if (!payment.getOrder().getUserId().equals(userId)) {
            log.warn("User {} attempted to access payment {} which belongs to user {}",
                    userId, paymentId, payment.getOrder().getUserId());
            throw new ResException(ResErrorCode.PAYMENT_ACCESS_DENIED);
        }

        PaymentResponse data = toPaymentResponse(payment);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<List<PaymentResponse>> getPaymentsByOrderId(UUID orderId, Long userId) {
        log.info("Getting payments for order {} by user {}", orderId, userId);

        // Verify order belongs to user
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to access payments for order {} which belongs to user {}",
                    userId, orderId, order.getUserId());
            throw new ResException(ResErrorCode.ORDER_ACCESS_DENIED);
        }

        List<PaymentEntity> payments = paymentRepository.findByOrderOrderId(orderId);

        List<PaymentResponse> data = payments.stream()
                .map(this::toPaymentResponse)
                .toList();

        log.info("✅ Found {} payments for order {}", data.size(), orderId);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentResponse> createPayment(PaymentRequest request, Long userId) {
        log.info("Creating payment for order {} by user {}", request.getOrderId(), userId);

        // Validate request
        validatePaymentRequest(request);

        // Verify order exists and belongs to user
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to create payment for order {} which belongs to user {}",
                    userId, request.getOrderId(), order.getUserId());
            throw new ResException(ResErrorCode.ORDER_ACCESS_DENIED);
        }

        // Check if order is in valid status for payment
        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("Cannot create payment for order {} with status {}", order.getOrderId(), order.getStatus());
            throw new ResException(ResErrorCode.ORDER_INVALID_STATUS_FOR_PAYMENT);
        }

        // Validate payment amount matches order total
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            log.warn("Payment amount {} does not match order total {} for order {}",
                    request.getAmount(), order.getTotalAmount(), order.getOrderId());
            throw new ResException(ResErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        try {
            PaymentEntity payment = PaymentEntity.builder()
                    .order(order)
                    .method(request.getMethod())
                    .amount(request.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paidAt(null)
                    .build();

            payment = paymentRepository.save(payment);

            log.info("✅ Created payment {} for order {} with amount {}",
                    payment.getPaymentId(), order.getOrderId(), payment.getAmount());

            PaymentResponse data = toPaymentResponse(payment);

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error creating payment: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.PAYMENT_CREATION_ERROR);
        }
    }

    // ============================================
    // ADMIN METHODS
    // ============================================

    @Override
    public GeneralResponse<List<PaymentResponse>> getAllPayments(HttpServletRequest httpRequest) {
        checkAdminRole(httpRequest);
        log.info("Getting all payments (admin)");

        List<PaymentResponse> data = paymentRepository.findAll().stream()
                .map(this::toPaymentResponse)
                .toList();

        log.info("✅ Found {} payments (admin)", data.size());

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<PaymentResponse> getPaymentById(HttpServletRequest httpRequest, UUID paymentId) {
        checkAdminRole(httpRequest);
        log.info("Getting payment {} (admin)", paymentId);

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResException(ResErrorCode.PAYMENT_NOT_FOUND));

        PaymentResponse data = toPaymentResponse(payment);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<List<PaymentResponse>> getPaymentsByOrderIdAdmin(HttpServletRequest httpRequest, UUID orderId) {
        checkAdminRole(httpRequest);
        log.info("Getting payments for order {} (admin)", orderId);

        // Verify order exists
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        List<PaymentEntity> payments = paymentRepository.findByOrderOrderId(orderId);

        List<PaymentResponse> data = payments.stream()
                .map(this::toPaymentResponse)
                .toList();

        log.info("✅ Found {} payments for order {} (admin)", data.size(), orderId);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentResponse> createPaymentAdmin(HttpServletRequest httpRequest, PaymentRequest request) {
        checkAdminRole(httpRequest);
        log.info("Creating payment for order {} (admin)", request.getOrderId());

        // Validate request
        validatePaymentRequest(request);

        // Verify order exists
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResException(ResErrorCode.ORDER_NOT_FOUND));

        try {
            PaymentEntity payment = PaymentEntity.builder()
                    .order(order)
                    .method(request.getMethod())
                    .amount(request.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paidAt(null)
                    .build();

            payment = paymentRepository.save(payment);

            log.info("✅ Created payment {} for order {} with amount {} (admin)",
                    payment.getPaymentId(), order.getOrderId(), payment.getAmount());

            PaymentResponse data = toPaymentResponse(payment);

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error creating payment (admin): {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.PAYMENT_CREATION_ERROR);
        }
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentResponse> updatePaymentStatus(HttpServletRequest httpRequest, UUID paymentId, PaymentStatus status) {
        checkAdminRole(httpRequest);
        log.info("Updating payment {} status to {} (admin)", paymentId, status);

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResException(ResErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus oldStatus = payment.getStatus();

        // Validate status transition
        validateStatusTransition(oldStatus, status);

        try {
            // Update payment status
            payment.setStatus(status);

            // Handle status-specific logic
            if (status == PaymentStatus.PROCESSING) {
                // Mark payment as processing
                payment.setPaidAt(null);
                log.info("💳 Payment {} is now PROCESSING", paymentId);

            } else if (status == PaymentStatus.PAID) {
                // Mark payment as paid
                payment.setPaidAt(LocalDateTime.now());

                // Update order status to PROCESSING
                OrderEntity order = payment.getOrder();
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);

                log.info("💰 Payment {} is now PAID, updating order {} to PROCESSING",
                        paymentId, order.getOrderId());

                // Deduct product stock
                deductProductStock(order);

                // After stock deducted, update order to CONFIRMED
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);

                log.info("📦 Order {} confirmed and stock deducted", order.getOrderId());

            } else if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {
                // Clear paid date if payment failed or cancelled
                payment.setPaidAt(null);
                log.info("❌ Payment {} marked as {}", paymentId, status);
            }

            payment = paymentRepository.save(payment);

            log.info("✅ Updated payment {} from {} to {} (admin)", paymentId, oldStatus, status);

            PaymentResponse data = toPaymentResponse(payment);

            return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);

        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error updating payment status: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.PAYMENT_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional
    public GeneralResponse<PaymentDeleteResponse> deletePayment(HttpServletRequest httpRequest, UUID paymentId) {
        checkAdminRole(httpRequest);
        log.info("Deleting payment {} (admin)", paymentId);

        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResException(ResErrorCode.PAYMENT_NOT_FOUND));

        // Only allow deletion of PENDING, FAILED, or CANCELLED payments
        if (payment.getStatus() == PaymentStatus.PAID || payment.getStatus() == PaymentStatus.PROCESSING) {
            log.warn("Cannot delete payment {} with status {}", paymentId, payment.getStatus());
            throw new ResException(ResErrorCode.PAYMENT_CANNOT_DELETE);
        }

        UUID orderId = payment.getOrder().getOrderId();
        paymentRepository.delete(payment);

        log.info("✅ Deleted payment {} for order {} (admin)", paymentId, orderId);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                new PaymentDeleteResponse(paymentId, orderId),
                null
        );
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Validate payment request
     */
    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getOrderId() == null) {
            throw new ResException(ResErrorCode.PAYMENT_ORDER_ID_REQUIRED);
        }

        if (request.getMethod() == null) {
            throw new ResException(ResErrorCode.PAYMENT_METHOD_REQUIRED);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResException(ResErrorCode.PAYMENT_INVALID_AMOUNT);
        }
    }

    /**
     * Validate payment status transition
     */
    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        // PAID status cannot be changed
        if (currentStatus == PaymentStatus.PAID && newStatus != PaymentStatus.PAID) {
            throw new ResException(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION,
                    "Không thể thay đổi trạng thái payment đã PAID");
        }

        // CANCELLED status cannot be changed to PAID
        if (currentStatus == PaymentStatus.CANCELLED && newStatus == PaymentStatus.PAID) {
            throw new ResException(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION,
                    "Không thể chuyển payment đã CANCELLED sang PAID");
        }

        // FAILED status should not go to PROCESSING or PAID directly
        if (currentStatus == PaymentStatus.FAILED &&
                (newStatus == PaymentStatus.PROCESSING || newStatus == PaymentStatus.PAID)) {
            throw new ResException(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION,
                    "Payment FAILED cần reset về PENDING trước");
        }
    }

    /**
     * Deduct product stock when payment is confirmed
     */
    private void deductProductStock(OrderEntity order) {
        log.info("📦 Deducting stock for order {}", order.getOrderId());

        // Get all order items
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

        for (OrderItemEntity item : orderItems) {
            try {
                log.info("📞 Calling Product Service to deduct stock for product: {} (quantity: {})",
                        item.getProductId(), item.getQuantity());

                // Create request to update stock
                ProductUpdateStockRequest stockRequest = new ProductUpdateStockRequest();
                // Negative to deduct
                GeneralResponse<SKUResponse> sku=productClient.getSkuById(item.getSkuId());
                // Call Product Service to deduct stock

                stockRequest.setStock(sku.getData().getStock()-item.getQuantity());
                productClient.updateProductStock(sku.getData().getId(), stockRequest);
                log.info("✅ Successfully deducted {} units of product {}",
                        item.getQuantity(), item.getProductId());

            } catch (FeignException.NotFound e) {
                log.error("❌ Product not found: {}", item.getProductId());
                throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND,
                        "Không tìm thấy sản phẩm với ID: " + item.getProductId());

            } catch (FeignException.BadRequest e) {
                log.error("❌ Insufficient stock for product: {}", item.getProductId());
                throw new ResException(ResErrorCode.PRODUCT_INSUFFICIENT_STOCK,
                        "Không đủ số lượng sản phẩm trong kho cho sản phẩm ID: " + item.getProductId());

            } catch (Exception e) {
                log.error("❌ Error deducting stock for product {}: {}",
                        item.getProductId(), e.getMessage(), e);
                throw new ResException(ResErrorCode.PRODUCT_STOCK_UPDATE_ERROR,
                        "Lỗi khi cập nhật số lượng sản phẩm");
            }
        }

        log.info("✅ Successfully deducted stock for all products in order {}", order.getOrderId());
    }

    /**
     * Convert PaymentEntity to PaymentResponse
     */
    private PaymentResponse toPaymentResponse(PaymentEntity payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setOrderId(payment.getOrder().getOrderId());
        response.setMethod(payment.getMethod().name());
        response.setStatus(payment.getStatus().name());
        response.setAmount(payment.getAmount());
        response.setPaidAt(payment.getPaidAt());
        return response;
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
            log.info("✅ Successfully extracted roles from token: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("❌ Failed to extract roles from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }
}