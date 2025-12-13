package com.proshop.review_service.service.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.OrderClient;
import com.proshop.review_service.client.ProductClient;
import com.proshop.review_service.dto.request.ProductReviewCreateRequest;
import com.proshop.review_service.dto.request.ProductReviewUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.ProductReviewResponse;
import com.proshop.review_service.dto.response.ProductReviewSummaryResponse;
import com.proshop.review_service.dto.response.ProductResponse;
import com.proshop.review_service.entity.ProductReviewEntity;
import com.proshop.review_service.entity.ProductReviewImageEntity;
import com.proshop.review_service.entity.TagEntity;
import com.proshop.review_service.mapper.ProductReviewMapper;
import com.proshop.review_service.repository.ProductReviewImageRepository;
import com.proshop.review_service.repository.ProductReviewRepository;
import com.proshop.review_service.repository.TagRepository;
import com.proshop.review_service.service.ProductReviewService;
import com.proshop.review_service.util.FileUtil;
import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final ProductReviewMapper mapper;
    private final FileUtil fileUtil;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    @Override
    public ProductReviewResponse createProductReview(ProductReviewCreateRequest request, Long userId, String userName, String userAvatar, List<MultipartFile> images) {
        log.info("Creating product review for user: {} and product: {}", userId, request.getProductId());

        // Validate product exists
        try {
            log.info("Validating product with ID: {}", request.getProductId());
            ProductResponse product = productClient.getProductById(request.getProductId());
            if (product == null) {
                log.error("Product response is null for productId: {}", request.getProductId());
                throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm không tồn tại");
            }
            log.info("Product validation successful - Product: {}", product.getName());
        } catch (ResException e) {
            // Re-throw ResException as is
            throw e;
        } catch (feign.FeignException e) {
            log.error("Feign error when calling product-service: Status={}, Message={}",
                    e.status(), e.getMessage(), e);
            if (e.status() == 404) {
                throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm không tồn tại");
            } else {
                throw new ResException(ResErrorCode.PRODUCT_SERVICE_UNAVAILABLE,
                        "Không thể kết nối đến product-service. Vui lòng thử lại sau.");
            }
        } catch (Exception e) {
            log.error("Unexpected error when validating product: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.PRODUCT_VALIDATION_ERROR,
                    "Lỗi khi xác minh sản phẩm: " + e.getMessage());
        }

        // Validate user has purchased the product
        try {
            log.info("Checking if user {} has purchased product {}", userId, request.getProductId());
            var purchaseResponse = orderClient.checkUserPurchase(request.getProductId());

            if (purchaseResponse == null || purchaseResponse.getData() == null || !purchaseResponse.getData()) {
                log.warn("User {} has not purchased product {}", userId, request.getProductId());
                throw new ResException(ResErrorCode.USER_NOT_PURCHASED_PRODUCT,
                    "Bạn chỉ có thể đánh giá sản phẩm đã mua");
            }
            log.info("User {} has purchased product {} - validation successful", userId, request.getProductId());
        } catch (ResException e) {
            // Re-throw ResException as is
            throw e;
        } catch (feign.FeignException e) {
            log.error("Feign error when calling order-service: Status={}, Message={}",
                    e.status(), e.getMessage(), e);
            throw new ResException(ResErrorCode.ORDER_SERVICE_UNAVAILABLE,
                    "Không thể xác minh lịch sử mua hàng. Vui lòng thử lại sau.");
        } catch (Exception e) {
            log.error("Unexpected error when checking purchase: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.PURCHASE_VALIDATION_ERROR,
                    "Lỗi khi xác minh lịch sử mua hàng: " + e.getMessage());
        }

        // Convert to entity
        ProductReviewEntity review = mapper.toEntity(request, userId, userName, userAvatar);
        review.setStatus(ReviewStatus.PENDING);
        review.setLikeCount(0);
        review.setViewCount(0);

        // Save review
        review = productReviewRepository.save(review);

        // Upload images if provided
        List<String> uploadedImageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            log.info("Uploading {} images for product review", images.size());
            uploadedImageUrls = fileUtil.uploadMultipleImages(images);
            log.info("Successfully uploaded {} images", uploadedImageUrls.size());
        }

        // Add images from upload
        if (!uploadedImageUrls.isEmpty()) {
            int order = 0;
            for (String imageUrl : uploadedImageUrls) {
                ProductReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Add images from request (if any - for backward compatibility)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            int order = uploadedImageUrls.size();
            for (String imageUrl : request.getImageUrls()) {
                ProductReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Add tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<TagEntity> tags = getOrCreateTags(request.getTags());
            review.setTags(tags);
            review = productReviewRepository.save(review);

            // Increment tag usage
            tags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        // Load images explicitly
        List<ProductReviewImageEntity> savedImages = imageRepository.findByProductReview_Id(review.getId());
        if (review.getImages() == null) {
            review.setImages(new ArrayList<>());
        }
        review.getImages().clear();
        review.getImages().addAll(savedImages);

        return mapper.toResponse(review);
    }

    @Override
    @Transactional
    public ProductReviewResponse getProductReviewById(Long id) {
        ProductReviewEntity review = productReviewRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Product review not found"));

        // Increment view count
        productReviewRepository.incrementViewCount(id);

        return mapper.toResponse(review);
    }

    @Override
    public ProductReviewResponse updateProductReview(Long id, ProductReviewUpdateRequest request, Long userId, List<MultipartFile> images) {
        ProductReviewEntity review = productReviewRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Product review not found"));

        // Check ownership
        if (!review.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền cập nhật review này");
        }

        mapper.updateEntity(review, request);

        // Handle image updates
        if (images != null && !images.isEmpty()) {
            // Delete old images
            List<ProductReviewImageEntity> oldImages = imageRepository.findByProductReview_Id(id);
            for (ProductReviewImageEntity oldImage : oldImages) {
                try {
                    fileUtil.deleteFileByUrl(oldImage.getImageUrl());
                } catch (Exception e) {
                    log.warn("Failed to delete old image: {}", e.getMessage());
                }
            }
            imageRepository.deleteByProductReview_Id(id);

            // Upload new images
            log.info("Uploading {} new images for product review", images.size());
            List<String> uploadedImageUrls = fileUtil.uploadMultipleImages(images);
            log.info("Successfully uploaded {} images", uploadedImageUrls.size());

            // Add new images
            int order = 0;
            for (String imageUrl : uploadedImageUrls) {
                ProductReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Update images from imageUrls in request
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            // Delete old images
            List<ProductReviewImageEntity> oldImages = imageRepository.findByProductReview_Id(id);
            for (ProductReviewImageEntity oldImage : oldImages) {
                try {
                    fileUtil.deleteFileByUrl(oldImage.getImageUrl());
                } catch (Exception e) {
                    log.warn("Failed to delete old image: {}", e.getMessage());
                }
            }
            imageRepository.deleteByProductReview_Id(id);

            // Add new images from URLs
            int order = 0;
            for (String imageUrl : request.getImageUrls()) {
                ProductReviewImageEntity image = mapper.toImageEntity(imageUrl, review, order++);
                imageRepository.save(image);
            }
        }

        // Update tags
        if (request.getTags() != null) {
            // Decrement old tags
            review.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

            // Add new tags
            List<TagEntity> newTags = getOrCreateTags(request.getTags());
            review.setTags(newTags);
            newTags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        review = productReviewRepository.save(review);

        // Load images explicitly
        List<ProductReviewImageEntity> savedImages = imageRepository.findByProductReview_Id(review.getId());
        if (review.getImages() == null) {
            review.setImages(new ArrayList<>());
        }
        review.getImages().clear();
        review.getImages().addAll(savedImages);

        return mapper.toResponse(review);
    }

    @Override
    public void deleteProductReview(Long id, Long userId) {
        ProductReviewEntity review = productReviewRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy product review"));

        if (!review.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền xóa review này");
        }

        // Decrement tag usage
        review.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

        // Delete images
        List<ProductReviewImageEntity> images = imageRepository.findByProductReview_Id(id);
        for (ProductReviewImageEntity image : images) {
            try {
                fileUtil.deleteFileByUrl(image.getImageUrl());
            } catch (Exception e) {
                log.warn("Failed to delete image: {}", e.getMessage());
            }
        }

        productReviewRepository.delete(review);
    }

    @Override
    @Transactional
    public PageResponse<ProductReviewSummaryResponse> getAllProductReviews(Integer page, Integer size) {
        log.info("Getting all product reviews - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReviewEntity> reviewPage = productReviewRepository.findAll(pageable);

        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductReviewSummaryResponse> searchProductReviews(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReviewEntity> reviewPage = productReviewRepository.searchByKeyword(keyword, pageable);

        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductReviewSummaryResponse> getProductReviewsByProduct(UUID productId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReviewEntity> reviewPage = productReviewRepository.findByProductIdAndStatus(
                productId, ReviewStatus.APPROVED, pageable);

        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductReviewSummaryResponse> getProductReviewsByUser(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReviewEntity> reviewPage = productReviewRepository.findByUserId(userId, pageable);

        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public PageResponse<ProductReviewSummaryResponse> getProductReviewsByRating(Double minRating, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReviewEntity> reviewPage = productReviewRepository.findByRatingGreaterThanEqual(minRating, pageable);

        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public List<ProductReviewSummaryResponse> getHotProductReviews(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ProductReviewEntity> page = productReviewRepository.findHotProductReviews(pageable);
        return mapper.toSummaryResponseList(page.getContent());
    }

    @Override
    @Transactional
    public ProductReviewResponse updateProductReviewStatus(Long id, UpdateReviewStatusRequest request) {
        log.info("Updating product review status for reviewId: {} to status: {}", id, request.getStatus());

        ProductReviewEntity review = productReviewRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Product review not found with ID: " + id));

        ReviewStatus currentStatus = review.getStatus();
        ReviewStatus newStatus = request.getStatus();

        if (currentStatus == newStatus) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Review đã ở trạng thái: " + newStatus);
        }

        review.setStatus(newStatus);

        if (newStatus == ReviewStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
                throw new ResException(ResErrorCode.FIELD_REQUIRED, "Lý do từ chối là bắt buộc");
            }
            review.setRejectionReason(request.getRejectionReason().trim());
        } else {
            review.setRejectionReason(null);
        }

        ProductReviewEntity updatedReview = productReviewRepository.save(review);

        return mapper.toResponse(updatedReview);
    }

    @Override
    public Double getAverageRatingByProduct(UUID productId) {
        return productReviewRepository.getAverageRatingByProduct(productId);
    }

    @Override
    public Long countReviewsByProduct(UUID productId) {
        return productReviewRepository.countByProductId(productId);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private List<TagEntity> getOrCreateTags(List<String> tagNames) {
        List<TagEntity> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            TagEntity tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        TagEntity newTag = mapper.toEntity(tagName);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }

    private PageResponse<ProductReviewSummaryResponse> buildPageResponse(Page<ProductReviewEntity> page) {
        return PageResponse.<ProductReviewSummaryResponse>builder()
                .content(mapper.toSummaryResponseList(page.getContent()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
