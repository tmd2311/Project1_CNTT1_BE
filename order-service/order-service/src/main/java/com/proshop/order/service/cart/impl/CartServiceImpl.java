package com.proshop.order.service.cart.impl;

import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.response.ProductResponse;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.entity.CartItemEntity;
import com.proshop.order.repository.CartItemRepository;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.service.cart.CartService;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public GeneralResponse<?> addToCart(UUID userId, UUID productId, int quantity) {
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
        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                cartItem,
                null
        );
    }

    @Override
    public GeneralResponse<?> removeFromCart(UUID userId, UUID productId) {
        Optional<CartItemEntity> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (!cartItemOpt.isPresent()) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm trong giỏ", "Item not found"),
                    null,
                    null
            );
        }

        cartItemRepository.delete(cartItemOpt.get());
        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                null,
                null
        );
    }

    @Override
    public GeneralResponse<?> getCart(UUID userId) {
        List<CartItemEntity> items = cartItemRepository.findByUserId(userId);
        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                items,
                null
        );
    }

    @Override
    public GeneralResponse<?> updateQuantity(UUID userId, UUID productId, int quantity) {
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
            return new GeneralResponse<>(
                    new ResponseStatus("200", "Đã xóa sản phẩm do số lượng không hợp lệ", "Deleted"),
                    null,
                    null
            );
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                cartItem,
                null
        );
    }
}
