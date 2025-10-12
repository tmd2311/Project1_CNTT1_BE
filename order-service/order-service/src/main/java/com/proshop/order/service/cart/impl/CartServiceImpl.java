package com.proshop.order.service.cart.impl;

import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.response.CartItemResponse;
import com.proshop.order.dto.response.ProductResponse;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.entity.CartItemEntity;
import com.proshop.order.repository.CartItemRepository;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.service.cart.CartService;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ResponseStatus;
import feign.FeignException;
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

    /**
     * Convert CartItemEntity to CartItemResponse DTO
     */
    private CartItemResponse toCartItemResponse(CartItemEntity entity) {
        return CartItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
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
            ProductResponse product = productClient.getProductById(entity.getProductId());
            response.setProductName(product.getName());
            response.setProductPrice(product.getPrice());
        } catch (Exception e) {
            log.warn("Could not fetch product info for productId: {}", entity.getProductId());
            // Continue without product info
        }

        return response;
    }

    @Override
    @Transactional
    public GeneralResponse<?> addToCart(long userId, UUID productId, int quantity) {
        log.info("Adding to cart: userId={}, productId={}, quantity={}", userId, productId, quantity);

        // Validate quantity
        if (quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Số lượng phải lớn hơn 0", "Invalid quantity"),
                    null,
                    null
            );
        }

        // Validate product exists
        ProductResponse product;
        try {
            product = productClient.getProductById(productId);
            if (product == null) {
                log.error("Product not found: {}", productId);
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy sản phẩm", "Product not found"),
                        null,
                        null
                );
            }
            log.info("Product validated: id={}, name={}, price={}",
                    product.getId(), product.getName(), product.getPrice());
        } catch (FeignException.NotFound e) {
            log.error("Product not found: {}", productId);
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm", "Product not found"),
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("Error calling product service: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("503", "Không thể kết nối Product Service", "Service unavailable"),
                    null,
                    null
            );
        }

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
        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

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
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            log.info("Created new cart item");
        }

        cartItemRepository.save(cartItem);

        log.info("✅ Successfully added product {} to cart for user {} with quantity {}",
                productId, userId, quantity);

        // ✅ Return DTO instead of Entity
        CartItemResponse response = toCartItemResponse(cartItem);
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,  // ← Return DTO, not Entity!
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> removeFromCart(long userId, UUID productId) {
        log.info("Removing from cart: userId={}, productId={}", userId, productId);

        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (!cartItemOpt.isPresent()) {
            log.warn("Cart item not found for user {} and product {}", userId, productId);
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm trong giỏ", "Item not found"),
                    null,
                    null
            );
        }

        cartItemRepository.delete(cartItemOpt.get());

        log.info("✅ Removed product {} from cart for user {}", productId, userId);

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

        log.info("✅ Found {} items in cart for user {}", items.size(), userId);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                responses,  // ← Return List<DTO>, not List<Entity>!
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> updateQuantity(long userId, UUID productId, int quantity) {
        log.info("Updating quantity: userId={}, productId={}, quantity={}", userId, productId, quantity);

        // Validate product exists
        ProductResponse product;
        try {
            product = productClient.getProductById(productId);
            if (product == null) {
                log.error("Product not found: {}", productId);
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy sản phẩm", "Product not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("Product not found: {}", productId);
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm", "Product not found"),
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("Error calling product service: {}", e.getMessage());
            return new GeneralResponse<>(
                    new ResponseStatus("503", "Không thể kết nối Product Service", "Service unavailable"),
                    null,
                    null
            );
        }

        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (!cartItemOpt.isPresent()) {
            log.warn("Cart item not found for user {} and product {}", userId, productId);
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm trong giỏ", "Item not found"),
                    null,
                    null
            );
        }

        CartItemEntity cartItem = cartItemOpt.get();

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            log.info("✅ Deleted product {} from cart for user {} (quantity = 0)", productId, userId);
            return new GeneralResponse<>(
                    new ResponseStatus("200", "Đã xóa sản phẩm", "Deleted"),
                    "Đã xóa sản phẩm khỏi giỏ hàng",
                    null
            );
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("✅ Updated quantity for product {} in cart for user {} to {}",
                productId, userId, quantity);

        // ✅ Return DTO with product info
        CartItemResponse response = toCartItemResponse(cartItem);
        response.setProductName(product.getName());
        response.setProductPrice(product.getPrice());

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
    }
}