package com.proshop.product.service.product;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ProductResponse;


import java.util.UUID;


public interface ProductService {
  GeneralResponse<PageResponse<ProductResponse>> getProducts(int page, int size);
  GeneralResponse<ProductResponse> getProductById(String idStr);
  GeneralResponse<ProductDeleteResponse> deleteProduct(String idStr);
  GeneralResponse<ProductResponse> updateProduct(UUID id, ProductUpdateRequest request);
  GeneralResponse<PageResponse<ProductResponse>> searchProducts(String name, Double minPrice, Double maxPrice, int page, int size);
  GeneralResponse<ProductResponse> createProduct(ProductCreateRequest request);

}