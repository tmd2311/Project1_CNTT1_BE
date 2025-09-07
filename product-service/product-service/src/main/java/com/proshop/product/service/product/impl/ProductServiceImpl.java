package com.proshop.product.service.product.impl;

import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.mapper.ProductMapper;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
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
  public GeneralResponse<ProductEntity> updateProduct(UUID id, ProductUpdateRequest request) {
      ProductEntity product = productRepository.findById(id).orElse(null);
      if (product == null) {
          return new GeneralResponse<>(
                  new ResponseStatus("404", "Không tìm thấy sản phẩm", "Not Found"),
                  null,
                  null
          );
      }

      // cập nhật thông tin
      if (request.getName() != null) product.setName(request.getName());
      if (request.getDescription() != null) product.setDescription(request.getDescription());
      if (request.getSpecs() != null) product.setSpecs(request.getSpecs());

      if (request.getBrandId() != null) {
          BrandEntity brand = brandRepository.findById(request.getBrandId()).orElse(null);
          product.setBrand(brand);
      }

      if (request.getCategoryId() != null) {
          CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElse(null);
          product.setCategory(category);
      }

      product.setUpdatedAt(LocalDateTime.now());
      ProductEntity updated = productRepository.save(product);

      return new GeneralResponse<>(
              ResponseStatus.SUCCESS_STATUS,
              updated,
              null
      );
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
}

