package com.proshop.product;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.request.CategoryCreateRequest;
import com.proshop.product.dto.request.CategoryImageRequest;
import com.proshop.product.dto.request.CategoryUpdateRequest;
import com.proshop.product.dto.response.CategoryResponse;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.service.category.impl.CategoryServiceImpl;
import com.proshop.product.utils.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryEntity testCategory;
    private CategoryEntity parentCategory;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;
    private MultipartFile mockFile;

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

        mockFile = new MockMultipartFile(
            "image", "test.png", "image/png", "dummy".getBytes()
        );
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(testCategory);
        when(fileUtil.uploadSingleImage(any())).thenReturn("http://example.com/test.png");

        GeneralResponse<CategoryResponse> result = categoryService.createCategory(createRequest, mockFile);

        assertNotNull(result);
        assertNotNull(result.getData());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void createCategory_NameRequired_ThrowsException() {
        createRequest.setName("");

        ResException exception = assertThrows(ResException.class,
            () -> categoryService.createCategory(createRequest, null));

        assertEquals(ResErrorCode.CATEGORY_NAME_REQUIRED.code(), exception.getCode());
    }

    @Test
    void createCategory_NameTooShort_ThrowsException() {
        createRequest.setName("A");

        ResException exception = assertThrows(ResException.class,
            () -> categoryService.createCategory(createRequest, null));

        assertEquals(ResErrorCode.CATEGORY_NAME_TOO_SHORT.code(), exception.getCode());
    }

    @Test
    void createCategory_SlugExists_ThrowsException() {
        when(categoryRepository.existsBySlug(anyString())).thenReturn(true);

        ResException exception = assertThrows(ResException.class,
            () -> categoryService.createCategory(createRequest, null));

        assertEquals(ResErrorCode.CATEGORY_SLUG_ALREADY_EXISTS.code(), exception.getCode());
    }

    @Test
    void createCategory_WithParent_Success() {
        createRequest.setParentId(parentCategory.getId());
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(parentCategory.getId()))
            .thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(testCategory);
        when(fileUtil.uploadSingleImage(any())).thenReturn("http://example.com/test.png");

        GeneralResponse<CategoryResponse> result = categoryService.createCategory(createRequest, mockFile);

        assertNotNull(result);
        verify(categoryRepository).save(argThat(cat -> cat.getParent() != null));
    }

    @Test
    void createCategory_ParentNotFound_ThrowsException() {
        createRequest.setParentId(UUID.randomUUID());
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResException exception = assertThrows(ResException.class,
            () -> categoryService.createCategory(createRequest, null));

        assertEquals(ResErrorCode.CATEGORY_PARENT_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(testCategory.getId()))
            .thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(CategoryEntity.class)))
            .thenReturn(testCategory);
        when(fileUtil.uploadSingleImage(any())).thenReturn("http://example.com/test.png");
        doNothing().when(categoryRepository).flush();

        GeneralResponse<CategoryResponse> result =
            categoryService.updateCategory(testCategory.getId(), updateRequest, mockFile);

        assertNotNull(result);
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResException exception = assertThrows(ResException.class,
            () -> categoryService.updateCategory(UUID.randomUUID(), updateRequest, null));

        assertEquals(ResErrorCode.CATEGORY_NOT_FOUND.code(), exception.getCode());
    }
}
