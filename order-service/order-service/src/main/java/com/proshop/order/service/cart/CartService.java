package com.proshop.order.service.cart;

import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.entity.CartEntity;

import java.util.UUID;

public interface CartService {
    GeneralResponse<?> addToCart(long userId, UUID productId, int quantity);
    GeneralResponse<?> removeFromCart(long userId, UUID productId);
    GeneralResponse<?> getCart(long userId);
    GeneralResponse<?> updateQuantity(long userId, UUID productId, int quantity);
}


