package com.proshop.product;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.request.ProductCreateRequest;
import com.proshop.product.dto.request.ProductImageRequest;
import com.proshop.product.dto.request.ProductUpdateRequest;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.PageResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.entity.*;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.ProductImageRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.service.product.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductEntity testProduct;
    private BrandEntity testBrand;
    private CategoryEntity testCategory;
    private ProductCreateRequest createRequest;
    private ProductUpdateRequest updateRequest;
    private SKUEntity testSku;

    @BeforeEach
    void setUp() {
        testBrand = BrandEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Brand")
                .build();

        testCategory = new CategoryEntity();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("Test Category");

        testSku = SKUEntity.builder()
                .id(UUID.randomUUID())
                .price(100.0)
                .isActive(true)
                .build();

        testProduct = ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .description("Test Description")
                .specs(Map.of("cpu", "Intel i7"))
                .brand(testBrand)
                .category(testCategory)
                .skus(Arrays.asList(testSku))
                .createdAt(LocalDateTime.now())
                .build();

        testSku.setProduct(testProduct);

        createRequest = new ProductCreateRequest();
        createRequest.setName("New Product");
        createRequest.setDescription("New Description");
        createRequest.setSpecs(Map.of("ram", "16GB"));
        createRequest.setBrandId(testBrand.getId());
        createRequest.setCategoryId(testCategory.getId());

        ProductImageRequest imageRequest = new ProductImageRequest();
        imageRequest.setUrl("http://example.com/image.png");
        imageRequest.setIsPrimary(true);
        createRequest.setImages(Arrays.asList(imageRequest));

        updateRequest = new ProductUpdateRequest();
        updateRequest.setName("Updated Product");
    }

    @Test
    void createProduct_Success() {
        // Arrange
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);
        when(productImageRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<ProductResponse> result = productService.createProduct(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        verify(productRepository, times(2)).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_NameRequired_ThrowsException() {
        // Arrange
        createRequest.setName("");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.createProduct(createRequest));
        assertEquals(ResErrorCode.PRODUCT_NAME_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createProduct_SpecsRequired_ThrowsException() {
        // Arrange
        createRequest.setSpecs(null);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.createProduct(createRequest));
        assertEquals(ResErrorCode.PRODUCT_SPECS_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createProduct_BrandNotFound_ThrowsException() {
        // Arrange
        when(brandRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.createProduct(createRequest));
        assertEquals(ResErrorCode.BRAND_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void createProduct_CategoryNotFound_ThrowsException() {
        // Arrange
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.createProduct(createRequest));
        assertEquals(ResErrorCode.CATEGORY_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void createProduct_MultiplePrimaryImages_ThrowsException() {
        // Arrange
        ProductImageRequest img1 = new ProductImageRequest();
        img1.setUrl("http://example.com/img1.png");
        img1.setIsPrimary(true);

        ProductImageRequest img2 = new ProductImageRequest();
        img2.setUrl("http://example.com/img2.png");
        img2.setIsPrimary(true);

        createRequest.setImages(Arrays.asList(img1, img2));

        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.createProduct(createRequest));
        assertEquals(ResErrorCode.PRODUCT_MULTIPLE_PRIMARY_IMAGES.code(), exception.getCode());
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findProductById(testProduct.getId()))
                .thenReturn(testProduct);

        // Act
        GeneralResponse<ProductResponse> result =
                productService.getProductById(testProduct.getId().toString());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("Test Product", result.getData().getName());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findProductById(any(UUID.class))).thenReturn(null);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.getProductById(UUID.randomUUID().toString()));
        assertEquals(ResErrorCode.PRODUCT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getProductById_InvalidId_ThrowsException() {
        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.getProductById("invalid-uuid"));
        assertEquals(ResErrorCode.BAD_REQUEST.code(), exception.getCode());
    }

    @Test
    void getProducts_Success() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        // Act
        GeneralResponse<PageResponse<ProductResponse>> result =
                productService.getProducts(0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getContent().size());

    }

    @Test
    void updateProduct_Success() {
        // Arrange
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        // Act
        GeneralResponse<ProductResponse> result =
                productService.updateProduct(testProduct.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> productService.updateProduct(UUID.randomUUID(), updateRequest));
        assertEquals(ResErrorCode.PRODUCT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void updateProduct_WithBrand_Success() {
        // Arrange
        updateRequest.setBrandId(testBrand.getId());
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(brandRepository.findById(testBrand.getId()))
                .thenReturn(Optional.of(testBrand));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        // Act
        GeneralResponse<ProductResponse> result =
                productService.updateProduct(testProduct.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(brandRepository).findById(testBrand.getId());
    }

    @Test
    void updateProduct_WithCategory_Success() {
        // Arrange
        updateRequest.setCategoryId(testCategory.getId());
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        // Act
        GeneralResponse<ProductResponse> result =
                productService.updateProduct(testProduct.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(testCategory.getId());
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));

        // Act
        var result = productService.deleteProduct(testProduct.getId().toString());

        // Assert
        assertNotNull(result);
        verify(productRepository).deleteById(testProduct.getId());
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act
        ResException exception = assertThrows(ResException.class,
                () -> productService.deleteProduct(UUID.randomUUID().toString()));
        assertEquals(ResErrorCode.PRODUCT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void searchProducts_Success() {
        // Arrange
        Page<ProductEntity> productPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(productPage);

        // Act
        GeneralResponse<PageResponse<ProductResponse>> result =
                productService.searchProducts("Test", "Brand", "Category", 50.0, 200.0, 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getContent().size());


    }

    @Test
    void updateProduct_WithImages_Success() {
        // Arrange
        ProductImageEntity existingImage = ProductImageEntity.builder()
                .id(UUID.randomUUID())
                .url("http://example.com/old.png")
                .isPrimary(false)
                .product(testProduct)
                .build();
        testProduct.setImages(new ArrayList<>(Arrays.asList(existingImage)));

        ProductImageRequest newImageRequest = new ProductImageRequest();
        newImageRequest.setUrl("http://example.com/new.png");
        newImageRequest.setIsPrimary(true);
        updateRequest.setImages(Arrays.asList(newImageRequest));

        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        // Act
        GeneralResponse<ProductResponse> result =
                productService.updateProduct(testProduct.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(any(ProductEntity.class));
    }
}
