package com.proshop.product.service.brand;

import com.proshop.product.dto.request.BrandCreateRequest;
import com.proshop.product.dto.request.BrandUpdateRequest;
import com.proshop.product.dto.response.BrandDeleteResponse;
import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.dto.response.GeneralResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BrandService {
    GeneralResponse<BrandDeleteResponse> deleteBrand(UUID id);
    GeneralResponse<BrandResponse> updateBrand(UUID id, BrandUpdateRequest request, MultipartFile image);
    GeneralResponse<Page<BrandResponse>> searchBrands(String name, int page, int size);
    GeneralResponse<BrandResponse> createBrand(BrandCreateRequest request, MultipartFile image);
    GeneralResponse<BrandResponse> getBrandById(UUID id);
    GeneralResponse<Page<BrandResponse>> getAllBrands(int page, int size);
}
