package com.proshop.order.mapper;

import com.proshop.order.dto.response.CartItemResponse;
import com.proshop.order.dto.response.CartResponse;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    /**
     * Convert CartEntity sang CartResponse
     */
    public CartResponse toResponse(CartEntity entity) {
        if (entity == null) {
            return null;
        }

        return CartResponse.builder()
                .cartId(entity.getCartId())
                .userId(entity.getUserId())
                .items(toItemResponseList(entity.getItems()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert List<CartEntity> sang List<CartResponse>
     */
    public List<CartResponse> toResponseList(List<CartEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert CartItemEntity sang CartItemResponse
     */
    public CartItemResponse toItemResponse(CartItemEntity entity) {
        if (entity == null) {
            return null;
        }

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
     * Convert List<CartItemEntity> sang List<CartItemResponse>
     */
    public List<CartItemResponse> toItemResponseList(List<CartItemEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }
}