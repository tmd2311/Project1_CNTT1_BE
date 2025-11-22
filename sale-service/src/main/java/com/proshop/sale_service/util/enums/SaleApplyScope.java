package com.proshop.sale_service.util.enums;

/**
 * Phạm vi áp dụng của Sale
 */
public enum SaleApplyScope {
    ALL_PRODUCTS,      // Tất cả sản phẩm
    SPECIFIC_PRODUCTS, // Sản phẩm cụ thể (chọn từng product/SKU)
    CATEGORY,          // Theo danh mục
    BRAND              // Theo thương hiệu
}
