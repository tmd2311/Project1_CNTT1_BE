package com.proshop.order;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.request.PaymentRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.*;
import com.proshop.order.repository.OrderItemRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.PaymentRepository;
import com.proshop.order.service.payment.impl.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentEntity testPayment;
    private OrderEntity testOrder;
    private PaymentRequest paymentRequest;
    private OrderItemEntity testOrderItem;
    private SKUResponse testSku;
    private UUID testSkuId;

    @BeforeEach
    void setUp() {
        testSkuId = UUID.randomUUID();

        testOrder = OrderEntity.builder()
                .orderId(UUID.randomUUID())
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(200))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        testPayment = PaymentEntity.builder()
                .paymentId(UUID.randomUUID())
                .order(testOrder)
                .method(PaymentMethod.CREDIT_CARD)
                .amount(BigDecimal.valueOf(200))
                .status(PaymentStatus.PENDING)
                .build();

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(testOrder.getOrderId());
        paymentRequest.setMethod(PaymentMethod.CREDIT_CARD);
        paymentRequest.setAmount(BigDecimal.valueOf(200));

        testOrderItem = OrderItemEntity.builder()
                .id(UUID.randomUUID())
                .order(testOrder)
                .productId(UUID.randomUUID())
                .skuId(testSkuId)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .subtotal(BigDecimal.valueOf(200))
                .build();

        testSku = SKUResponse.builder()
                .id(testSkuId)
                .stock(10)
                .price(100.0)
                .build();
    }

    @Nested
    @DisplayName("Create Payment Tests")
    class CreatePaymentTests {

        @Test
        @DisplayName("Should create payment successfully")
        void createPayment_Success() {
            // Arrange
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));
            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(testPayment);

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.createPayment(paymentRequest, 1L);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testPayment.getPaymentId(), result.getData().getPaymentId());
            verify(paymentRepository).save(any(PaymentEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when order not found")
        void createPayment_OrderNotFound_ThrowsException() {
            // Arrange
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.createPayment(paymentRequest, 1L));
            assertEquals(ResErrorCode.ORDER_NOT_FOUND.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when wrong user")
        void createPayment_WrongUser_ThrowsException() {
            // Arrange
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.createPayment(paymentRequest, 999L));
            assertEquals(ResErrorCode.ORDER_ACCESS_DENIED.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when invalid order status")
        void createPayment_InvalidOrderStatus_ThrowsException() {
            // Arrange
            testOrder.setStatus(OrderStatus.CONFIRMED);
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.createPayment(paymentRequest, 1L));
            assertEquals(ResErrorCode.ORDER_INVALID_STATUS_FOR_PAYMENT.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when amount mismatch")
        void createPayment_AmountMismatch_ThrowsException() {
            // Arrange
            paymentRequest.setAmount(BigDecimal.valueOf(100));
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.createPayment(paymentRequest, 1L));
            assertEquals(ResErrorCode.PAYMENT_AMOUNT_MISMATCH.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should create payment by admin successfully")
        void createPaymentAdmin_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));
            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(testPayment);

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.createPaymentAdmin(httpRequest, paymentRequest);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getData());
            verify(paymentRepository).save(any(PaymentEntity.class));
        }
    }

    @Nested
    @DisplayName("Get Payment Tests")
    class GetPaymentTests {

        @Test
        @DisplayName("Should get payments by user ID successfully")
        void getPaymentsByUserId_Success() {
            // Arrange
            when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));
            when(paymentRepository.findByOrderOrderIdIn(anyList()))
                    .thenReturn(Arrays.asList(testPayment));

            // Act
            GeneralResponse<List<PaymentResponse>> result =
                    paymentService.getPaymentsByUserId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("Should get payment by ID and user ID successfully")
        void getPaymentByIdAndUserId_Success() {
            // Arrange
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.getPaymentByIdAndUserId(testPayment.getPaymentId(), 1L);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(testPayment.getPaymentId(), result.getData().getPaymentId());
        }

        @Test
        @DisplayName("Should throw exception when wrong user accesses payment")
        void getPaymentByIdAndUserId_WrongUser_ThrowsException() {
            // Arrange
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.getPaymentByIdAndUserId(testPayment.getPaymentId(), 999L));
            assertEquals(ResErrorCode.PAYMENT_ACCESS_DENIED.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should get payments by order ID successfully")
        void getPaymentsByOrderId_Success() {
            // Arrange
            when(orderRepository.findById(testOrder.getOrderId()))
                    .thenReturn(Optional.of(testOrder));
            when(paymentRepository.findByOrderOrderId(testOrder.getOrderId()))
                    .thenReturn(Arrays.asList(testPayment));

            // Act
            GeneralResponse<List<PaymentResponse>> result =
                    paymentService.getPaymentsByOrderId(testOrder.getOrderId(), 1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("Should get all payments by admin successfully")
        void getAllPayments_Admin_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findAll()).thenReturn(Arrays.asList(testPayment));

            // Act
            GeneralResponse<List<PaymentResponse>> result =
                    paymentService.getAllPayments(httpRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("Should throw exception when non-admin tries to get all payments")
        void getAllPayments_NonAdmin_ThrowsException() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("User"));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.getAllPayments(httpRequest));
            assertEquals(ResErrorCode.PERMISSION_DENIED.code(), exception.getCode());
        }
    }

    @Nested
    @DisplayName("Update Payment Status Tests")
    class UpdatePaymentStatusTests {

        @Test
        @DisplayName("Should update payment status to PAID successfully")
        void updatePaymentStatus_ToPaid_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));
            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(testPayment);

            // ✅ Order được save 2 lần: PROCESSING → CONFIRMED
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrder);

            when(orderItemRepository.findByOrderOrderId(testOrder.getOrderId()))
                    .thenReturn(Arrays.asList(testOrderItem));

            GeneralResponse<SKUResponse> skuResponse = new GeneralResponse<>();
            skuResponse.setData(testSku);
            when(productClient.getSkuById(testSkuId)).thenReturn(skuResponse);
            when(productClient.updateProductStock(any(), any())).thenReturn(skuResponse);

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.updatePaymentStatus(httpRequest, testPayment.getPaymentId(),
                            PaymentStatus.PAID);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getData());
            assertEquals(PaymentStatus.PAID.name(), result.getData().getStatus());

            // ✅ Verify payment được save với status PAID và paidAt không null
            verify(paymentRepository).save(argThat(payment ->
                    payment.getStatus() == PaymentStatus.PAID && payment.getPaidAt() != null
            ));

            // ✅ Verify order được save 2 lần
            verify(orderRepository, times(2)).save(any(OrderEntity.class));

            // ✅ Verify stock được update
            verify(productClient).updateProductStock(any(), any());
        }

        @Test
        @DisplayName("Should update payment status to PROCESSING successfully")
        void updatePaymentStatus_ToProcessing_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));
            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(testPayment);

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.updatePaymentStatus(httpRequest, testPayment.getPaymentId(),
                            PaymentStatus.PROCESSING);

            // Assert
            assertNotNull(result);
            assertEquals(PaymentStatus.PROCESSING.name(), result.getData().getStatus());

            // Verify payment saved với paidAt vẫn là null
            verify(paymentRepository).save(argThat(payment ->
                    payment.getStatus() == PaymentStatus.PROCESSING && payment.getPaidAt() == null
            ));

            // Verify order KHÔNG được update khi status là PROCESSING
            verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        @DisplayName("Should update payment status to FAILED successfully")
        void updatePaymentStatus_ToFailed_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));
            when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(testPayment);

            // Act
            GeneralResponse<PaymentResponse> result =
                    paymentService.updatePaymentStatus(httpRequest, testPayment.getPaymentId(),
                            PaymentStatus.FAILED);

            // Assert
            assertNotNull(result);
            assertEquals(PaymentStatus.FAILED.name(), result.getData().getStatus());

            verify(paymentRepository).save(argThat(payment ->
                    payment.getStatus() == PaymentStatus.FAILED && payment.getPaidAt() == null
            ));
        }

        @Test
        @DisplayName("Should throw exception when stock deduction fails")
        void updatePaymentStatus_StockDeductionFails_ThrowsException() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrder);
            when(orderItemRepository.findByOrderOrderId(testOrder.getOrderId()))
                    .thenReturn(Arrays.asList(testOrderItem));

            GeneralResponse<SKUResponse> skuResponse = new GeneralResponse<>();
            skuResponse.setData(testSku);
            when(productClient.getSkuById(testSkuId)).thenReturn(skuResponse);

            // ✅ Mock stock update fails với error code đúng
            when(productClient.updateProductStock(any(), any()))
                    .thenThrow(new ResException(ResErrorCode.PRODUCT_STOCK_UPDATE_ERROR));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.updatePaymentStatus(httpRequest,
                            testPayment.getPaymentId(), PaymentStatus.PAID));

            // ✅ Expect đúng error code
            assertEquals(ResErrorCode.PRODUCT_STOCK_UPDATE_ERROR.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception for invalid transition from PAID")
        void updatePaymentStatus_InvalidTransition_PaidToOther_ThrowsException() {
            // Arrange
            testPayment.setStatus(PaymentStatus.PAID);
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.updatePaymentStatus(httpRequest,
                            testPayment.getPaymentId(), PaymentStatus.PENDING));
            assertEquals(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception for invalid transition from CANCELLED to PAID")
        void updatePaymentStatus_InvalidTransition_CancelledToPaid_ThrowsException() {
            // Arrange
            testPayment.setStatus(PaymentStatus.CANCELLED);
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.updatePaymentStatus(httpRequest,
                            testPayment.getPaymentId(), PaymentStatus.PAID));
            assertEquals(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception for invalid transition from FAILED to PROCESSING")
        void updatePaymentStatus_InvalidTransition_FailedToProcessing_ThrowsException() {
            // Arrange
            testPayment.setStatus(PaymentStatus.FAILED);
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.updatePaymentStatus(httpRequest,
                            testPayment.getPaymentId(), PaymentStatus.PROCESSING));
            assertEquals(ResErrorCode.PAYMENT_STATUS_INVALID_TRANSITION.code(), exception.getCode());
        }
    }

    @Nested
    @DisplayName("Delete Payment Tests")
    class DeletePaymentTests {

        @Test
        @DisplayName("Should delete payment successfully")
        void deletePayment_Success() {
            // Arrange
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));
            doNothing().when(paymentRepository).delete(testPayment);

            // Act
            GeneralResponse<PaymentDeleteResponse> result =
                    paymentService.deletePayment(httpRequest, testPayment.getPaymentId());

            // Assert
            assertNotNull(result);
            assertEquals(testPayment.getPaymentId(), result.getData().getPaymentId());
            verify(paymentRepository).delete(testPayment);
        }

        @Test
        @DisplayName("Should throw exception when deleting paid payment")
        void deletePayment_PaidPayment_ThrowsException() {
            // Arrange
            testPayment.setStatus(PaymentStatus.PAID);
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.deletePayment(httpRequest, testPayment.getPaymentId()));
            assertEquals(ResErrorCode.PAYMENT_CANNOT_DELETE.code(), exception.getCode());
        }

        @Test
        @DisplayName("Should throw exception when deleting processing payment")
        void deletePayment_ProcessingPayment_ThrowsException() {
            // Arrange
            testPayment.setStatus(PaymentStatus.PROCESSING);
            when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
            when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
            when(paymentRepository.findById(testPayment.getPaymentId()))
                    .thenReturn(Optional.of(testPayment));

            // Act & Assert
            ResException exception = assertThrows(ResException.class,
                    () -> paymentService.deletePayment(httpRequest, testPayment.getPaymentId()));
            assertEquals(ResErrorCode.PAYMENT_CANNOT_DELETE.code(), exception.getCode());
        }
    }
}