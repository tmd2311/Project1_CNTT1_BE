package com.proshop.review_service.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.dto.request.ReactionRequest;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.service.ReactionService;
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

    private final ReactionService reactionService;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7).trim();
        try {
            return jwtUtil.getUserIDFromToken(token);
        } catch (Exception e) {
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addReaction(
            @Valid @RequestBody ReactionRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("User {} reacting to {} {}", userId, request.getTargetType(), request.getTargetId());
        reactionService.addReaction(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Reaction added successfully", null));
    }
}
