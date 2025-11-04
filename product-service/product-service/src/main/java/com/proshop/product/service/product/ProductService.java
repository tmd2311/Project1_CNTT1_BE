package com.proshop.product.service.product;

import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.ProductDeleteResponse;
import com.proshop.product.dto.response.ProductResponse;


import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;


public interface ProductService {
  GeneralResponse<PageResponse<ProductResponse>> getProducts(int page, int size);
  GeneralResponse<ProductResponse> getProductById(String idStr);
  GeneralResponse<ProductDeleteResponse> deleteProduct(String idStr);
  GeneralResponse<ProductResponse> updateProduct(UUID id, ProductUpdateRequest request, List<MultipartFile> images);
  GeneralResponse<PageResponse<ProductResponse>> searchProducts(
      String name, String brand, String category,
      Double minPrice, Double maxPrice,
      int page, int size);
  GeneralResponse<ProductResponse> createProduct(ProductCreateRequest request, List<MultipartFile> images);

}