package com.proshop.order;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.response.*;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.entity.CartItemEntity;
import com.proshop.order.mapper.CartMapper;
import com.proshop.order.repository.CartItemRepository;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.service.cart.impl.CartServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartEntity testCart;
    private CartItemEntity testCartItem;
    private ProductResponse testProduct;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();

        testCart = new CartEntity();
        testCart.setCartId(UUID.randomUUID());
        testCart.setUserId(1L);
        testCart.setCreatedAt(LocalDateTime.now());

        testCartItem = new CartItemEntity();
        testCartItem.setId(UUID.randomUUID());
        testCartItem.setCart(testCart);
        testCartItem.setProductId(testProductId);
        testCartItem.setQuantity(2);
        testCartItem.setCreatedAt(LocalDateTime.now());

        testProduct = new ProductResponse();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
    }

    @Test
    void addToCart_NewCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartEntity.class))).thenReturn(testCart);
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItemEntity.class))).thenReturn(testCartItem);

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act
        GeneralResponse<?> result = cartService.addToCart(1L, testProductId, 2);

        // Assert
        assertNotNull(result);
        verify(cartRepository).save(any(CartEntity.class));
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void addToCart_ExistingItem_UpdateQuantity() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItemEntity.class))).thenReturn(testCartItem);

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act
        GeneralResponse<?> result = cartService.addToCart(1L, testProductId, 3);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository).save(argThat(item -> item.getQuantity() == 5));
    }

    @Test
    void addToCart_InvalidQuantity_ThrowsException() {
        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> cartService.addToCart(1L, testProductId, 0));
        assertEquals(ResErrorCode.CART_INVALID_QUANTITY.code(), exception.getCode());
    }

    @Test
    void addToCart_ProductNotFound_ThrowsException() {
        // Arrange
        when(productClient.getProductById(testProductId))
                .thenThrow(new ResException(ResErrorCode.PRODUCT_NOT_FOUND));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> cartService.addToCart(1L, testProductId, 2));
        assertEquals(ResErrorCode.PRODUCT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void removeFromCart_Success() {
        // Arrange
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.of(testCartItem));

        // Act
        GeneralResponse<?> result = cartService.removeFromCart(1L, testProductId);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository).delete(testCartItem);
    }

    @Test
    void removeFromCart_ItemNotFound_ThrowsException() {
        // Arrange
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> cartService.removeFromCart(1L, testProductId));
        assertEquals(ResErrorCode.CART_ITEM_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getCart_Success() {
        // Arrange
        List<CartItemEntity> items = Arrays.asList(testCartItem);
        when(cartItemRepository.findByUserId(1L)).thenReturn(items);

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act
        GeneralResponse<?> result = cartService.getCart(1L);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository).findByUserId(1L);
    }

    @Test
    void updateQuantity_Success() {
        // Arrange
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItemEntity.class))).thenReturn(testCartItem);

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act
        GeneralResponse<?> result = cartService.updateQuantity(1L, testProductId, 5);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository).save(argThat(item -> item.getQuantity() == 5));
    }

    @Test
    void updateQuantity_ZeroQuantity_DeletesItem() {
        // Arrange
        when(cartItemRepository.findByUserIdAndProductId(1L, testProductId))
                .thenReturn(Optional.of(testCartItem));

        GeneralResponse<ProductResponse> productResponse = new GeneralResponse<>();
        productResponse.setData(testProduct);
        when(productClient.getProductById(testProductId)).thenReturn(productResponse);

        // Act
        GeneralResponse<?> result = cartService.updateQuantity(1L, testProductId, 0);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository).delete(testCartItem);
    }

    @Test
    void getAllCarts_Admin_Success() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("Admin"));
        when(cartRepository.findAll()).thenReturn(Arrays.asList(testCart));
        when(cartMapper.toResponseList(anyList())).thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<CartResponse>> result = cartService.getAllCarts(httpRequest);

        // Assert
        assertNotNull(result);
        verify(cartRepository).findAll();
    }

    @Test
    void getAllCarts_NonAdmin_ThrowsException() {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extractRoles("token123")).thenReturn(Arrays.asList("User"));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> cartService.getAllCarts(httpRequest));
        assertEquals(ResErrorCode.PERMISSION_DENIED.code(), exception.getCode());
    }
}
