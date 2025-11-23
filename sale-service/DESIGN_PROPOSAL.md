# SALE SERVICE - THIẾT KẾ CHI TIẾT

## 1. DATABASE SCHEMA

### 1.1. Sales Table (Chương trình giảm giá sản phẩm)
```sql
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Loại sale
    sale_type VARCHAR(50) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT, BUY_X_GET_Y
    sale_value DECIMAL(10,2) NOT NULL,

    -- Giới hạn
    min_purchase_quantity INT DEFAULT 1,
    max_discount_amount DECIMAL(10,2),

    -- Phạm vi áp dụng
    apply_scope VARCHAR(50) NOT NULL, -- ALL_PRODUCTS, SPECIFIC_PRODUCTS, CATEGORY, BRAND

    -- Thời gian
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,

    -- Trạng thái
    status VARCHAR(50) NOT NULL, -- SCHEDULED, ACTIVE, EXPIRED, PAUSED
    is_active BOOLEAN DEFAULT true,
    is_delete BOOLEAN DEFAULT false,

    -- Priority (sale nào được ưu tiên)
    priority INT DEFAULT 0,

    -- Hình ảnh
    banner_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),

    -- Tracking
    total_applied_count INT DEFAULT 0,

    -- Audit
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sales_code ON sales(code);
CREATE INDEX idx_sales_status ON sales(status);
CREATE INDEX idx_sales_dates ON sales(start_date, end_date);
```

### 1.2. Sale Products Table (Sản phẩm tham gia sale)
```sql
CREATE TABLE sale_products (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,

    -- Có thể áp dụng cho Product hoặc SKU cụ thể
    product_id BIGINT,
    sku_id BIGINT,
    category_id BIGINT,
    brand_id BIGINT,

    -- Giá gốc và giá sau sale (snapshot khi apply)
    original_price DECIMAL(10,2),
    sale_price DECIMAL(10,2),
    discount_amount DECIMAL(10,2),

    -- Trạng thái
    is_applied BOOLEAN DEFAULT false,
    applied_at TIMESTAMP,
    reverted_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sale_id) REFERENCES sales(id),

    -- Đảm bảo không duplicate
    UNIQUE(sale_id, product_id, sku_id)
);

CREATE INDEX idx_sale_products_sale_id ON sale_products(sale_id);
CREATE INDEX idx_sale_products_product_id ON sale_products(product_id);
CREATE INDEX idx_sale_products_sku_id ON sale_products(sku_id);
```

### 1.3. Vouchers Table (Mã giảm giá đơn hàng)
```sql
CREATE TABLE vouchers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Loại voucher
    voucher_type VARCHAR(50) NOT NULL, -- ORDER_DISCOUNT, SHIPPING_DISCOUNT, GIFT
    discount_type VARCHAR(50) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT
    discount_value DECIMAL(10,2) NOT NULL,

    -- Điều kiện áp dụng
    min_order_value DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2),

    -- Giới hạn sử dụng
    total_quantity INT NOT NULL, -- Tổng số voucher
    used_count INT DEFAULT 0, -- Đã sử dụng
    usage_limit_per_user INT DEFAULT 1, -- Mỗi user dùng được bao nhiêu lần

    -- Phạm vi user
    user_scope VARCHAR(50) NOT NULL, -- ALL_USERS, SPECIFIC_USERS, NEW_USERS, VIP_USERS

    -- Thời gian
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,

    -- Trạng thái
    status VARCHAR(50) NOT NULL, -- SCHEDULED, ACTIVE, EXPIRED, PAUSED
    is_active BOOLEAN DEFAULT true,
    is_delete BOOLEAN DEFAULT false,

    -- Hình ảnh
    banner_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),

    -- Audit
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vouchers_code ON vouchers(code);
CREATE INDEX idx_vouchers_status ON vouchers(status);
CREATE INDEX idx_vouchers_dates ON vouchers(start_date, end_date);
```

### 1.4. Voucher Usage Table (Tracking sử dụng voucher)
```sql
CREATE TABLE voucher_usage (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,

    -- Thông tin giảm giá
    order_value DECIMAL(10,2),
    discount_amount DECIMAL(10,2),

    -- Trạng thái
    status VARCHAR(50) NOT NULL, -- USED, CANCELLED, REFUNDED

    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

CREATE INDEX idx_voucher_usage_voucher_id ON voucher_usage(voucher_id);
CREATE INDEX idx_voucher_usage_user_id ON voucher_usage(user_id);
CREATE INDEX idx_voucher_usage_order_id ON voucher_usage(order_id);
```

### 1.5. Voucher Users Table (User được phép dùng voucher)
```sql
CREATE TABLE voucher_users (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    UNIQUE(voucher_id, user_id)
);

CREATE INDEX idx_voucher_users_voucher_id ON voucher_users(voucher_id);
CREATE INDEX idx_voucher_users_user_id ON voucher_users(user_id);
```

---

## 2. ENTITIES & ENUMS

### 2.1. Enums

```java
// Sale Type
public enum SaleType {
    PERCENTAGE,        // Giảm theo %
    FIXED_AMOUNT,      // Giảm số tiền cố định
    BUY_X_GET_Y        // Mua X tặng Y
}

// Sale Apply Scope
public enum SaleApplyScope {
    ALL_PRODUCTS,      // Tất cả sản phẩm
    SPECIFIC_PRODUCTS, // Sản phẩm cụ thể
    CATEGORY,          // Theo danh mục
    BRAND              // Theo thương hiệu
}

// Sale/Voucher Status
public enum PromotionStatus {
    SCHEDULED,  // Đã lên lịch, chưa bắt đầu
    ACTIVE,     // Đang hoạt động
    EXPIRED,    // Hết hạn
    PAUSED      // Tạm dừng
}

// Voucher Type
public enum VoucherType {
    ORDER_DISCOUNT,    // Giảm giá đơn hàng
    SHIPPING_DISCOUNT, // Giảm phí ship
    GIFT               // Tặng quà
}

// Voucher User Scope
public enum VoucherUserScope {
    ALL_USERS,      // Tất cả user
    SPECIFIC_USERS, // User cụ thể
    NEW_USERS,      // User mới
    VIP_USERS       // User VIP
}

// Discount Type
public enum DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT
}
```

---

## 3. BATCH JOBS (Scheduled Tasks)

### 3.1. Sale Activation Job
- **Chạy**: Mỗi phút hoặc mỗi 5 phút
- **Chức năng**:
  - Tìm các sale có `status = SCHEDULED` và `start_date <= now()`
  - Chuyển status sang `ACTIVE`
  - Apply giảm giá lên các sản phẩm tương ứng
  - Gọi Product Service để cập nhật giá

### 3.2. Sale Expiration Job
- **Chạy**: Mỗi phút hoặc mỗi 5 phút
- **Chức năng**:
  - Tìm các sale có `status = ACTIVE` và `end_date <= now()`
  - Chuyển status sang `EXPIRED`
  - Revert giá sản phẩm về giá gốc
  - Gọi Product Service để cập nhật giá

### 3.3. Voucher Status Update Job
- **Chạy**: Mỗi giờ
- **Chức năng**:
  - Activate vouchers khi đến start_date
  - Expire vouchers khi đến end_date
  - Vô hiệu hóa vouchers đã hết số lượng

### 3.4. Cleanup Job
- **Chạy**: Mỗi ngày lúc 2h sáng
- **Chức năng**:
  - Dọn dẹp các bản ghi cũ
  - Archive dữ liệu sale/voucher đã hết hạn lâu

---

## 4. API ENDPOINTS

### 4.1. Sale APIs

```
POST   /api/v1/sales                          - Tạo chương trình sale
GET    /api/v1/sales                          - Danh sách sales (paginated)
GET    /api/v1/sales/{id}                     - Chi tiết sale
PUT    /api/v1/sales/{id}                     - Cập nhật sale
DELETE /api/v1/sales/{id}                     - Xóa sale
PATCH  /api/v1/sales/{id}/activate            - Kích hoạt sale (manual)
PATCH  /api/v1/sales/{id}/pause               - Tạm dừng sale
POST   /api/v1/sales/{id}/products            - Thêm sản phẩm vào sale
DELETE /api/v1/sales/{id}/products/{productId} - Xóa sản phẩm khỏi sale
GET    /api/v1/sales/{id}/products            - Danh sách sản phẩm trong sale
GET    /api/v1/sales/active                   - Danh sách sale đang active
POST   /api/v1/sales/{id}/apply               - Áp dụng sale (update giá SKU)
POST   /api/v1/sales/{id}/revert              - Hoàn giá gốc
```

### 4.2. Voucher APIs

```
POST   /api/v1/vouchers                       - Tạo voucher
GET    /api/v1/vouchers                       - Danh sách vouchers
GET    /api/v1/vouchers/{id}                  - Chi tiết voucher
GET    /api/v1/vouchers/code/{code}           - Lấy voucher theo code
PUT    /api/v1/vouchers/{id}                  - Cập nhật voucher
DELETE /api/v1/vouchers/{id}                  - Xóa voucher
POST   /api/v1/vouchers/{id}/users            - Gán voucher cho users
POST   /api/v1/vouchers/validate              - Validate voucher (cho order)
POST   /api/v1/vouchers/apply                 - Áp dụng voucher vào order
GET    /api/v1/vouchers/user/{userId}         - Vouchers của user
GET    /api/v1/vouchers/user/{userId}/available - Vouchers khả dụng của user
```

---

## 5. BUSINESS LOGIC QUAN TRỌNG

### 5.1. Apply Sale to Products

```java
@Transactional
public void applySaleToProducts(Long saleId) {
    Sale sale = findById(saleId);

    // 1. Lấy danh sách sản phẩm cần apply
    List<SaleProduct> saleProducts = getSaleProducts(sale);

    // 2. Tính giá sale cho từng sản phẩm
    for (SaleProduct sp : saleProducts) {
        SKUResponse sku = productClient.getSKU(sp.getSkuId());

        Double originalPrice = sku.getPrice();
        Double discountedPrice = calculateDiscountedPrice(originalPrice, sale);

        sp.setOriginalPrice(originalPrice);
        sp.setSalePrice(discountedPrice);
        sp.setDiscountAmount(originalPrice - discountedPrice);
        sp.setIsApplied(true);
        sp.setAppliedAt(LocalDateTime.now());

        // 3. Call Product Service để update giá
        productClient.updateSKUPrice(sp.getSkuId(), discountedPrice);
    }

    // 4. Update sale status
    sale.setStatus(PromotionStatus.ACTIVE);
    saleRepository.save(sale);
}
```

### 5.2. Revert Sale Prices

```java
@Transactional
public void revertSalePrices(Long saleId) {
    Sale sale = findById(saleId);

    List<SaleProduct> appliedProducts =
        saleProductRepository.findBySaleIdAndIsAppliedTrue(saleId);

    for (SaleProduct sp : appliedProducts) {
        // Revert về giá gốc
        productClient.updateSKUPrice(sp.getSkuId(), sp.getOriginalPrice());

        sp.setIsApplied(false);
        sp.setRevertedAt(LocalDateTime.now());
    }

    sale.setStatus(PromotionStatus.EXPIRED);
    saleRepository.save(sale);
}
```

### 5.3. Validate Voucher

```java
public VoucherValidationResult validateVoucher(String code, Long userId, Double orderValue) {
    Voucher voucher = findByCode(code);

    // Check 1: Voucher còn active không
    if (!voucher.getIsActive() || voucher.getStatus() != PromotionStatus.ACTIVE) {
        return invalid("Voucher không hoạt động");
    }

    // Check 2: Còn trong thời gian không
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
        return invalid("Voucher đã hết hạn");
    }

    // Check 3: Còn số lượng không
    if (voucher.getUsedCount() >= voucher.getTotalQuantity()) {
        return invalid("Voucher đã hết số lượng");
    }

    // Check 4: User có được phép dùng không
    if (!isUserEligible(voucher, userId)) {
        return invalid("Bạn không được phép sử dụng voucher này");
    }

    // Check 5: User đã dùng quá giới hạn chưa
    int userUsageCount = voucherUsageRepository.countByVoucherIdAndUserId(voucher.getId(), userId);
    if (userUsageCount >= voucher.getUsageLimitPerUser()) {
        return invalid("Bạn đã sử dụng hết số lần cho phép");
    }

    // Check 6: Đơn hàng đủ điều kiện không
    if (orderValue < voucher.getMinOrderValue()) {
        return invalid("Đơn hàng chưa đủ giá trị tối thiểu");
    }

    // Calculate discount
    Double discount = calculateVoucherDiscount(voucher, orderValue);

    return valid(discount);
}
```

---

## 6. INTEGRATION VỚI CÁC SERVICE KHÁC

### 6.1. Product Service
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/v1/skus/{id}")
    SKUResponse getSKU(@PathVariable Long id);

    @PutMapping("/api/v1/skus/{id}/price")
    void updateSKUPrice(@PathVariable Long id, @RequestParam Double price);

    @PutMapping("/api/v1/skus/{id}/sale-price")
    void updateSKUSalePrice(@PathVariable Long id, @RequestBody SalePriceRequest request);
}
```

### 6.2. Order Service
```java
// Order Service sẽ gọi Sale Service để validate và apply voucher
@FeignClient(name = "sale-service")
public interface VoucherClient {
    @PostMapping("/api/v1/vouchers/validate")
    VoucherValidationResponse validateVoucher(@RequestBody VoucherValidationRequest request);

    @PostMapping("/api/v1/vouchers/apply")
    VoucherApplyResponse applyVoucher(@RequestBody VoucherApplyRequest request);
}
```

---

## 7. LƯU Ý QUAN TRỌNG

### 7.1. Đồng bộ giá với Product Service
- Sale Service không lưu giá trực tiếp
- Chỉ lưu snapshot khi apply sale
- Gọi Product Service để update giá thực tế

### 7.2. Transaction & Rollback
- Dùng distributed transaction hoặc saga pattern
- Nếu update giá Product Service fail → rollback Sale Service

### 7.3. Priority khi nhiều Sale
- Nếu 1 sản phẩm có nhiều sale → chọn sale có priority cao nhất
- Hoặc sale có discount lớn nhất

### 7.4. Caching
- Cache danh sách active sales/vouchers
- Invalidate cache khi có thay đổi

### 7.5. Event-Driven
- Publish events khi sale start/end
- Product Service subscribe để update giá

---

## 8. MIGRATION PLAN

```sql
-- Flyway Migration V2.0__create_sale_voucher_tables.sql
-- (Sẽ tạo trong file riêng)
```
