package com.proshop.product.service.brand.impl;

import com.proshop.product.dto.request.BrandUpdateRequest;
import com.proshop.product.dto.response.BrandDeleteResponse;
import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.exceptions.ResException;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.service.brand.BrandService;
import com.proshop.product.utils.enums.ResErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public GeneralResponse<BrandDeleteResponse> deleteBrand(UUID id) {
        BrandEntity brand = brandRepository.findById(id).orElse(null);
        if (brand == null) {
            return new GeneralResponse<>(
                    new ResponseStatus("404", "Không tìm thấy thương hiệu", "Brand Not Found"),
                    null,
                    null
            );
        }

        BrandDeleteResponse data = new BrandDeleteResponse(brand.getId(), brand.getName());
        brandRepository.deleteById(id);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                data,
                null
        );
    }

    @Override
    @Transactional
    public GeneralResponse<BrandResponse> updateBrand(UUID id, BrandUpdateRequest request) {
        // Find brand
        BrandEntity brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.BRAND_NOT_FOUND));

        // Validation
        validateBrandUpdateRequest(request);

        // Update fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            brand.setName(request.getName().trim());
        }

        if (request.getLogoUrl() != null) {
            brand.setLogoUrl(request.getLogoUrl().trim());
        }

        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            brand.setSlug(request.getSlug().trim());
        }

        BrandEntity updated = brandRepository.save(brand);
        BrandResponse brandResponse = convertToDTO(updated);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                brandResponse,
                null
        );
    }

    @Override
    public GeneralResponse<Page<BrandResponse>> searchBrands(String name, int page, int size) {
        // Clean parameters
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }

        // Create Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        // Call repository với pagination
        Page<BrandResponse> brands = brandRepository.searchBrands(name, pageable);

        return new GeneralResponse<>(
                ResponseStatus.SUCCESS_STATUS,
                brands,
                null
        );
    }

    private void validateBrandUpdateRequest(BrandUpdateRequest request) {
        if (request.getName() != null && request.getName().isBlank()) {
            throw new ResException(ResErrorCode.BRAND_NAME_REQUIRED);
        }

        if (request.getSlug() != null && request.getSlug().isBlank()) {
            throw new ResException(ResErrorCode.BRAND_SLUG_REQUIRED);
        }
    }

    private BrandResponse convertToDTO(BrandEntity entity) {
        BrandResponse dto = new BrandResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setSlug(entity.getSlug());
        return dto;
    }
}