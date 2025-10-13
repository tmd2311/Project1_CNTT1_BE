package com.proshop.product.service.product.impl;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductImageRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.PageResponseUtil;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.*;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductImageRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;


import com.proshop.product.specification.ProductSpecification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

import com.proshop.exceptionlib.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final ProductImageRepository productImageRepository;

  @Override
  public GeneralResponse<PageResponse<ProductResponse>> getProducts(int page, int size) {
    List<ProductEntity> productEntityList = productRepository.findAll();
    List<ProductResponse> productResponses = new ArrayList<>();
    for (ProductEntity entity : productEntityList) {
      ProductResponse response = convertToDTO(entity);
      productResponses.add(response);
    }
    long totalElements = productResponses.size();

    // Tính start & end index để phân trang
    int start = page * size;
    int end = Math.min(start + size, productResponses.size());

    // Nếu page vượt quá số phần tử thì trả về empty list
    List<ProductResponse> pageContent = start < totalElements
        ? productResponses.subList(start, end)
        : Collections.emptyList();

    // Tạo PageResponse
    PageResponse<ProductResponse> pageResponse = PageResponseUtil.buildPageResponse(
        pageContent,
        totalElements,
        page,
        size
    );

    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        pageResponse,
        null
    );
  }

  @Override
  public GeneralResponse<ProductResponse> getProductById(String idStr) {
    UUID id = covertIdToUUID(idStr);
    ProductEntity entity = productRepository.findProductById(id);
    if (entity == null) {
      throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND);
    }
    ProductResponse productResponse = convertToDTO(entity);
    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        productResponse,
        null
    );
  }

  private static UUID covertIdToUUID(String idStr) {
    if (idStr == null || idStr.isBlank()) {
      throw new ResException(ResErrorCode.FIELD_REQUIRED);
    }
    UUID id;
    try {
      id = UUID.fromString(idStr);
    } catch (IllegalArgumentException e) {
      throw new ResException(ResErrorCode.BAD_REQUEST);
    }
    return id;
  }

  @Override
  public GeneralResponse<ProductDeleteResponse> deleteProduct(String idStr) {
      UUID id = covertIdToUUID(idStr);
      ProductEntity product = productRepository.findById(id).orElse(null);
      if (product == null) {
          throw new ResException(ResErrorCode.PRODUCT_NOT_FOUND);
      }

      ProductDeleteResponse data = new ProductDeleteResponse(product.getId(), product.getName());
      productRepository.deleteById(id);

      return new GeneralResponse<>(
              ResponseStatus.SUCCESS_STATUS,
              data,
              null
      );
  }

    @Override
    @Transactional
    public GeneralResponse<ProductResponse> updateProduct(UUID id, ProductUpdateRequest request) {
        // 🔹 1. Tìm product cần cập nhật
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.PRODUCT_NOT_FOUND));

        // 🔹 2. Validate request
        validateProductUpdateRequest(request);

        // 🔹 3. Cập nhật các field cơ bản
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            product.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }

        if (request.getSpecs() != null) {
            product.setSpecs(request.getSpecs());
        }

        // 🔹 4. Cập nhật brand
        if (request.getBrandId() != null) {
            BrandEntity brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));
            product.setBrand(brand);
        }

        // 🔹 5. Cập nhật category
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        // 🔹 6. Xử lý cập nhật danh sách ảnh
        if (request.getImages() != null) {
            List<ProductImageRequest> imageRequests = request.getImages();

            // Lấy danh sách ảnh hiện có
            List<ProductImageEntity> existingImages = product.getImages();
            if (existingImages == null) {
                existingImages = new ArrayList<>();
            }

            // 6.1 Xóa ảnh không còn trong request
            List<UUID> newIds = imageRequests.stream()
                    .map(ProductImageRequest::getId)
                    .filter(Objects::nonNull)
                    .toList();

            existingImages.removeIf(img -> img.getId() != null && !newIds.contains(img.getId()));

            // 6.2 Cập nhật hoặc thêm ảnh mới
            for (ProductImageRequest imgReq : imageRequests) {
                if (imgReq.getUrl() == null || imgReq.getUrl().trim().isEmpty()) {
                    continue;
                }

                ProductImageEntity image;

                if (imgReq.getId() != null) {
                    // Cập nhật ảnh cũ
                    image = existingImages.stream()
                            .filter(i -> i.getId().equals(imgReq.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResException(ResErrorCode.PRODUCT_IMAGE_NOT_FOUND));

                    image.setUrl(imgReq.getUrl().trim());
                    image.setIsPrimary(imgReq.getIsPrimary() != null && imgReq.getIsPrimary());
                } else {
                    // Thêm ảnh mới
                    image = ProductImageEntity.builder()
                            .url(imgReq.getUrl().trim())
                            .isPrimary(imgReq.getIsPrimary() != null && imgReq.getIsPrimary())
                            .product(product)
                            .build();
                    existingImages.add(image);
                }
            }

            // 6.3 Kiểm tra chỉ có 1 ảnh chính
            long primaryCount = existingImages.stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                    .count();

            if (primaryCount > 1) {
                throw new ResException(ResErrorCode.PRODUCT_MULTIPLE_PRIMARY_IMAGES);
            }

            // 6.4 Cập nhật thumbnailUrl
            ProductImageEntity primaryImage = existingImages.stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                    .findFirst()
                    .orElse(null);

            if (primaryImage != null) {
                // Có ảnh chính
                product.setThumbnailUrl(primaryImage.getUrl());
            } else if (!existingImages.isEmpty()) {
                // Không có ảnh chính → dùng ảnh đầu tiên
                product.setThumbnailUrl(existingImages.get(0).getUrl());
            } else {
                // Không có ảnh nào
                product.setThumbnailUrl(null);
            }

            product.setImages(existingImages);
        }

        // 🔹 7. Cập nhật thời gian và lưu
        product.setUpdatedAt(LocalDateTime.now());
        ProductEntity updated = productRepository.save(product);

        // 🔹 8. Chuyển sang DTO và trả về
        ProductResponse productResponse = convertToDTO(updated);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                productResponse,
                null
        );
    }


    private void validateProductUpdateRequest(ProductUpdateRequest request) {
        if (request.getName() != null && request.getName().isBlank()) {
            throw new ResException(ResErrorCode.PRODUCT_NAME_REQUIRED);
        }

        if (request.getSpecs() != null && request.getSpecs().isEmpty()) {
            throw new ResException(ResErrorCode.PRODUCT_SPECS_REQUIRED);
        }
    }

    // Helper method convert Entity to DTO
    private ProductResponse convertToDTO(ProductEntity entity) {
        ProductResponse dto = new ProductResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSpecs(entity.getSpecs());

        if (entity.getBrand() != null) {
            dto.setBrandName(entity.getBrand().getName());
        }

        if (entity.getCategory() != null) {
            dto.setCategoryName(entity.getCategory().getName());
        }

        // Get min price from SKUs
        if (entity.getSkus() != null && !entity.getSkus().isEmpty()) {
            dto.setPrice(entity.getSkus().stream()
                    .filter(sku -> Boolean.TRUE.equals(sku.getIsActive()))
                    .map(SKUEntity::getPrice)
                    .filter(Objects::nonNull)
                    .min(Double::compareTo)
                    .orElse(null));
        }
      if (entity.getImages() != null && !entity.getImages().isEmpty()) {
        entity.getImages().stream()
            .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
            .findFirst()
            .ifPresent(primaryImage -> dto.setThumbnailUrl(primaryImage.getUrl()));
      }

      return dto;
    }

  @Override
  public GeneralResponse<PageResponse<ProductResponse>> searchProducts(
      String name, String brand, String category,
      Double minPrice, Double maxPrice,
      int page, int size) {

    Specification<ProductEntity> spec = ProductSpecification.hasName(name)
        .and(ProductSpecification.hasBrand(brand))
        .and(ProductSpecification.hasCategory(category))
        .and(ProductSpecification.priceBetween(minPrice, maxPrice));

    Pageable pageable = PageRequest.of(page, size);

    Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

    List<ProductResponse> productResponses = productPage
        .map(this::convertToDTO)
        .getContent();

    PageResponse<ProductResponse> pageResponse = PageResponseUtil.buildPageResponse(
        productResponses,
        productPage.getTotalElements(),
        page,
        size
    );

    return new GeneralResponse<>(
        ResponseStatus.SUCCESS_STATUS,
        pageResponse,
        null
    );
  }

    @Override
    @Transactional
    public GeneralResponse<ProductResponse> createProduct(ProductCreateRequest request) {
        // 🔹 1. Validate dữ liệu đầu vào
        validateProductCreationRequest(request);

        // 🔹 2. Lấy Brand và Category
        BrandEntity brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

        // 🔹 3. Tạo Product (chưa có ảnh)
        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .specs(request.getSpecs())
                .brand(brand)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 🔹 4. Lưu Product trước để có ID
        ProductEntity savedProduct = productRepository.save(product);

        // 🔹 5. Xử lý danh sách ảnh
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImageRequest> imageRequests = request.getImages();

            // 5.1 Giới hạn chỉ 1 ảnh chính
            long primaryCount = imageRequests.stream()
                    .filter(imgReq -> imgReq.getIsPrimary() != null && imgReq.getIsPrimary())
                    .count();

            if (primaryCount > 1) {
                throw new ResException(ResErrorCode.PRODUCT_MULTIPLE_PRIMARY_IMAGES);
            }

            // 5.2 Tạo danh sách ảnh entities
            List<ProductImageEntity> imageEntities = new ArrayList<>();
            String thumbnailUrl = null;

            for (ProductImageRequest imgReq : imageRequests) {
                // Bỏ qua nếu URL null hoặc rỗng
                if (imgReq.getUrl() == null || imgReq.getUrl().trim().isEmpty()) {
                    continue;
                }

                String cleanUrl = imgReq.getUrl().trim();
                Boolean isPrimary = imgReq.getIsPrimary() != null && imgReq.getIsPrimary();

                ProductImageEntity image = ProductImageEntity.builder()
                        .product(savedProduct)
                        .url(cleanUrl)
                        .isPrimary(isPrimary)
                        .build();

                imageEntities.add(image);

                // Lưu URL của ảnh chính
                if (isPrimary && thumbnailUrl == null) {
                    thumbnailUrl = cleanUrl;
                }
            }

            // 5.3 Lưu danh sách ảnh
            if (!imageEntities.isEmpty()) {
                productImageRepository.saveAll(imageEntities);
                savedProduct.setImages(imageEntities);

                // 5.4 Set thumbnail URL
                if (thumbnailUrl != null) {
                    // Có ảnh chính
                    savedProduct.setThumbnailUrl(thumbnailUrl);
                } else {
                    // Không có ảnh chính → dùng ảnh đầu tiên
                    savedProduct.setThumbnailUrl(imageEntities.get(0).getUrl());
                }

                // 5.5 Lưu lại product với thumbnailUrl
                savedProduct = productRepository.save(savedProduct);
            }
        }

        // 🔹 6. Chuyển sang DTO để trả về
        ProductResponse response = convertToDTO(savedProduct);

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null);
    }



    private void validateProductCreationRequest(ProductCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResException(ResErrorCode.PRODUCT_NAME_REQUIRED);
        }
        if (request.getSpecs() == null || request.getSpecs().isEmpty()) {
            throw new ResException(ResErrorCode.PRODUCT_SPECS_REQUIRED);
        }
        if (request.getBrandId() == null) {
            throw new ResException(ResErrorCode.BRAND_NOT_FOUND);
        }
        if (request.getCategoryId() == null) {
            throw new ResException(ResErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}

