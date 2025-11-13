package com.proshop.review_service.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.review_service.dto.request.ReactionRequest;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReactionController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7).trim();
        try {
            return jwtUtil.getUserIDFromToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addReaction(
            @Valid @RequestBody ReactionRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("User {} reacting to {} {}", userId, request.getTargetType(), request.getTargetId());
        reviewService.addReaction(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Reaction added successfully", null));
    }
}
