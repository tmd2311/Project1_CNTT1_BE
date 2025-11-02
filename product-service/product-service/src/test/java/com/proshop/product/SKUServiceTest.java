package com.proshop.product;

import com.proshop.product.dto.request.SKURequest;
import com.proshop.product.dto.request.SKUstockRequest;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.SKURepository;
import com.proshop.product.service.sku.impl.SKUServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SKUServiceTest {

    @Mock
    private SKURepository skuRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SKUServiceImpl skuService;

    private SKUEntity testSku;
    private ProductEntity testProduct;
    private SKURequest skuRequest;

    @BeforeEach
    void setUp() {
        testProduct = ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .build();

        testSku = SKUEntity.builder()
                .id(UUID.randomUUID())
                .product(testProduct)
                .skuCode("SKU-001")
                .specs(Map.of("color", "black", "size", "M"))
                .price(100.0)
                .discountPrice(90.0)
                .stock(50)
                .barcode("1234567890")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        skuRequest = new SKURequest();
        skuRequest.setProductId(testProduct.getId());
        skuRequest.setSkuCode("SKU-002");
        skuRequest.setSpecs(Map.of("color", "white", "size", "L"));
        skuRequest.setPrice(120.0);
        skuRequest.setDiscountPrice(110.0);
        skuRequest.setStock(30);
        skuRequest.setBarcode("0987654321");
        skuRequest.setIsActive(true);
    }

    @Test
    void createSKU_Success() {
        // Arrange
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);



        // Act
        SKUResponse result = skuService.createSKU(skuRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("SKU-001", result.getSkuCode());
        verify(skuRepository).save(any(SKUEntity.class));
    }

    @Test
    void createSKU_ProductNotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> skuService.createSKU(skuRequest));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void createSKU_DefaultActiveTrue() {
        // Arrange
        skuRequest.setIsActive(null);
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);

        // Act
        SKUResponse result = skuService.createSKU(skuRequest);

        // Assert
        assertNotNull(result);
        verify(skuRepository).save(argThat(sku -> sku.getIsActive() == true));
    }

    @Test
    void getById_Success() {
        // Arrange
        when(skuRepository.findById(testSku.getId())).thenReturn(Optional.of(testSku));

        // Act
        SKUResponse result = skuService.getById(testSku.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testSku.getId(), result.getId());
        assertEquals("SKU-001", result.getSkuCode());
        assertEquals(100.0, result.getPrice());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        // Arrange
        when(skuRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> skuService.getById(UUID.randomUUID()));
        assertEquals("SKU not found", exception.getMessage());
    }

    @Test
    void getAll_Success() {
        // Arrange
        when(skuRepository.findAll()).thenReturn(Arrays.asList(testSku));

        // Act
        List<SKUResponse> result = skuService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU-001", result.get(0).getSkuCode());
    }

    @Test
    void getAll_EmptyList() {
        // Arrange
        when(skuRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<SKUResponse> result = skuService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateSKU_Success() {
        // Arrange
        when(skuRepository.findById(testSku.getId())).thenReturn(Optional.of(testSku));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);
        when(productRepository.findById(any()))
                .thenReturn(Optional.of(testProduct));

        // Act
        SKUResponse result = skuService.updateSKU(testSku.getId(), skuRequest);

        // Assert
        assertNotNull(result);
        verify(skuRepository).save(argThat(sku ->
                "SKU-002".equals(sku.getSkuCode()) &&
                        sku.getPrice() == 120.0
        ));
    }

    @Test
    void updateSKU_NotFound_ThrowsException() {
        // Arrange
        when(skuRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> skuService.updateSKU(UUID.randomUUID(), skuRequest));
        assertEquals("SKU not found", exception.getMessage());
    }

    @Test
    void updateSKU_WithNewProduct_Success() {
        // Arrange
        ProductEntity newProduct = ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("New Product")
                .build();
        skuRequest.setProductId(newProduct.getId());

        when(skuRepository.findById(testSku.getId())).thenReturn(Optional.of(testSku));
        when(productRepository.findById(newProduct.getId()))
                .thenReturn(Optional.of(newProduct));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);

        // Act
        SKUResponse result = skuService.updateSKU(testSku.getId(), skuRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).findById(newProduct.getId());
    }

    @Test
    void updateStockSKU_Success() {
        // Arrange
        SKUstockRequest stockRequest = new SKUstockRequest();
        stockRequest.setProductId(testProduct.getId());
        stockRequest.setStock(100);

        when(skuRepository.findById(testSku.getId())).thenReturn(Optional.of(testSku));
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);

        // Act
        SKUResponse result = skuService.updateStockSKU(testSku.getId(), stockRequest);

        // Assert
        assertNotNull(result);
        verify(skuRepository).save(argThat(sku -> sku.getStock() == 100));
    }

    @Test
    void updateStockSKU_NotFound_ThrowsException() {
        // Arrange
        SKUstockRequest stockRequest = new SKUstockRequest();
        stockRequest.setStock(100);

        when(skuRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> skuService.updateStockSKU(UUID.randomUUID(), stockRequest));
        assertEquals("SKU not found", exception.getMessage());
    }

    @Test
    void deleteSKU_Success() {
        // Arrange
        doNothing().when(skuRepository).deleteById(testSku.getId());

        // Act
        skuService.deleteSKU(testSku.getId());

        // Assert
        verify(skuRepository).deleteById(testSku.getId());
    }

    @Test
    void deleteSKU_NotFound_NoException() {
        // Arrange
        doNothing().when(skuRepository).deleteById(any(UUID.class));

        // Act
        skuService.deleteSKU(UUID.randomUUID());

        // Assert
        verify(skuRepository).deleteById(any(UUID.class));
    }

    @Test
    void createSKU_WithAllFields_Success() {
        // Arrange
        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);

        // Act
        SKUResponse result = skuService.createSKU(skuRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getProductId());
        assertEquals(100.0, result.getPrice());
        assertEquals(90.0, result.getDiscountPrice());
        assertEquals(50, result.getStock());
        assertEquals("1234567890", result.getBarcode());
        assertTrue(result.getIsActive());
        verify(skuRepository).save(any(SKUEntity.class));
    }

    @Test
    void updateSKU_AllFields_Success() {
        // Arrange
        when(skuRepository.findById(testSku.getId())).thenReturn(Optional.of(testSku));
        when(skuRepository.save(any(SKUEntity.class))).thenReturn(testSku);
        when(productRepository.findById(any()))
                .thenReturn(Optional.of(testProduct));


        // Act
        SKUResponse result = skuService.updateSKU(testSku.getId(), skuRequest);

        // Assert
        assertNotNull(result);
        verify(skuRepository).save(argThat(sku -> {
            return "SKU-002".equals(sku.getSkuCode()) &&
                    sku.getPrice() == 120.0 &&
                    sku.getDiscountPrice() == 110.0 &&
                    sku.getStock() == 30 &&
                    "0987654321".equals(sku.getBarcode()) &&
                    sku.getIsActive() == true &&
                    sku.getUpdatedAt() != null;
        }));
    }
}

