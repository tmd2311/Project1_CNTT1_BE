package com.proshop.product.service.product;

import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.ProductEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ProductService {
  Page<ProductResponse> getProducts(int page, int size);
  ProductResponse getProductById(UUID id);
}
