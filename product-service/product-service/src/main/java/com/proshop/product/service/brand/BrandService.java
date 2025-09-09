package com.proshop.product.service.brand;

import com.proshop.product.dto.request.BrandUpdateRequest;
import com.proshop.product.dto.response.BrandDeleteResponse;
import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.dto.response.GeneralResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BrandService {
    GeneralResponse<BrandDeleteResponse> deleteBrand(UUID id);
    GeneralResponse<BrandResponse> updateBrand(UUID id, BrandUpdateRequest request);
    GeneralResponse<Page<BrandResponse>> searchBrands(String name, int page, int size);
}
