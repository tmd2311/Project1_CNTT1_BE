package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.order.dto.request.CartRequest;
import com.proshop.order.dto.response.CartResponse;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.service.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    /**
     * Extract userId from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();
        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Add product to cart (userId from token)
     */
    @PostMapping("/add")
    public ResponseEntity<GeneralResponse<?>> addToCart(
            @RequestBody CartRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Adding to cart for user: {} - Product: {} - Quantity: {}",
                userId, request.getProductId(), request.getQuantity());

        GeneralResponse<?> response = cartService.addToCart(
                userId,
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get current user's cart (userId from token)
     */
    @GetMapping("/my-cart")
    public ResponseEntity<GeneralResponse<?>> getMyCart(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(httpRequest);
        log.info("Getting cart for user: {}", userId);

        GeneralResponse<?> response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update quantity in cart (userId from token)
     */
    @PutMapping("/update")
    public ResponseEntity<GeneralResponse<?>> updateQuantity(
            @RequestBody CartRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating cart for user: {} - Product: {} - Quantity: {}",
                userId, request.getProductId(), request.getQuantity());

        GeneralResponse<?> response = cartService.updateQuantity(
                userId,
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Remove product from cart (userId from token)
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<GeneralResponse<?>> removeFromCart(
            @PathVariable UUID productId,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Removing product {} from cart for user: {}", productId, userId);

        GeneralResponse<?> response = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // ADMIN ENDPOINTS (Optional - for debugging)
    // ============================================

    /**
     * ✅ Lấy toàn bộ giỏ hàng (Admin)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<CartResponse>>> getAllCarts(HttpServletRequest httpRequest) {
        log.info("Getting all carts (admin)");
        GeneralResponse<List<CartResponse>> response = cartService.getAllCarts(httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Lấy giỏ hàng theo userId (Admin)
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartByUserId(
        @PathVariable long userId,
        HttpServletRequest httpRequest) {

        log.info("Getting cart for user {} (admin)", userId);
        GeneralResponse<CartResponse> response = cartService.getCartByUserId(httpRequest, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Lấy giỏ hàng theo cartId (Admin)
     */
    @GetMapping("/admin/{cartId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartById(
        @PathVariable UUID cartId,
        HttpServletRequest httpRequest) {

        log.info("Getting cart by id {} (admin)", cartId);
        GeneralResponse<CartResponse> response = cartService.getCartById(httpRequest, cartId);
        return ResponseEntity.ok(response);
    }
}
