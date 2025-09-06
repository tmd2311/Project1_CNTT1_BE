package com.proshop.product.service.product;

import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ProductService {
  Page<ProductResponse> getProducts(int page, int size);
  ProductResponse getProductById(UUID id);
  GeneralResponse<ProductDeleteResponse> deleteProduct(UUID id);
}