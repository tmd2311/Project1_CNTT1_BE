package com.proshop.product.service.product.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductImageRequest;
import com.proshop.product.dto.request.ProductImageUpdateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.PageResponseUtil;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.ProductImageEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductImageRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;
import com.proshop.product.specification.ProductSpecification;
import com.proshop.product.utils.FileUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final ProductImageRepository productImageRepository;
  private final FileUtil fileUtil;

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
  public GeneralResponse<ProductResponse> updateProduct(UUID id, ProductUpdateRequest request, List<MultipartFile> newImages) {
    ProductEntity product = findProductOrThrow(id);
    validateProductUpdateRequest(request);

    applyBasicUpdates(product, request);
    applyBrandUpdate(product, request);
    applyCategoryUpdate(product, request);

    if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
      deleteProductImages(product, request.getDeleteImageIds());
    }

    if (request.getUpdateImages() != null && !request.getUpdateImages().isEmpty()) {
      replaceProductImages(product, request.getUpdateImages());
    }

    if (newImages != null && !newImages.isEmpty()) {
      List<String> uploadedUrls = fileUtil.uploadMultipleImages(newImages);
      addNewImages(product, uploadedUrls);
    }

    product.setUpdatedAt(LocalDateTime.now());
    ProductEntity updated = productRepository.save(product);

    ProductResponse response = convertToDTO(updated);
    return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null);
  }

  private void deleteProductImages(ProductEntity product, List<UUID> imageIds) {
    List<ProductImageEntity> images = product.getImages();
    List<ProductImageEntity> toDelete = images.stream()
        .filter(img -> imageIds.contains(img.getId()))
        .toList();

    toDelete.forEach(img -> {
      fileUtil.deleteFileByUrl(img.getUrl());
      productImageRepository.delete(img);
    });

    images.removeAll(toDelete);
  }

  private void replaceProductImages(ProductEntity product, List<ProductImageUpdateRequest> updates) {
    for (ProductImageUpdateRequest update : updates) {
      ProductImageEntity image = product.getImages().stream()
          .filter(i -> i.getId().equals(update.getId()))
          .findFirst()
          .orElseThrow(() -> new ResException(ResErrorCode.PRODUCT_IMAGE_NOT_FOUND));

      fileUtil.deleteFileByUrl(image.getUrl());

      String newUrl = fileUtil.uploadSingleImage(update.getFile());
      image.setUrl(newUrl);
    }
  }

  private void addNewImages(ProductEntity product, List<String> imageUrls) {
    List<ProductImageEntity> newImages = imageUrls.stream()
        .map(url -> ProductImageEntity.builder()
            .product(product)
            .url(url)
            .isPrimary(false)
            .build())
        .toList();

    product.getImages().addAll(newImages);
  }


  private ProductEntity findProductOrThrow(UUID id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResException(ResErrorCode.PRODUCT_NOT_FOUND));
  }

  private void applyBasicUpdates(ProductEntity product, ProductUpdateRequest request) {
    if (request.getName() != null && !request.getName().trim().isEmpty()) {
      product.setName(request.getName().trim());
    }
    if (request.getDescription() != null) {
      product.setDescription(request.getDescription().trim());
    }
    if (request.getSpecs() != null) {
      product.setSpecs(request.getSpecs());
    }
  }

  private void applyBrandUpdate(ProductEntity product, ProductUpdateRequest request) {
    if (request.getBrandId() != null) {
      BrandEntity brand = brandRepository.findById(request.getBrandId())
          .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));
      product.setBrand(brand);
    }
  }

  private void applyCategoryUpdate(ProductEntity product, ProductUpdateRequest request) {
    if (request.getCategoryId() != null) {
      CategoryEntity category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));
      product.setCategory(category);
    }
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

    if (entity.getImages() != null && !entity.getImages().isEmpty()) {
      List<String> imageUrls = entity.getImages().stream()
          .filter(img -> Boolean.FALSE.equals(img.getIsPrimary()))
          .map(ProductImageEntity::getUrl)
          .toList();
      dto.setImages(imageUrls);
    } else {
      dto.setImages(Collections.emptyList());
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
  public GeneralResponse<ProductResponse> createProduct(ProductCreateRequest request,
      List<MultipartFile> images) {
    validateProductCreationRequest(request);

    ProductEntity savedProduct = createBaseProduct(request);

    List<String> imageUrls = uploadProductImages(images);
    if (!imageUrls.isEmpty()) {
      saveProductImages(savedProduct, imageUrls);
    }

    ProductResponse response = convertToDTO(savedProduct);
    return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null);
  }

  private ProductEntity createBaseProduct(ProductCreateRequest request) {
    BrandEntity brand = brandRepository.findById(request.getBrandId())
        .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));

    CategoryEntity category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));

    ProductEntity product = ProductEntity.builder()
        .name(request.getName())
        .description(request.getDescription())
        .specs(request.getSpecs())
        .brand(brand)
        .category(category)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    return productRepository.save(product);
  }

  private List<String> uploadProductImages(List<MultipartFile> images) {
    if (images == null || images.isEmpty()) {
      return new ArrayList<>();
    }
    return fileUtil.uploadMultipleImages(images);
  }

  private void saveProductImages(ProductEntity product, List<String> imageUrls) {
    List<ProductImageEntity> imageEntities = new ArrayList<>();
    String thumbnailUrl = imageUrls.get(0);

    for (int i = 0; i < imageUrls.size(); i++) {
      ProductImageEntity imageEntity = ProductImageEntity.builder()
          .product(product)
          .url(imageUrls.get(i))
          .isPrimary(i == 0) // Ảnh đầu tiên là thumbnail
          .build();
      imageEntities.add(imageEntity);
    }

    productImageRepository.saveAll(imageEntities);
    product.setImages(imageEntities);
    product.setThumbnailUrl(thumbnailUrl);
    productRepository.save(product);
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

