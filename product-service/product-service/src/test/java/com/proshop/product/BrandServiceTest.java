package com.proshop.product;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.request.BrandCreateRequest;
import com.proshop.product.dto.request.BrandUpdateRequest;
import com.proshop.product.dto.response.BrandResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.service.brand.impl.BrandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    private BrandEntity testBrand;
    private BrandCreateRequest createRequest;
    private BrandUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testBrand = BrandEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Brand")
                .slug("test-brand")
                .logoUrl("http://example.com/logo.png")
                .build();

        createRequest = new BrandCreateRequest();
        createRequest.setName("New Brand");
        createRequest.setSlug("new-brand");
        createRequest.setLogoUrl("http://example.com/new-logo.png");

        updateRequest = new BrandUpdateRequest();
        updateRequest.setName("Updated Brand");
        updateRequest.setSlug("updated-brand");
    }

    @Test
    void createBrand_Success() {
        // Arrange
        when(brandRepository.existsBySlug(anyString())).thenReturn(false);
        when(brandRepository.save(any(BrandEntity.class))).thenReturn(testBrand);

        // Act
        GeneralResponse<BrandResponse> result = brandService.createBrand(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        verify(brandRepository).save(any(BrandEntity.class));
    }

    @Test
    void createBrand_NameRequired_ThrowsException() {
        // Arrange
        createRequest.setName("");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> brandService.createBrand(createRequest));
        assertEquals(ResErrorCode.BRAND_NAME_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createBrand_SlugRequired_ThrowsException() {
        // Arrange
        createRequest.setSlug("");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> brandService.createBrand(createRequest));
        assertEquals(ResErrorCode.BRAND_SLUG_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createBrand_SlugExists_ThrowsException() {
        // Arrange
        when(brandRepository.existsBySlug(anyString())).thenReturn(true);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> brandService.createBrand(createRequest));
        assertEquals(ResErrorCode.BRAND_ALREADY_EXISTS.code(), exception.getCode());
    }

    @Test
    void getBrandById_Success() {
        // Arrange
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));

        // Act
        GeneralResponse<BrandResponse> result = brandService.getBrandById(testBrand.getId());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("Test Brand", result.getData().getName());
    }

    @Test
    void getBrandById_NotFound_ThrowsException() {
        // Arrange
        when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> brandService.getBrandById(UUID.randomUUID()));
        assertEquals(ResErrorCode.BRAND_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getAllBrands_Success() {
        // Arrange
        Page<BrandEntity> brandPage = new PageImpl<>(Arrays.asList(testBrand));
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(brandPage);

        // Act
        GeneralResponse<Page<BrandResponse>> result = brandService.getAllBrands(0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getContent().size());
    }

    @Test
    void updateBrand_Success() {
        // Arrange
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));
        when(brandRepository.save(any(BrandEntity.class))).thenReturn(testBrand);

        // Act
        GeneralResponse<BrandResponse> result =
                brandService.updateBrand(testBrand.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(brandRepository).save(any(BrandEntity.class));
    }

    @Test
    void updateBrand_NotFound_ThrowsException() {
        // Arrange
        when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> brandService.updateBrand(UUID.randomUUID(), updateRequest));
        assertEquals(ResErrorCode.BRAND_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void deleteBrand_Success() {
        // Arrange
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));

        // Act
        var result = brandService.deleteBrand(testBrand.getId());

        // Assert
        assertNotNull(result);
        verify(brandRepository).deleteById(testBrand.getId());
    }

    @Test
    void searchBrands_Success() {
        // Arrange
        Page<BrandResponse> brandPage = new PageImpl<>(Arrays.asList(new BrandResponse()));
        when(brandRepository.searchBrands(anyString(), any(Pageable.class)))
                .thenReturn(brandPage);

        // Act
        GeneralResponse<Page<BrandResponse>> result =
                brandService.searchBrands("test", 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
    }
}
