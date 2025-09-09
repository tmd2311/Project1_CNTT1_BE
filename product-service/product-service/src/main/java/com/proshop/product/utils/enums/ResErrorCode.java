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
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "Không tìm thấy thương hiệu", "Brand not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "P005", "Không tìm thấy danh mục", "Category not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P006", "Không tìm thấy sản phẩm", "Product not found"),
    BRAND_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "B001", "Tên thương hiệu không được để trống", "Brand name is required"),
    BRAND_SLUG_REQUIRED(HttpStatus.BAD_REQUEST, "B002", "Slug thương hiệu không được để trống", "Brand slug is required");

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
