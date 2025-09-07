package com.proshop.product.service.product.impl;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.exceptions.ResException;
import com.proshop.product.mapper.ProductMapper;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

import com.proshop.product.utils.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public Page<ProductResponse> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return productRepository.findAllProductDTO(pageable);
  }

  @Override
  public ProductResponse getProductById(UUID id) {
    ProductEntity entity = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    return productMapper.toDTO(entity); // <- map entity sang DTO
  }

  @Override
  public GeneralResponse<ProductDeleteResponse> deleteProduct(UUID id) {
      ProductEntity product = productRepository.findById(id).orElse(null);
      if (product == null) {
          return new GeneralResponse<>(
                  new ResponseStatus("404", "Không tìm thấy sản phẩm", "Not Found"),
                  null,
                  null
          );
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
    public GeneralResponse<ProductResponse> updateProduct(UUID id, ProductUpdateRequest request) {
        // Tìm product
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy sản phẩm", "Not Found"),
                    null,
                    null
            );
        }

        // Validate và update fields
        try {
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                product.setName(request.getName().trim());
            }

            if (request.getDescription() != null) {
                product.setDescription(request.getDescription().trim());
            }

            if (request.getSpecs() != null) {
                product.setSpecs(request.getSpecs());
            }

            // Validate và update brand
            if (request.getBrandId() != null) {
                BrandEntity brand = brandRepository.findById(request.getBrandId()).orElse(null);
                if (brand == null) {
                    return new GeneralResponse<>(
                            new ResponseStatus("400", "Brand không tồn tại", "Bad Request"),
                            null,
                            null
                    );
                }
                product.setBrand(brand);
            }

            // Validate và update category
            if (request.getCategoryId() != null) {
                CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElse(null);
                if (category == null) {
                    return new GeneralResponse<>(
                            new ResponseStatus("400", "Category không tồn tại", "Bad Request"),
                            null,
                            null
                    );
                }
                product.setCategory(category);
            }

            product.setUpdatedAt(LocalDateTime.now());
            ProductEntity updated = productRepository.save(product);

            // Convert sang DTO để tránh circular reference
            ProductResponse productResponse = convertToDTO(updated);

            return new GeneralResponse<>(
                    ResponseStatus.SUCCESS_STATUS,
                    productResponse,
                    null
            );

        } catch (Exception e) {
            return new GeneralResponse<>(
                    new ResponseStatus("500", "Lỗi server: " + e.getMessage(), "Internal Error"),
                    null,
                    null
            );
        }
    }

    // Helper method convert Entity to DTO
    private ProductResponse convertToDTO(ProductEntity entity) {
        ProductResponse dto = new ProductResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

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
    public GeneralResponse<List<ProductResponse>> searchProducts(String name, Double minPrice, Double maxPrice) {
        // Clean parameters
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }

        // Gọi trực tiếp - không cần mapper
        List<ProductResponse> products = productRepository.searchProducts(name, minPrice, maxPrice);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                products,
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
        ProductResponse response = new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getBrand() != null ? saved.getBrand().getName() : null,
                saved.getCategory() != null ? saved.getCategory().getName() : null,
                null, // price: chưa có SKU nên để null
                saved.getImages() != null && !saved.getImages().isEmpty()
                        ? saved.getImages().get(0).getUrl() : null // lấy thumbnail đầu tiên nếu có
        );

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

