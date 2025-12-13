package com.proshop.review_service.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.AuthClient;
import com.proshop.review_service.dto.request.QuestionCreateRequest;
import com.proshop.review_service.dto.request.QuestionUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.QuestionResponse;
import com.proshop.review_service.dto.response.QuestionSummaryResponse;
import com.proshop.review_service.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho QuestionEntity (Q&A)
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final AuthClient authClient;
    private final JwtUtil jwtUtil;

    // ============================================
    // HELPER METHOD - Extract User Info from JWT
    // ============================================

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new ResException(ResErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();

        try {
            Long userId = jwtUtil.getUserIDFromToken(token);
            log.info("Successfully extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Failed to extract userId from token: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.TOKEN_INVALID);
        }
    }

    private String getUserNameFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Anonymous";
        }

        try {
            return authClient.getUserById(getUserIdFromToken(request)).getData().getUsername();
        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return "Anonymous";
        }
    }

    private String getUserAvatarFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            return authClient.getUserById(getUserIdFromToken(request)).getData().getAvatarUrl();
        } catch (Exception e) {
            log.warn("Failed to extract avatar from token: {}", e.getMessage());
            return null;
        }
    }

    // ============================================
    // CREATE & UPDATE
    // ============================================

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        String userName = getUserNameFromToken(httpRequest);
        String userAvatar = getUserAvatarFromToken(httpRequest);

        log.info("Creating question for user: {} ({})", userName, userId);

        QuestionResponse response = questionService.createQuestion(request, userId, userName, userAvatar);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating question {} by user: {}", id, userId);

        QuestionResponse response = questionService.updateQuestion(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Question updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Deleting question {} by user: {}", id, userId);
        questionService.deleteQuestion(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Question deleted successfully", null));
    }

    // ============================================
    // READ & SEARCH
    // ============================================

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<QuestionSummaryResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting all questions - page: {}, size: {}", page, size);
        PageResponse<QuestionSummaryResponse> response = questionService.getAllQuestions(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@PathVariable Long id) {
        log.info("Getting question: {}", id);
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<QuestionSummaryResponse>>> searchQuestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Searching questions with keyword: {}", keyword);
        PageResponse<QuestionSummaryResponse> response = questionService.searchQuestions(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<QuestionSummaryResponse>>> getQuestionsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting questions for category: {}", categoryId);
        PageResponse<QuestionSummaryResponse> response = questionService.getQuestionsByCategory(categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<QuestionSummaryResponse>>> getQuestionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting questions for user: {}", userId);
        PageResponse<QuestionSummaryResponse> response = questionService.getQuestionsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<QuestionSummaryResponse>>> getHotQuestions(
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Getting hot questions");
        List<QuestionSummaryResponse> response = questionService.getHotQuestions(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestionStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewStatusRequest request) {
        QuestionResponse response = questionService.updateQuestionStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }
}
