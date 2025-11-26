package com.proshop.review_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.AuthClient;
import com.proshop.review_service.dto.request.ProductReviewCreateRequest;
import com.proshop.review_service.dto.request.ProductReviewUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.ApiResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ProductReviewResponse;
import com.proshop.review_service.dto.response.ProductReviewSummaryResponse;
import com.proshop.review_service.service.ProductReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller cho ProductReviewEntity (Đánh giá sản phẩm)
 */
@RestController
@RequestMapping("/api/product-reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductReviewController {

    private final ProductReviewService productReviewService;
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
        return "https://i.pravatar.cc/150?u=" + getUserIdFromToken(request);
    }

    // ============================================
    // CREATE & UPDATE
    // ============================================

    /**
     * Tạo product review mới với JSON (không có ảnh)
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createProductReviewJson(
            @Valid @RequestBody ProductReviewCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        String userName = getUserNameFromToken(httpRequest);
        String userAvatar = getUserAvatarFromToken(httpRequest);

        log.info("Creating product review (JSON) for user: {} ({})", userName, userId);

        ProductReviewResponse response = productReviewService.createProductReview(request, userId, userName, userAvatar, List.of());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product review created successfully", response));
    }

    /**
     * Tạo product review mới với ảnh (multipart)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createProductReviewWithImages(
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            HttpServletRequest httpRequest) throws JsonProcessingException {

        log.debug("Received review JSON: {}", reviewJson);
        if (images != null) {
            log.debug("Received {} images", images.length);
        }

        // Parse JSON to object
        ObjectMapper mapper = new ObjectMapper();
        ProductReviewCreateRequest request = mapper.readValue(reviewJson, ProductReviewCreateRequest.class);

        Long userId = getUserIdFromToken(httpRequest);
        String userName = getUserNameFromToken(httpRequest);
        String userAvatar = getUserAvatarFromToken(httpRequest);

        log.info("Creating product review (multipart) for user: {} ({})", userName, userId);

        // Convert MultipartFile[] to List
        List<MultipartFile> imageList = images != null ? Arrays.asList(images) : List.of();

        ProductReviewResponse response = productReviewService.createProductReview(request, userId, userName, userAvatar, imageList);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product review created successfully", response));
    }

    /**
     * Cập nhật product review với JSON (không có ảnh)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateProductReviewJson(
            @PathVariable Long id,
            @Valid @RequestBody ProductReviewUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating product review (JSON) {} by user: {}", id, userId);

        ProductReviewResponse response = productReviewService.updateProductReview(id, request, userId, List.of());
        return ResponseEntity.ok(ApiResponse.success("Product review updated successfully", response));
    }

    /**
     * Cập nhật product review với ảnh (multipart)
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateProductReviewWithImages(
            @PathVariable Long id,
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            HttpServletRequest httpRequest) throws JsonProcessingException {

        log.debug("Received review JSON for update: {}", reviewJson);
        if (images != null) {
            log.debug("Received {} images for update", images.length);
        }

        // Parse JSON to object
        ObjectMapper mapper = new ObjectMapper();
        ProductReviewUpdateRequest request = mapper.readValue(reviewJson, ProductReviewUpdateRequest.class);

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Updating product review (multipart) {} by user: {}", id, userId);

        // Convert MultipartFile[] to List
        List<MultipartFile> imageList = images != null ? Arrays.asList(images) : List.of();

        ProductReviewResponse response = productReviewService.updateProductReview(id, request, userId, imageList);
        return ResponseEntity.ok(ApiResponse.success("Product review updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductReview(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        Long userId = getUserIdFromToken(httpRequest);
        log.info("Deleting product review {} by user: {}", id, userId);
        productReviewService.deleteProductReview(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Product review deleted successfully", null));
    }

    // ============================================
    // READ & SEARCH
    // ============================================

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewSummaryResponse>>> getAllProductReviews(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting all product reviews - page: {}, size: {}", page, size);
        PageResponse<ProductReviewSummaryResponse> response = productReviewService.getAllProductReviews(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> getProductReviewById(@PathVariable Long id) {
        log.info("Getting product review: {}", id);
        ProductReviewResponse response = productReviewService.getProductReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewSummaryResponse>>> searchProductReviews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Searching product reviews with keyword: {}", keyword);
        PageResponse<ProductReviewSummaryResponse> response = productReviewService.searchProductReviews(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewSummaryResponse>>> getProductReviewsByProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting reviews for product: {}", productId);
        PageResponse<ProductReviewSummaryResponse> response = productReviewService.getProductReviewsByProduct(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewSummaryResponse>>> getProductReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting reviews for user: {}", userId);
        PageResponse<ProductReviewSummaryResponse> response = productReviewService.getProductReviewsByUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewSummaryResponse>>> getProductReviewsByRating(
            @PathVariable Double minRating,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting reviews with rating >= {}", minRating);
        PageResponse<ProductReviewSummaryResponse> response = productReviewService.getProductReviewsByRating(minRating, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<ProductReviewSummaryResponse>>> getHotProductReviews(
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Getting hot product reviews");
        List<ProductReviewSummaryResponse> response = productReviewService.getHotProductReviews(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateProductReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewStatusRequest request) {
        ProductReviewResponse response = productReviewService.updateProductReviewStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    // ============================================
    // STATISTICS
    // ============================================

    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRatingByProduct(@PathVariable UUID productId) {
        Double avgRating = productReviewService.getAverageRatingByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(avgRating));
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<ApiResponse<Long>> countReviewsByProduct(@PathVariable UUID productId) {
        Long count = productReviewService.countReviewsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
