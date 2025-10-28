package com.proshop.order;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderItemRequest;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderItemEntity;
import com.proshop.order.entity.OrderStatus;
import com.proshop.order.repository.OrderItemRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.service.order.impl.OrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class OrderServiceTest {

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
    private OrderServiceImpl orderService;

    private OrderEntity testOrder;
    private OrderCreateRequest createRequest;
    private ProductResponse testProduct;
    private SKUResponse testSku;
    private UUID testProductId;
    private UUID testSkuId;
    private OrderItemEntity testOrderItem;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testSkuId = UUID.randomUUID();

        testOrder = OrderEntity.builder()
                .orderId(UUID.randomUUID())
                .userId(1L)
                .totalAmount(BigDecimal.valueOf(200))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        testProduct = new ProductResponse();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);

        testSku = SKUResponse.builder()
                .id(testSkuId)
                .productId(testProductId)
                .price(100.0)
                .stock(10)
                .build();

        testOrderItem = OrderItemEntity.builder()
                .id(UUID.randomUUID())
                .order(testOrder)
                .productId(testProductId)
                .skuId(testSkuId)
                .quantity(2)
                .price(BigDecimal.valueOf(100))
                .subtotal(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(testProductId);
        itemRequest.setSkuId(testSkuId);
        itemRequest.setQuantity(2);

        createRequest = new OrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setItems(Arrays.asList(itemRequest));
    }

    @Test
    void createOrder_Success() {
        // Arrange
        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        GeneralResponse<SKUResponse> skuResponse = new GeneralResponse<>();
        skuResponse.setData(testSku);
        when(productClient.getSkuById(testSkuId)).thenReturn(skuResponse);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrder);

        // ✅ FIX: Mock orderItemRepository.saveAll()
        when(orderItemRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(testOrderItem));

        // Act
        GeneralResponse<OrderResponse> result = orderService.createOrder(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testOrder.getOrderId(), result.getData().getOrderId());
        assertEquals(BigDecimal.valueOf(200), result.getData().getTotalAmount());

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(productClient).getProductById(testProductId);
        verify(productClient, times(2)).getSkuById(testSkuId); // Called twice in the flow
    }

    @Test
    void createOrder_EmptyItems_ThrowsException() {
        // Arrange
        createRequest.setItems(new ArrayList<>());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.ORDER_ITEMS_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createOrder_NullItems_ThrowsException() {
        // Arrange
        createRequest.setItems(null);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.ORDER_ITEMS_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createOrder_ProductNotFound_ThrowsException() {
        // Arrange
        when(productClient.getProductById(testProductId))
                .thenThrow(feign.FeignException.NotFound.class);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.PRODUCT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void createOrder_SKUNotFound_ThrowsException() {
        // Arrange
        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        when(productClient.getSkuById(testSkuId))
                .thenThrow(feign.FeignException.NotFound.class);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.SKU_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void createOrder_InvalidQuantity_ThrowsException() {
        // Arrange
        createRequest.getItems().get(0).setQuantity(0);

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.ORDER_INVALID_QUANTITY.code(), exception.getCode());
    }

    @Test
    void createOrder_InvalidPrice_ThrowsException() {
        // Arrange
        testProduct.setPrice(0.0);
        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.createOrder(createRequest));
        assertEquals(ResErrorCode.ORDER_INVALID_PRICE.code(), exception.getCode());
    }

    @Test
    void getOrdersByUserId_Success() {
        // Arrange
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        // Act
        GeneralResponse<List<OrderResponse>> result = orderService.getOrdersByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(testOrder.getOrderId(), result.getData().get(0).getOrderId());
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void getOrdersByUserId_EmptyList() {
        // Arrange
        when(orderRepository.findByUserId(1L)).thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<OrderResponse>> result = orderService.getOrdersByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void getOrderByIdAndUserId_Success() {
        // Arrange
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));

        // Act
        GeneralResponse<OrderResponse> result =
                orderService.getOrderByIdAndUserId(testOrder.getOrderId(), 1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testOrder.getOrderId(), result.getData().getOrderId());
        verify(orderRepository).findById(testOrder.getOrderId());
    }

    @Test
    void getOrderByIdAndUserId_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.getOrderByIdAndUserId(UUID.randomUUID(), 1L));
        assertEquals(ResErrorCode.ORDER_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getOrderByIdAndUserId_WrongUser_ThrowsException() {
        // Arrange
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.getOrderByIdAndUserId(testOrder.getOrderId(), 999L));
        assertEquals(ResErrorCode.ORDER_ACCESS_DENIED.code(), exception.getCode());
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrder);

        // Act
        GeneralResponse<OrderResponse> result =
                orderService.cancelOrder(testOrder.getOrderId(), 1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED.name(), result.getData().getStatus());
        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.CANCELLED
        ));
    }

    @Test
    void cancelOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.cancelOrder(UUID.randomUUID(), 1L));
        assertEquals(ResErrorCode.ORDER_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void cancelOrder_WrongUser_ThrowsException() {
        // Arrange
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.cancelOrder(testOrder.getOrderId(), 999L));
        assertEquals(ResErrorCode.ORDER_ACCESS_DENIED.code(), exception.getCode());
    }

    @Test
    void cancelOrder_AlreadyProcessed_ThrowsException() {
        // Arrange
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.cancelOrder(testOrder.getOrderId(), 1L));
        assertEquals(ResErrorCode.ORDER_CANNOT_CANCEL.code(), exception.getCode());
    }

    @Test
    void getAllOrders_Admin_Success() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // Act
        GeneralResponse<List<OrderResponse>> result = orderService.getAllOrders(httpRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrders_NonAdmin_ThrowsException() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("User"));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.getAllOrders(httpRequest));
        assertEquals(ResErrorCode.PERMISSION_DENIED.code(), exception.getCode());
    }

    @Test
    void getOrderById_Admin_Success() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));

        // Act
        GeneralResponse<OrderResponse> result =
                orderService.getOrderById(httpRequest, testOrder.getOrderId());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void deleteOrder_Admin_Success() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(testOrder);

        // Act
        GeneralResponse<OrderDeleteResponse> result =
                orderService.deleteOrder(httpRequest, testOrder.getOrderId());

        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getOrderId(), result.getData().getOrderId());
        verify(orderRepository).delete(testOrder);
    }

    @Test
    void deleteOrder_NonAdmin_ThrowsException() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("User"));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.deleteOrder(httpRequest, UUID.randomUUID()));
        assertEquals(ResErrorCode.PERMISSION_DENIED.code(), exception.getCode());
    }

    @Test
    void getOrderDetailByIdAndUserId_Success() {
        // Arrange
        when(orderRepository.findById(testOrder.getOrderId()))
                .thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderOrderId(testOrder.getOrderId()))
                .thenReturn(Arrays.asList(testOrderItem));

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        GeneralResponse<SKUResponse> skuResponse = new GeneralResponse<>();
        skuResponse.setData(testSku);
        when(productClient.getSkuById(testSkuId)).thenReturn(skuResponse);

        // Act
        GeneralResponse<OrderDetailResponse> result =
                orderService.getOrderDetailByIdAndUserId(testOrder.getOrderId(), 1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getItems().size());
        verify(orderItemRepository).findByOrderOrderId(testOrder.getOrderId());
    }

    @Test
    void getOrdersByUserIdAdmin_Success() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(testOrder));

        // Act
        GeneralResponse<List<OrderResponse>> result =
                orderService.getOrdersByUserIdAdmin(httpRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }

    @Test
    void extractRoles_InvalidToken_ThrowsException() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("InvalidHeader");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.getAllOrders(httpRequest));
        assertEquals(ResErrorCode.UNAUTHORIZED.code(), exception.getCode());
    }

    @Test
    void extractRoles_MissingHeader_ThrowsException() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> orderService.getAllOrders(httpRequest));
        assertEquals(ResErrorCode.UNAUTHORIZED.code(), exception.getCode());
    }
}