package com.proshop.product;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryImageRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.CategoryImageEntity;
import com.proshop.product.repository.CategoryImageRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.service.category.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryImageRepository categoryImageRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryEntity testCategory;
    private CategoryEntity parentCategory;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        parentCategory = new CategoryEntity();
        parentCategory.setId(UUID.randomUUID());
        parentCategory.setName("Parent Category");
        parentCategory.setSlug("parent-category");

        testCategory = new CategoryEntity();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");
        testCategory.setParent(parentCategory);

        createRequest = new CategoryCreateRequest();
        createRequest.setName("New Category");
        createRequest.setSlug("new-category");

        CategoryImageRequest imageRequest = new CategoryImageRequest();
        imageRequest.setUrl("http://example.com/image.png");
        createRequest.setImage(imageRequest);

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Updated Category");
    }

    @Test
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(testCategory);
        when(categoryImageRepository.save(any(CategoryImageEntity.class)))
                .thenReturn(new CategoryImageEntity());

        // Act
        GeneralResponse<CategoryResponse> result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void createCategory_NameRequired_ThrowsException() {
        // Arrange
        createRequest.setName("");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.createCategory(createRequest));
        assertEquals(ResErrorCode.CATEGORY_NAME_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createCategory_NameTooShort_ThrowsException() {
        // Arrange
        createRequest.setName("A");

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.createCategory(createRequest));
        assertEquals(ResErrorCode.CATEGORY_NAME_TOO_SHORT.code(), exception.getCode());
    }

    @Test
    void createCategory_SlugExists_ThrowsException() {
        // Arrange
        when(categoryRepository.existsBySlug(anyString())).thenReturn(true);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.createCategory(createRequest));
        assertEquals(ResErrorCode.CATEGORY_SLUG_ALREADY_EXISTS.code(), exception.getCode());
    }

    @Test
    void createCategory_WithParent_Success() {
        // Arrange
        createRequest.setParentId(parentCategory.getId());
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(parentCategory.getId()))
                .thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(testCategory);

        // Act
        GeneralResponse<CategoryResponse> result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(argThat(cat -> cat.getParent() != null));
    }

    @Test
    void createCategory_ParentNotFound_ThrowsException() {
        // Arrange
        createRequest.setParentId(UUID.randomUUID());
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.createCategory(createRequest));
        assertEquals(ResErrorCode.CATEGORY_PARENT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));

        // Act
        GeneralResponse<CategoryResponse> result =
                categoryService.getCategoryById(testCategory.getId());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void getCategoryById_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.getCategoryById(UUID.randomUUID()));
        assertEquals(ResErrorCode.CATEGORY_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getAllCategories_Success() {
        // Arrange
        when(categoryRepository.findAll(any(Sort.class)))
                .thenReturn(Arrays.asList(testCategory));

        // Act
        GeneralResponse<List<CategoryResponse>> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }

    @Test
    void updateCategory_Success() {
        // Arrange
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenReturn(testCategory);
        doNothing().when(categoryRepository).flush(); // <-- sửa chỗ này


        // Act
        GeneralResponse<CategoryResponse> result =
                categoryService.updateCategory(testCategory.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> categoryService.updateCategory(UUID.randomUUID(), updateRequest));
        assertEquals(ResErrorCode.CATEGORY_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        testCategory.setChildren(new ArrayList<>());
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));
        when(categoryRepository.hasProducts(testCategory.getId())).thenReturn(false);

        // Act
        var result = categoryService.deleteCategory(testCategory.getId());

        // Assert
        assertNotNull(result);
        verify(categoryRepository).deleteById(testCategory.getId());
    }

    @Test
    void deleteCategory_HasChildren_CannotDelete() {
        // Arrange
        testCategory.setChildren(Arrays.asList(new CategoryEntity()));
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));

        // Act
        var result = categoryService.deleteCategory(testCategory.getId());

        // Assert
        assertNotNull(result);
        assertEquals("400", result.getStatus().getCode());
    }

    @Test
    void getRootCategories_Success() {
        // Arrange
        when(categoryRepository.findByParentIsNull(any(Sort.class)))
                .thenReturn(Arrays.asList(parentCategory));

        // Act
        GeneralResponse<List<CategoryResponse>> result = categoryService.getRootCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }

    @Test
    void getCategoryChildren_Success() {
        // Arrange
        CategoryEntity child = new CategoryEntity();
        child.setName("Child");
        testCategory.setChildren(Arrays.asList(child));

        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));

        // Act
        GeneralResponse<List<CategoryResponse>> result =
                categoryService.getCategoryChildren(testCategory.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }

    @Test
    void getCategoryBreadcrumb_Success() {
        // Arrange
        when(categoryRepository.findById(testCategory.getId()))
                .thenReturn(Optional.of(testCategory));

        // Act
        GeneralResponse<List<CategoryResponse>> result =
                categoryService.getCategoryBreadcrumb(testCategory.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().size() >= 2); // Parent + current
    }

    @Test
    void getCategoryBySlug_Success() {
        // Arrange
        when(categoryRepository.findBySlug("test-category"))
                .thenReturn(Optional.of(testCategory));

        // Act
        GeneralResponse<CategoryResponse> result =
                categoryService.getCategoryBySlug("test-category");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void getProductCountInCategory_Success() {
        // Arrange
        when(categoryRepository.existsById(testCategory.getId())).thenReturn(true);
        when(categoryRepository.countProductsInCategory(testCategory.getId()))
                .thenReturn(5L);

        // Act
        GeneralResponse<Long> result =
                categoryService.getProductCountInCategory(testCategory.getId(), false);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getData());
    }
}
