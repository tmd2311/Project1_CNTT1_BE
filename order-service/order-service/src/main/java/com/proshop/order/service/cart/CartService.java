package com.proshop.order.service.cart;

import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.entity.CartEntity;

import java.util.UUID;

public interface CartService {
    GeneralResponse<?> addToCart(UUID userId, UUID productId, int quantity);
    GeneralResponse<?> removeFromCart(UUID userId, UUID productId);
    GeneralResponse<?> getCart(UUID userId);
    GeneralResponse<?> updateQuantity(UUID userId, UUID productId, int quantity);
}


