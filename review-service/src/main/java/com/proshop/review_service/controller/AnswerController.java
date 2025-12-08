package com.proshop.review_service.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.AuthClient;
import com.proshop.review_service.dto.request.AnswerCreateRequest;
import com.proshop.review_service.dto.request.AnswerUpdateRequest;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Slf4j
public class AnswerController {

    private final QuestionService questionService;
    private final JwtUtil jwtUtil;
    private final AuthClient authClient;

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

    private String getUserNameFromToken(HttpServletRequest request) {
        try {
            return authClient.getUserById(getUserIdFromToken(request)).getData().getUsername();
        } catch (Exception e) {
            return "Anonymous";
        }
    }

    private String getUserAvatarFromToken(HttpServletRequest request) {
        return "https://i.pravatar.cc/150?u=" + getUserIdFromToken(request);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(
            @Valid @RequestBody AnswerCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        String userName = getUserNameFromToken(httpRequest);
        String userAvatar = getUserAvatarFromToken(httpRequest);

        log.info("Creating answer for question: {} by user: {} ({})", request.getQuestionId(), userName, userId);
        AnswerResponse response = questionService.createAnswer(request, userId, userName, userAvatar);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Answer created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating answer {} by user: {}", id, userId);
        AnswerResponse response = questionService.updateAnswer(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Answer updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Deleting answer {} by user: {}", id, userId);
        questionService.deleteAnswer(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Answer deleted successfully", null));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAnswersByQuestion(
            @PathVariable Long questionId) {

        log.info("Getting answers for question: {}", questionId);
        List<AnswerResponse> response = questionService.getAnswersByQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}