# 🚀 QUICK START - SALE SERVICE

## Bắt đầu trong 5 phút!

### BƯỚC 1: Thêm Error Codes (2 phút)

Mở file `exception-lib/src/main/java/com/proshop/exceptionlib/enums/ResErrorCode.java`

Thêm vào cuối enum (trước dấu `;` cuối cùng):

```java
// VOUCHER ERRORS
VOUCHER_NOT_FOUND("VOU01", "Voucher not found", "Voucher không tồn tại"),
VOUCHER_CODE_ALREADY_EXISTS("VOU02", "Voucher code already exists", "Mã voucher đã tồn tại"),
VOUCHER_INVALID_OR_EXPIRED("VOU03", "Voucher invalid or expired", "Voucher không hợp lệ hoặc đã hết hạn"),
VOUCHER_OUT_OF_STOCK("VOU04", "Voucher out of stock", "Voucher đã hết số lượng"),
VOUCHER_USER_NOT_ELIGIBLE("VOU05", "User not eligible", "User không đủ điều kiện"),
VOUCHER_USAGE_LIMIT_EXCEEDED("VOU06", "Usage limit exceeded", "Đã sử dụng hết số lần"),
VOUCHER_ORDER_VALUE_TOO_LOW("VOU07", "Order value too low", "Đơn hàng chưa đủ giá trị"),
VOUCHER_ALREADY_APPLIED("VOU08", "Voucher already applied", "Đơn hàng đã sử dụng voucher"),
VOUCHER_INVALID_DATE("VOU09", "Invalid date range", "Thời gian không hợp lệ"),
VOUCHER_INVALID_OPERATION("VOU10", "Invalid operation", "Thao tác không hợp lệ"),

// SALE ERRORS (nếu chưa có)
SALE_ALREADY_DELETED("SAL07", "Sale already deleted", "Sale đã bị xóa"),
SALE_DELETED_STATUS_CHANGE("SAL08", "Cannot change deleted sale", "Không thể thay đổi sale đã xóa"),
```

### BƯỚC 2: Database Migration (1 phút)

Tạo file `flyway-migration-service/src/main/resources/db/migration/sale-service/V1.0__create_voucher_tables.sql`:

```sql
-- Create vouchers table
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

-- Create voucher_usage table
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

-- Create voucher_users table
CREATE TABLE IF NOT EXISTS voucher_users (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(voucher_id, user_id)
);

-- Create sale_products table
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
CREATE INDEX idx_sale_products_sku_id ON sale_products(sku_id);
```

### BƯỚC 3: Configuration (30 giây)

Cập nhật `sale-service/src/main/resources/application.yml`:

```yaml
product:
  service:
    url: ${PRODUCT_SERVICE_URL:http://localhost:8082}
```

### BƯỚC 4: Start Service (30 giây)

```bash
cd sale-service
mvn clean install
mvn spring-boot:run
```

### BƯỚC 5: Test thử! (1 phút)

#### Test 1: Tạo Voucher
```bash
curl -X POST http://localhost:8087/api/v1/vouchers \
-H "Content-Type: application/json" \
-d '{
  "code": "TEST100",
  "name": "Test Voucher",
  "voucherType": "ORDER_DISCOUNT",
  "discountType": "FIXED_AMOUNT",
  "discountValue": 100000,
  "minOrderValue": 500000,
  "totalQuantity": 100,
  "usageLimitPerUser": 1,
  "userScope": "ALL_USERS",
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-12-31T23:59:59"
}'
```

#### Test 2: Validate Voucher
```bash
curl -X POST http://localhost:8087/api/v1/vouchers/validate \
-H "Content-Type: application/json" \
-d '{
  "code": "TEST100",
  "userId": 1,
  "orderValue": 750000
}'
```

#### Test 3: Tạo Sale
```bash
curl -X POST http://localhost:8087/api/v1/sales-v2 \
-H "Content-Type: application/json" \
-d '{
  "code": "FLASH50",
  "name": "Flash Sale 50%",
  "saleType": "PERCENTAGE",
  "saleValue": 50,
  "applyScope": "SPECIFIC_PRODUCTS",
  "startDate": "2025-01-25T00:00:00",
  "endDate": "2025-01-31T23:59:59"
}'
```

---

## 🎯 API ENDPOINTS CHEAT SHEET

### Voucher APIs
- `POST /api/v1/vouchers` - Tạo voucher
- `GET /api/v1/vouchers/code/{code}` - Lấy theo code
- `POST /api/v1/vouchers/validate` - Validate
- `POST /api/v1/vouchers/apply` - Apply vào order
- `POST /api/v1/vouchers/cancel/{orderId}` - Cancel
- `GET /api/v1/vouchers/user/{userId}/available` - Vouchers khả dụng

### Sale APIs
- `POST /api/v1/sales-v2` - Tạo sale
- `POST /api/v1/sales-v2/{id}/products` - Thêm SKUs
- `POST /api/v1/sales-v2/{id}/activate` - Kích hoạt
- `POST /api/v1/sales-v2/{id}/apply` - Apply giá
- `POST /api/v1/sales-v2/{id}/revert` - Revert giá
- `GET /api/v1/sales-v2/active` - Sales đang active

---

## 📚 TÀI LIỆU CHI TIẾT

- **[README_DESIGN.md](./README_DESIGN.md)** - Tổng quan kiến trúc
- **[COMPLETION_SUMMARY.md](./COMPLETION_SUMMARY.md)** - Tổng kết implementation
- **[API_ENDPOINTS.md](./API_ENDPOINTS.md)** - API docs đầy đủ
- **[IMPLEMENTATION_GUIDE.md](./IMPLEMENTATION_GUIDE.md)** - Hướng dẫn chi tiết

---

## ⚠️ TROUBLESHOOTING

### Lỗi: "ResErrorCode.VOUCHER_NOT_FOUND not found"
→ Chưa thêm error codes vào exception-lib (Bước 1)

### Lỗi: "Table vouchers does not exist"
→ Chưa run database migration (Bước 2)

### Lỗi: "ProductClient bean not found"
→ Kiểm tra `@EnableFeignClients` trong FeignConfig.java

### Lỗi: "Connection refused to Product Service"
→ Cập nhật `product.service.url` trong application.yml

---

## 🎉 XEM KẾT QUẢ

Service chạy tại: http://localhost:8087

Swagger UI (nếu có): http://localhost:8087/swagger-ui.html

Health check: http://localhost:8087/actuator/health

---

**Chúc bạn thành công!** 🚀
