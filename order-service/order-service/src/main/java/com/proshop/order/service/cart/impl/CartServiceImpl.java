package com.proshop.order.service.cart.impl;

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
import com.proshop.order.service.cart.CartService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final JwtUtil jwtUtil;
    private final CartMapper cartMapper;

    // ============================================
    // USER METHODS
    // ============================================

    @Override
    @Transactional
    public GeneralResponse<?> addToCart(long userId, UUID productId, UUID skuId, int quantity) {
        log.info("Adding to cart: userId={}, productId={}, skuId={}, quantity={}", userId, productId, skuId, quantity);

        // Validate quantity
        if (quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new ResException(ResErrorCode.CART_INVALID_QUANTITY);
        }

        // Validate product exists
        ProductResponse product = validateAndGetProduct(productId);

        // Get or create cart
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new cart for user: {}", userId);
                    CartEntity newCart = new CartEntity();
                    newCart.setUserId(userId);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Find existing cart item
        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductIdAndSkuId(userId, productId, skuId);

        CartItemEntity cartItem;
        if (cartItemOpt.isPresent()) {
            // Item exists, increase quantity
            cartItem = cartItemOpt.get();
            int oldQuantity = cartItem.getQuantity();
            cartItem.setQuantity(oldQuantity + quantity);
            cartItem.setUpdatedAt(LocalDateTime.now());
            log.info("Updated existing cart item: {} + {} = {}", oldQuantity, quantity, cartItem.getQuantity());
        } else {
            // New item
            cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setProductId(productId);
            cartItem.setSkuId(skuId);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            log.info("Created new cart item");
        }

        cartItemRepository.save(cartItem);

        log.info(" Successfully added product {} to cart for user {} with quantity {}",
                productId, userId, quantity);

        // Return DTO with product info
        CartItemResponse response = toCartItemResponse(cartItem);
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> removeFromCart(long userId, UUID productId, UUID skuId) {
        log.info("Removing from cart: userId={}, productId={}, skuId={}", userId, productId, skuId);

        CartItemEntity cartItem = cartItemRepository.findByUserIdAndProductIdAndSkuId(userId, productId, skuId)
                .orElseThrow(() -> new ResException(ResErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);

        log.info(" Removed product {} from cart for user {}", productId, userId);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                "Đã xóa sản phẩm khỏi giỏ hàng",
                null
        );
    }

    @Override
    public GeneralResponse<?> getCart(long userId) {
        log.info("Getting cart for user: {}", userId);

        List<CartItemEntity> items = cartItemRepository.findByUserId(userId);

        // Convert to DTOs with product info
        List<CartItemResponse> responses = items.stream()
                .map(this::toCartItemResponseWithProduct)
                .collect(Collectors.toList());

        log.info(" Found {} items in cart for user {}", items.size(), userId);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> updateQuantity(long userId, UUID productId, UUID skuId, int quantity) {
        log.info("Updating quantity: userId={}, productId={}, skuId={}, quantity={}", userId, productId, skuId, quantity);

        // Validate product exists
        ProductResponse product = validateAndGetProduct(productId);

        CartItemEntity cartItem = cartItemRepository.findByUserIdAndProductIdAndSkuId(userId, productId, skuId)
                .orElseThrow(() -> new ResException(ResErrorCode.CART_ITEM_NOT_FOUND));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            log.info(" Deleted product {} from cart for user {} (quantity = 0)", productId, userId);
            return new GeneralResponse<>(
                    new ResponseStatus("200", "Đã xóa sản phẩm", "Deleted"),
                    "Đã xóa sản phẩm khỏi giỏ hàng",
                    null
            );
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info(" Updated quantity for product {} in cart for user {} to {}",
                productId, userId, quantity);

        // Return DTO with product info
        CartItemResponse response = toCartItemResponse(cartItem);
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }

    // ============================================
    // ADMIN METHODS
    // ============================================

    @Override
    public GeneralResponse<List<CartResponse>> getAllCarts(HttpServletRequest httpRequest) {
        checkAdminRole(httpRequest);

        List<CartEntity> carts = cartRepository.findAll();
        List<CartResponse> response = cartMapper.toResponseList(carts);

        log.info(" Found {} carts (admin)", carts.size());

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        return new GeneralResponse<>(status, response, null);
    }

    @Override
    public GeneralResponse<CartResponse> getCartByUserId(HttpServletRequest httpRequest, long userId) {
        checkAdminRole(httpRequest);

        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResException(ResErrorCode.CART_NOT_FOUND,
                        "Không tìm thấy giỏ hàng của userId: " + userId));

        CartResponse response = cartMapper.toResponse(cart);

        log.info(" Found cart for user {} (admin)", userId);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        return new GeneralResponse<>(status, response, null);
    }

    @Override
    public GeneralResponse<CartResponse> getCartById(HttpServletRequest httpRequest, UUID cartId) {
        checkAdminRole(httpRequest);

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResException(ResErrorCode.CART_NOT_FOUND,
                        "Không tìm thấy giỏ hàng: " + cartId));

        CartResponse response = cartMapper.toResponse(cart);

        log.info(" Found cart {} (admin)", cartId);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        return new GeneralResponse<>(status, response, null);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Validate product exists and get product details
     */
    private ProductResponse validateAndGetProduct(UUID productId) {
        try {
            GeneralResponse<ProductResponse> productResponse = productClient.getProductById(productId);
            ProductResponse product = productResponse.getData();

            if (product == null) {
                log.error("Product not found: {}", productId);
                throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND);
            }

            log.info("Product validated: id={}, name={}, price={}",
                    product.getId(), product.getName(), product.getPrice());

            return product;

        } catch (FeignException.NotFound e) {
            log.error("Product not found: {}", productId);
            throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND);
        } catch (ResException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling product service: {}", e.getMessage());
            throw new ResException(ResErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Convert CartItemEntity to CartItemResponse DTO
     */
    private CartItemResponse toCartItemResponse(CartItemEntity entity) {
        return CartItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .skuId(entity.getSkuId())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert CartItemEntity to CartItemResponse with product info
     */
    private CartItemResponse toCartItemResponseWithProduct(CartItemEntity entity) {
        CartItemResponse response = toCartItemResponse(entity);

        // Try to get product info
        try {
            GeneralResponse<ProductResponse> productResponse = productClient.getProductById(entity.getProductId());
            ProductResponse product = productResponse.getData();

            if (product != null) {
                response.setProductName(product.getName());
                response.setProductPrice(product.getPrice());
            }
        } catch (Exception e) {
            log.warn("Could not fetch product info for productId: {}", entity.getProductId());
            // Continue without product info
        }

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
            log.info(" Successfully extracted role from token: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("❌ Failed to extract role from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }
}