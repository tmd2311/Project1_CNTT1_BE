package  com.proshop.review_service.controller;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.AuthClient;
import com.proshop.review_service.dto.request.ReviewCreateRequest;
import com.proshop.review_service.dto.request.ReviewSearchRequest;
import com.proshop.review_service.dto.request.ReviewUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ReviewResponse;
import com.proshop.review_service.dto.response.ReviewSummaryResponse;
import com.proshop.review_service.service.ReviewService;

import com.proshop.auth_lib.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================
// REVIEW CONTROLLER
// ============================================
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
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

    // Optional: Get avatar from user-service via Feign (nếu cần)
    private String getUserAvatarFromToken(HttpServletRequest request) {
        // TODO: Call user-service to get avatar
        // For now, return placeholder
        return "https://i.pravatar.cc/150?u=" + getUserIdFromToken(request);
    }

    // ============================================
    // CREATE & UPDATE
    // ============================================

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        String userName = getUserNameFromToken(httpRequest);
        String userAvatar = getUserAvatarFromToken(httpRequest);

        log.info("Creating review for user: {} ({})", userName, userId);
        ReviewResponse response = reviewService.createReview(request, userId, userName, userAvatar);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating review {} by user: {}", id, userId);
        ReviewResponse response = reviewService.updateReview(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Deleting review {} by user: {}", id, userId);
        reviewService.deleteReview(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }

    // ============================================
    // READ & SEARCH
    // ============================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable Long id) {
        log.info("Getting review: {}", id);
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ReviewSummaryResponse>>> searchReviews(
            @RequestBody ReviewSearchRequest request) {

        log.info("Searching reviews with keyword: {}", request.getKeyword());
        PageResponse<ReviewSummaryResponse> response = reviewService.searchReviews(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewSummaryResponse>>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting reviews for product: {}", productId);
        PageResponse<ReviewSummaryResponse> response = reviewService.getReviewsByProduct(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<ReviewSummaryResponse>>> getHotReviews(
            @RequestParam(defaultValue = "QA") String type,
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Getting hot reviews of type: {}", type);
        List<ReviewSummaryResponse> response = reviewService.getHotReviews(type, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới được đổi status
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewStatusRequest request) {
        ReviewResponse response = reviewService.updateReviewStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    // ============================================
    // STATISTICS
    // ============================================

//    @GetMapping("/product/{productId}/summary")
//    public ResponseEntity<ApiResponse<ProductReviewSummary>> getProductReviewSummary(
//            @PathVariable Long productId) {
//
//        log.info("Getting review summary for product: {}", productId);
//        ProductReviewSummary response = reviewService.getProductReviewSummary(productId);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
}
