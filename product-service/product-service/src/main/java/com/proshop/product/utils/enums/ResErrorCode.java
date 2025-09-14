package com.proshop.product.utils.enums;

import org.springframework.http.HttpStatus;

public enum ResErrorCode {

    // --- General ---
    SUCCESS(HttpStatus.OK, "GN001", "Thành công", "Success"),
    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GN002", "Thất bại", "An error occurred, please try again later"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GN003", "Truyền sai tham số", "Invalid request parameters"),
    FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "GN004", "Trường bắt buộc không được để trống", "Required field is missing"),
    MISS_PARAM(HttpStatus.BAD_REQUEST, "GN005", "Truyền lên thiếu tham số", "Passing up without parameters"),

    // --- Product ---
    PRODUCT_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "P001", "Tên sản phẩm không được để trống", "Product name is required"),
    PRODUCT_DESCRIPTION_REQUIRED(HttpStatus.BAD_REQUEST, "P002", "Mô tả sản phẩm không được để trống", "Product description is required"),
    PRODUCT_SPECS_REQUIRED(HttpStatus.BAD_REQUEST, "P003", "Thông số kỹ thuật phải có ít nhất 1 mục", "Product specs is required"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P006", "Không tìm thấy sản phẩm", "Product not found"),
    // --- Brand ---
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "Không tìm thấy thương hiệu", "Brand not found"),
    BRAND_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "B001", "Tên thương hiệu không được để trống", "Brand name is required"),
    BRAND_SLUG_REQUIRED(HttpStatus.BAD_REQUEST, "B002", "Slug thương hiệu không được để trống", "Brand slug is required"),
    BRAND_ALREADY_EXISTS(HttpStatus.CONFLICT, "B004", "Thương hiệu đã tồn tại", "Brand already exists"),
    // --- Category ---
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "P005", "Không tìm thấy danh mục", "Category not found"),
    CATEGORY_PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "Không tìm thấy danh mục cha", "Parent category not found"),
    CATEGORY_CIRCULAR_REFERENCE(HttpStatus.BAD_REQUEST, "C007", "Không thể tạo tham chiếu vòng lặp", "Cannot create circular reference"),
    CATEGORY_NAME_TOO_SHORT(HttpStatus.BAD_REQUEST, "C008", "Tên danh mục quá ngắn", "Category name too short"),
    CATEGORY_SLUG_TOO_SHORT(HttpStatus.BAD_REQUEST, "C009", "Slug danh mục quá ngắn", "Category slug too short"),
    CATEGORY_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "C010", "Slug danh mục đã tồn tại", "Category slug already exists"),
    CATEGORY_SLUG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "C011", "Định dạng slug danh mục không hợp lệ", "Category slug format invalid"),
    CATEGORY_MAX_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "C012", "Vượt quá độ sâu phân cấp tối đa", "Maximum hierarchy depth exceeded"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.BAD_REQUEST, "C013", "Không thể xóa danh mục đang chứa sản phẩm", "Cannot delete category that contains products"),
    CATEGORY_HAS_CHILDREN(HttpStatus.BAD_REQUEST, "C014", "Không thể xóa danh mục đang có danh mục con", "Cannot delete category that has subcategories");


    private final HttpStatus status;
    private final String code;
    private final String message;
    private final String label;

    ResErrorCode(HttpStatus status, String code, String message, String label) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String label() {
        return label;
    }
}
