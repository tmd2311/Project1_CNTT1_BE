# HƯỚNG DẪN TRIỂN KHAI SALE SERVICE

## 📋 TỔNG QUAN

Sale Service được thiết kế để quản lý:
1. **Sale/Promotion**: Giảm giá sản phẩm (áp dụng trực tiếp lên SKU)
2. **Voucher/Coupon**: Mã giảm giá đơn hàng (áp dụng khi checkout)

## 🗂️ CẤU TRÚC FILE ĐÃ TẠO

```
sale-service/
├── DESIGN_PROPOSAL.md              # Thiết kế chi tiết database & business logic
├── API_ENDPOINTS.md                # Tài liệu API endpoints
├── REPOSITORY_ADDITIONS.md         # Methods cần thêm vào SaleRepository
├── ERROR_CODES_TO_ADD.md          # Error codes cần thêm vào exception-lib
├── IMPLEMENTATION_GUIDE.md         # File này
│
├── src/main/java/com/proshop/sale_service/
│   ├── entity/
│   │   ├── SaleEntityV2.java                  # ✨ Sale entity nâng cấp
│   │   ├── SaleProductEntity.java             # ✨ Liên kết Sale-Product
│   │   ├── VoucherEntity.java                 # ✨ Voucher entity
│   │   ├── VoucherUsageEntity.java            # ✨ Tracking voucher usage
│   │   └── VoucherUserEntity.java             # ✨ Assign voucher to users
│   │
│   ├── util/enums/
│   │   ├── SaleApplyScope.java                # ✨ Phạm vi áp dụng sale
│   │   ├── PromotionStatus.java               # ✨ Trạng thái sale/voucher
│   │   ├── VoucherType.java                   # ✨ Loại voucher
│   │   ├── VoucherUserScope.java              # ✨ Phạm vi user
│   │   ├── DiscountType.java                  # ✨ Loại giảm giá
│   │   └── VoucherUsageStatus.java            # ✨ Trạng thái usage
│   │
│   ├── repository/
│   │   ├── SaleProductRepository.java         # ✨ Repository sale-product
│   │   ├── VoucherRepository.java             # ✨ Repository voucher
│   │   ├── VoucherUsageRepository.java        # ✨ Repository voucher usage
│   │   └── VoucherUserRepository.java         # ✨ Repository voucher user
│   │
│   ├── scheduler/
│   │   ├── SaleScheduler.java                 # ✨ Batch jobs cho sale
│   │   └── VoucherScheduler.java              # ✨ Batch jobs cho voucher
│   │
│   ├── config/
│   │   └── SchedulerConfig.java               # ✨ Enable scheduling
│   │
│   ├── dto/request/
│   │   ├── VoucherCreateRequest.java          # ✨ DTO tạo voucher
│   │   ├── VoucherValidateRequest.java        # ✨ DTO validate voucher
│   │   └── VoucherApplyRequest.java           # ✨ DTO apply voucher
│   │
│   ├── dto/response/
│   │   ├── VoucherResponse.java               # ✨ DTO voucher response
│   │   ├── VoucherValidationResponse.java     # ✨ DTO validation result
│   │   └── VoucherApplyResponse.java          # ✨ DTO apply result
│   │
│   └── service/voucher/
│       ├── VoucherService.java                # ✨ Voucher service interface
│       └── impl/
│           └── VoucherServiceImpl.java        # ✨ Voucher service implementation
```

---

## 🚀 BƯỚC TRIỂN KHAI

### BƯỚC 1: Thêm Error Codes

Xem file `ERROR_CODES_TO_ADD.md` và thêm vào `exception-lib`.

### BƯỚC 2: Database Migration

Tạo migration script trong `flyway-migration-service`:

```sql
-- File: V2.0__create_sale_voucher_tables.sql

-- 1. Thêm columns mới vào sales table (nếu upgrade từ V1)
ALTER TABLE sales ADD COLUMN IF NOT EXISTS apply_scope VARCHAR(50);
ALTER TABLE sales ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'SCHEDULED';
ALTER TABLE sales ADD COLUMN IF NOT EXISTS priority INT DEFAULT 0;
ALTER TABLE sales ADD COLUMN IF NOT EXISTS banner_image_url VARCHAR(500);
ALTER TABLE sales ADD COLUMN IF NOT EXISTS thumbnail_image_url VARCHAR(500);
ALTER TABLE sales ADD COLUMN IF NOT EXISTS total_applied_count INT DEFAULT 0;
ALTER TABLE sales ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE sales ADD COLUMN IF NOT EXISTS updated_by BIGINT;

-- 2. Create sale_products table
CREATE TABLE IF NOT EXISTS sale_products (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT,
    sku_id BIGINT,
    category_id BIGINT,
    brand_id BIGINT,
    original_price DECIMAL(10,2),
    sale_price DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    is_applied BOOLEAN DEFAULT false,
    applied_at TIMESTAMP,
    reverted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sale_products_sale_id ON sale_products(sale_id);
CREATE INDEX idx_sale_products_product_id ON sale_products(product_id);
CREATE INDEX idx_sale_products_sku_id ON sale_products(sku_id);

-- 3. Create vouchers table
CREATE TABLE IF NOT EXISTS vouchers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    voucher_type VARCHAR(50) NOT NULL,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_value DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2),
    total_quantity INT NOT NULL,
    used_count INT DEFAULT 0,
    usage_limit_per_user INT DEFAULT 1,
    user_scope VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    is_active BOOLEAN DEFAULT true,
    is_delete BOOLEAN DEFAULT false,
    banner_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vouchers_code ON vouchers(code);
CREATE INDEX idx_vouchers_status ON vouchers(status);
CREATE INDEX idx_vouchers_dates ON vouchers(start_date, end_date);

-- 4. Create voucher_usage table
CREATE TABLE IF NOT EXISTS voucher_usage (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,
    order_value DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    status VARCHAR(50) NOT NULL DEFAULT 'USED',
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_voucher_usage_voucher_id ON voucher_usage(voucher_id);
CREATE INDEX idx_voucher_usage_user_id ON voucher_usage(user_id);
CREATE INDEX idx_voucher_usage_order_id ON voucher_usage(order_id);

-- 5. Create voucher_users table
CREATE TABLE IF NOT EXISTS voucher_users (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(voucher_id, user_id)
);

CREATE INDEX idx_voucher_users_voucher_id ON voucher_users(voucher_id);
CREATE INDEX idx_voucher_users_user_id ON voucher_users(user_id);
```

### BƯỚC 3: Cập nhật Dependencies

Đảm bảo `pom.xml` có:
```xml
<!-- Spring Scheduling -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

### BƯỚC 4: Tạo Feign Client cho Product Service

```java
@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/v1/skus/{id}")
    SKUResponse getSKU(@PathVariable Long id);

    @PutMapping("/api/v1/skus/{id}/sale-price")
    void updateSKUSalePrice(
        @PathVariable Long id,
        @RequestParam Double originalPrice,
        @RequestParam Double salePrice,
        @RequestParam Long saleId
    );

    @PutMapping("/api/v1/skus/{id}/revert-price")
    void revertSKUPrice(@PathVariable Long id);
}
```

### BƯỚC 5: Implement Sale Service Logic

TODO: Tạo `SaleService` và `SaleServiceImpl` tương tự `VoucherServiceImpl`.

Các chức năng cần implement:
- `applySaleToProducts()` - Áp dụng giá sale cho SKU
- `revertSalePrices()` - Hoàn giá gốc
- `addProductsToSale()` - Thêm sản phẩm vào sale
- `removeProductFromSale()` - Xóa sản phẩm khỏi sale

### BƯỚC 6: Tạo Controllers

TODO: Tạo:
- `VoucherController` - API cho voucher
- `SaleController` (update existing) - API cho sale

### BƯỚC 7: Testing

1. Test Voucher validation logic
2. Test Sale apply/revert logic
3. Test Schedulers
4. Test integration với Order Service

---

## 🎯 USE CASES CHÍNH

### Use Case 1: Tạo Flash Sale

```java
// 1. Admin tạo sale
POST /api/v1/sales
{
  "code": "FLASH50",
  "saleType": "PERCENTAGE",
  "saleValue": 50,
  "applyScope": "SPECIFIC_PRODUCTS",
  ...
}

// 2. Admin thêm sản phẩm vào sale
POST /api/v1/sales/{id}/products
{
  "skuIds": [1, 2, 3]
}

// 3. Khi đến startDate, Scheduler tự động:
// - Chuyển status -> ACTIVE
// - Gọi Product Service cập nhật giá SKU

// 4. Khi đến endDate, Scheduler tự động:
// - Chuyển status -> EXPIRED
// - Revert giá SKU về giá gốc
```

### Use Case 2: User sử dụng Voucher

```java
// 1. User xem vouchers available
GET /api/v1/vouchers/user/{userId}/available

// 2. User chọn voucher khi checkout
// Order Service validate voucher:
POST /api/v1/vouchers/validate
{
  "code": "NEWYEAR100",
  "userId": 123,
  "orderValue": 500000
}

// 3. Nếu valid, Order Service apply voucher:
POST /api/v1/vouchers/apply
{
  "code": "NEWYEAR100",
  "userId": 123,
  "orderId": 456,
  "orderValue": 500000
}

// 4. Nếu order bị hủy:
POST /api/v1/vouchers/cancel
{
  "orderId": 456
}
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 1. Transaction Management
- Dùng `@Transactional` cho các operations update database
- Xem xét distributed transaction nếu gọi nhiều services

### 2. Concurrency
- Voucher có thể bị race condition (nhiều user dùng cùng lúc)
- Cân nhắc dùng pessimistic locking hoặc Redis distributed lock

### 3. Performance
- Cache danh sách active sales/vouchers
- Invalidate cache khi có update
- Index database đúng cách

### 4. Monitoring
- Log tất cả voucher apply/cancel operations
- Monitor scheduler execution
- Alert khi sale không được apply thành công

---

## 📊 KẾ HOẠCH PHÁT TRIỂN

### Phase 1: MVP (Hiện tại)
- ✅ Database design
- ✅ Entities & Enums
- ✅ Repositories
- ✅ Schedulers
- ✅ Voucher Service implementation
- ⏳ Sale Service implementation
- ⏳ Controllers
- ⏳ Integration tests

### Phase 2: Enhancement
- [ ] Event-driven architecture (Kafka/RabbitMQ)
- [ ] Redis caching
- [ ] Distributed locking
- [ ] Analytics & Reporting
- [ ] Admin Dashboard

### Phase 3: Advanced Features
- [ ] Conditional vouchers (user segment, product category)
- [ ] Stackable vouchers
- [ ] Referral vouchers
- [ ] Auto-apply best voucher
- [ ] Voucher recommendations

---

## 🔗 RESOURCES

- **Design Document**: `DESIGN_PROPOSAL.md`
- **API Documentation**: `API_ENDPOINTS.md`
- **Repository Updates**: `REPOSITORY_ADDITIONS.md`
- **Error Codes**: `ERROR_CODES_TO_ADD.md`

---

## 📞 SUPPORT

Nếu có câu hỏi về implementation, tham khảo:
1. Spring Scheduling: https://spring.io/guides/gs/scheduling-tasks/
2. Spring Transaction Management: https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction
3. Flyway Migration: https://flywaydb.org/documentation/
