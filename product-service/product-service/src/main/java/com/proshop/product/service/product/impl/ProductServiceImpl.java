package com.proshop.product.service.product.impl;

import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.mapper.ProductMapper;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.ProductService;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  @Override
  public Page<ProductResponse> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<ProductEntity> products = productRepository.findAll(pageable);

    return products.map(productMapper::toDTO);
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
}

