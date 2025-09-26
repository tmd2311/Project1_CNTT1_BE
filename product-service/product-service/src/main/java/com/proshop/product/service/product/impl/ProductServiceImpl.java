package com.proshop.product.service.product.impl;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.PageResponseUtil;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.exceptions.ResException;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;


import com.proshop.product.specification.ProductSpecification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

import com.proshop.product.utils.enums.ResErrorCode;
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
        // Sử dụng PRODUCT_NOT_FOUND thay vì RuntimeException
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.PRODUCT_NOT_FOUND));

        // Validation
        validateProductUpdateRequest(request);

        // Update fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            product.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }

        if (request.getSpecs() != null) {
            product.setSpecs(request.getSpecs());
        }

        // Brand validation
        if (request.getBrandId() != null) {
            BrandEntity brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));
            product.setBrand(brand);
        }

        // Category validation
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResException(ResErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        product.setUpdatedAt(LocalDateTime.now());
        ProductEntity updated = productRepository.save(product);

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
        validateProductCreationRequest(request);
        BrandEntity brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .specs(request.getSpecs())
                .brand(brand)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductEntity saved = productRepository.save(product);

        // map sang ProductResponse
        // TODO: Sau này bổ sung logic để lấy SKU và giá (price) cho ProductResponse
        ProductResponse response = convertToDTO(saved);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                response,
                null
        );
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

