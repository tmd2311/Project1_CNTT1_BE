package com.proshop.order.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.order.dto.request.CartRequest;
import com.proshop.order.dto.response.CartResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.entity.CartEntity;
import com.proshop.order.mapper.CartMapper;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.service.cart.CartService;
import com.proshop.order.dto.response.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final JwtUtil jwtUtil;

    /**
     * Extract userId from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();

        log.debug("Token extracted, length: {}", token.length());

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("✅ Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage());
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
     * Get all carts (Admin only)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<GeneralResponse<List<CartResponse>>> getAllCarts() {
        log.info("Getting all carts (admin)");

        List<CartEntity> carts = cartRepository.findAll();
        List<CartResponse> response = cartMapper.toResponseList(carts);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<List<CartResponse>> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }

    /**
     * Get cart by userId (Admin only)
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartByUserId(@PathVariable long userId) {
        log.info("Getting cart for user {} (admin)", userId);

        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartResponse response = cartMapper.toResponse(cart);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<CartResponse> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }

    /**
     * Get cart by cartId (Admin only)
     */
    @GetMapping("/admin/{cartId}")
    public ResponseEntity<GeneralResponse<CartResponse>> getCartById(@PathVariable UUID cartId) {
        log.info("Getting cart by id {} (admin)", cartId);

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));

        CartResponse response = cartMapper.toResponse(cart);

        ResponseStatus status = new ResponseStatus("200", "Thành công", "Success");
        GeneralResponse<CartResponse> generalResponse = new GeneralResponse<>(status, response, null);

        return ResponseEntity.ok(generalResponse);
    }
}