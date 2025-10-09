package com.proshop.order.service.cart.impl;

import com.proshop.order.client.ProductClient;
import com.proshop.order.client.UserClient;
import com.proshop.order.dto.response.ProductResponse;
import com.proshop.order.dto.response.UserResponse;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public GeneralResponse<?> addToCart(long userId, UUID productId, int quantity) {
        // Validate user exists
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null) {
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
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

        // Validate product exists and get product info
        ProductResponse product;
        try {
            product = productClient.getProductById(productId);
            if (product == null) {
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
                    new ResponseStatus("500", "Lỗi khi kiểm tra sản phẩm", "Error validating product"),
                    null,
                    null
            );
        }

        // Validate quantity
        if (quantity <= 0) {
            return new GeneralResponse<>(
                    new ResponseStatus("400", "Số lượng phải lớn hơn 0", "Invalid quantity"),
                    null,
                    null
            );
        }

        // Lấy giỏ hàng hoặc tạo mới
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserId(userId);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Tìm sản phẩm trong giỏ
        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        CartItemEntity cartItem;
        if (cartItemOpt.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ thì tăng số lượng
            cartItem = cartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setUpdatedAt(LocalDateTime.now());
        } else {
            // Nếu chưa có thì thêm mới
            cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
        }

        cartItemRepository.save(cartItem);

        log.info("Added product {} to cart for user {} with quantity {}", productId, userId, quantity);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                cartItem,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> removeFromCart(long userId, UUID productId) {
        // Validate user exists
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null) {
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
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

        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (!cartItemOpt.isPresent()) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm trong giỏ", "Item not found"),
                    null,
                    null
            );
        }

        cartItemRepository.delete(cartItemOpt.get());

        log.info("Removed product {} from cart for user {}", productId, userId);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                null,
                null
        );
    }

    @Override
    public GeneralResponse<?> getCart(long userId) {
        // Validate user exists
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null) {
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
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

        List<CartItemEntity> items = cartItemRepository.findByUserId(userId);
        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                items,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<?> updateQuantity(long userId, UUID productId, int quantity) {
        // Validate user exists
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null) {
                return new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy người dùng", "User not found"),
                        null,
                        null
                );
            }
        } catch (FeignException.NotFound e) {
            log.error("User not found: {}", userId);
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

        // Validate product exists
        try {
            ProductResponse product = productClient.getProductById(productId);
            if (product == null) {
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
                    new ResponseStatus("500", "Lỗi khi kiểm tra sản phẩm", "Error validating product"),
                    null,
                    null
            );
        }

        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (!cartItemOpt.isPresent()) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm trong giỏ", "Item not found"),
                    null,
                    null
            );
        }

        CartItemEntity cartItem = cartItemOpt.get();

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            log.info("Deleted product {} from cart for user {} due to invalid quantity", productId, userId);
            return new GeneralResponse<>(
                    new ResponseStatus("200", "Đã xóa sản phẩm do số lượng không hợp lệ", "Deleted"),
                    null,
                    null
            );
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Updated quantity for product {} in cart for user {} to {}", productId, userId, quantity);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                cartItem,
                null
        );
    }
}