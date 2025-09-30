package com.proshop.order.controller;

import com.proshop.order.dto.response.CartResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.mapper.CartMapper;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.service.cart.CartService;
import com.proshop.order.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    /**
     * Lấy tất cả giỏ hàng
     */
    @GetMapping
    public ResponseEntity<GeneralResponse<List<CartResponse>>> getAllCarts() {
        List<CartEntity> carts = cartRepository.findAll();
        List<CartResponse> response = cartMapper.toResponseList(carts);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<List<CartResponse>> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }

    /**
     * Lấy giỏ hàng theo userId
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartByUserId(@PathVariable UUID userId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartResponse response = cartMapper.toResponse(cart);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<CartResponse> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }

    /**
     * Lấy giỏ hàng theo cartId
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartById(@PathVariable UUID cartId) {
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));

        CartResponse response = cartMapper.toResponse(cart);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<CartResponse> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }

    @PostMapping("/{userId}/add/{productId}")
    public GeneralResponse<?> addToCart(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public GeneralResponse<?> removeFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        return cartService.removeFromCart(userId, productId);
    }

    @PutMapping("/{userId}/update/{productId}")
    public GeneralResponse<?> updateQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        return cartService.updateQuantity(userId, productId, quantity);
    }
}
