package com.proshop.order.service.cart;

import com.proshop.order.dto.response.CartResponse;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.entity.CartEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface CartService {
    GeneralResponse<?> addToCart(long userId, UUID productId, int quantity);
    GeneralResponse<?> removeFromCart(long userId, UUID productId);
    GeneralResponse<?> getCart(long userId);
    GeneralResponse<?> updateQuantity(long userId, UUID productId, int quantity);
    GeneralResponse<List<CartResponse>> getAllCarts(HttpServletRequest httpRequest);
    GeneralResponse<CartResponse> getCartByUserId(HttpServletRequest httpRequest, long userId);
    GeneralResponse<CartResponse> getCartById(HttpServletRequest httpRequest, UUID cartId);
}


