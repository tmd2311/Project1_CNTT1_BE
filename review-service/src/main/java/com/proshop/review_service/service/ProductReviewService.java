package com.proshop.review_service.service;

import com.proshop.review_service.dto.request.ProductReviewCreateRequest;
import com.proshop.review_service.dto.request.ProductReviewUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ProductReviewResponse;
import com.proshop.review_service.dto.response.ProductReviewSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface cho ProductReviewEntity
 */
public interface ProductReviewService {

    // CRUD
    ProductReviewResponse createProductReview(ProductReviewCreateRequest request, Long userId, String userName, String userAvatar, List<MultipartFile> images);
    ProductReviewResponse getProductReviewById(Long id);
    ProductReviewResponse updateProductReview(Long id, ProductReviewUpdateRequest request, Long userId, List<MultipartFile> images);
    void deleteProductReview(Long id, Long userId);

    // Listing & Search
    PageResponse<ProductReviewSummaryResponse> getAllProductReviews(Integer page, Integer size);
    PageResponse<ProductReviewSummaryResponse> searchProductReviews(String keyword, Integer page, Integer size);
    PageResponse<ProductReviewSummaryResponse> getProductReviewsByProduct(UUID productId, Integer page, Integer size);
    PageResponse<ProductReviewSummaryResponse> getProductReviewsByUser(Long userId, Integer page, Integer size);
    PageResponse<ProductReviewSummaryResponse> getProductReviewsByRating(Double minRating, Integer page, Integer size);
    List<ProductReviewSummaryResponse> getHotProductReviews(Integer limit);

    // Status management
    ProductReviewResponse updateProductReviewStatus(Long id, UpdateReviewStatusRequest request);

    // Statistics
    Double getAverageRatingByProduct(UUID productId);
    Long countReviewsByProduct(UUID productId);
}
