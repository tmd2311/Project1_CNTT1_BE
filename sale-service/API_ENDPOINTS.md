# SALE SERVICE - API ENDPOINTS SPECIFICATION

## 1. SALE APIs (Quản lý chương trình giảm giá sản phẩm)

### 1.1. Quản lý Sale

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/sales` | Tạo chương trình sale mới | Admin |
| GET | `/api/v1/sales` | Danh sách sales (paginated) | Public |
| GET | `/api/v1/sales/{id}` | Chi tiết sale | Public |
| GET | `/api/v1/sales/code/{code}` | Lấy sale theo code | Public |
| PUT | `/api/v1/sales/{id}` | Cập nhật sale | Admin |
| DELETE | `/api/v1/sales/{id}` | Xóa mềm sale | Admin |
| GET | `/api/v1/sales/active` | Danh sách sale đang active | Public |

### 1.2. Quản lý sản phẩm trong Sale

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/sales/{id}/products` | Thêm sản phẩm vào sale | Admin |
| GET | `/api/v1/sales/{id}/products` | Danh sách sản phẩm trong sale | Public |
| DELETE | `/api/v1/sales/{id}/products/{productId}` | Xóa sản phẩm khỏi sale | Admin |
| DELETE | `/api/v1/sales/{id}/skus/{skuId}` | Xóa SKU khỏi sale | Admin |

### 1.3. Điều khiển Sale

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/sales/{id}/activate` | Kích hoạt sale (manual) | Admin |
| POST | `/api/v1/sales/{id}/pause` | Tạm dừng sale | Admin |
| POST | `/api/v1/sales/{id}/apply` | Áp dụng sale (update giá SKU) | Admin/Auto |
| POST | `/api/v1/sales/{id}/revert` | Hoàn giá gốc | Admin/Auto |

---

## 2. VOUCHER APIs (Quản lý mã giảm giá đơn hàng)

### 2.1. Quản lý Voucher

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/vouchers` | Tạo voucher mới | Admin |
| GET | `/api/v1/vouchers` | Danh sách vouchers (paginated) | Admin |
| GET | `/api/v1/vouchers/{id}` | Chi tiết voucher | Admin |
| GET | `/api/v1/vouchers/code/{code}` | Lấy voucher theo code | Public |
| PUT | `/api/v1/vouchers/{id}` | Cập nhật voucher | Admin |
| DELETE | `/api/v1/vouchers/{id}` | Xóa mềm voucher | Admin |
| GET | `/api/v1/vouchers/active` | Danh sách voucher đang active | Public |

### 2.2. Gán Voucher cho User

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/vouchers/{id}/users` | Gán voucher cho user(s) | Admin |
| DELETE | `/api/v1/vouchers/{id}/users/{userId}` | Xóa user khỏi voucher | Admin |
| GET | `/api/v1/vouchers/{id}/users` | Danh sách user của voucher | Admin |

### 2.3. Sử dụng Voucher (cho Order Service)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/vouchers/validate` | Validate voucher cho order | User/Service |
| POST | `/api/v1/vouchers/apply` | Áp dụng voucher vào order | User/Service |
| POST | `/api/v1/vouchers/cancel` | Hủy voucher đã apply | User/Service |
| GET | `/api/v1/vouchers/user/{userId}` | Vouchers của user | User |
| GET | `/api/v1/vouchers/user/{userId}/available` | Vouchers khả dụng | User |

### 2.4. Voucher Usage History

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/vouchers/{id}/usage` | Lịch sử sử dụng voucher | Admin |
| GET | `/api/v1/vouchers/usage/user/{userId}` | Lịch sử dùng voucher của user | User |

---

## 3. REQUEST/RESPONSE EXAMPLES

### 3.1. Create Sale Request

```json
POST /api/v1/sales
{
  "code": "FLASH_SALE_2025",
  "name": "Flash Sale Tết 2025",
  "description": "Giảm giá sốc dịp Tết Nguyên Đán",
  "saleType": "PERCENTAGE",
  "saleValue": 30.0,
  "applyScope": "SPECIFIC_PRODUCTS",
  "minPurchaseQuantity": 1,
  "maxDiscountAmount": 500000.0,
  "startDate": "2025-01-20T00:00:00",
  "endDate": "2025-01-31T23:59:59",
  "priority": 10,
  "bannerImageUrl": "https://cdn.example.com/banner/flash-sale-tet.jpg",
  "thumbnailImageUrl": "https://cdn.example.com/thumb/flash-sale-tet.jpg"
}
```

### 3.2. Add Products to Sale

```json
POST /api/v1/sales/{saleId}/products
{
  "productIds": [1, 2, 3, 4, 5],
  "skuIds": [10, 11, 12]
}
```

### 3.3. Create Voucher Request

```json
POST /api/v1/vouchers
{
  "code": "NEWYEAR2025",
  "name": "Voucher Năm Mới 2025",
  "description": "Giảm 100K cho đơn hàng từ 500K",
  "voucherType": "ORDER_DISCOUNT",
  "discountType": "FIXED_AMOUNT",
  "discountValue": 100000.0,
  "minOrderValue": 500000.0,
  "maxDiscountAmount": 100000.0,
  "totalQuantity": 1000,
  "usageLimitPerUser": 1,
  "userScope": "ALL_USERS",
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-01-31T23:59:59",
  "bannerImageUrl": "https://cdn.example.com/voucher/newyear.jpg"
}
```

### 3.4. Validate Voucher Request

```json
POST /api/v1/vouchers/validate
{
  "code": "NEWYEAR2025",
  "userId": 123,
  "orderValue": 750000.0
}
```

**Response:**
```json
{
  "data": {
    "isValid": true,
    "voucher": {
      "id": 1,
      "code": "NEWYEAR2025",
      "name": "Voucher Năm Mới 2025",
      "discountType": "FIXED_AMOUNT",
      "discountValue": 100000.0
    },
    "discountAmount": 100000.0,
    "finalOrderValue": 650000.0,
    "message": "Voucher hợp lệ"
  },
  "status": {
    "code": "SUCCESS",
    "message": "Success"
  }
}
```

### 3.5. Apply Voucher to Order

```json
POST /api/v1/vouchers/apply
{
  "code": "NEWYEAR2025",
  "userId": 123,
  "orderId": 456,
  "orderValue": 750000.0
}
```

**Response:**
```json
{
  "data": {
    "voucherUsageId": 789,
    "voucherId": 1,
    "discountAmount": 100000.0,
    "finalOrderValue": 650000.0,
    "appliedAt": "2025-01-15T10:30:00"
  },
  "status": {
    "code": "SUCCESS",
    "message": "Voucher applied successfully"
  }
}
```

---

## 4. ERROR CODES

| Code | Message | Description |
|------|---------|-------------|
| SAL01 | Sale not found | Sale không tồn tại |
| SAL02 | Sale code already exists | Mã sale đã tồn tại |
| SAL03 | Sale invalid date | Ngày kết thúc phải sau ngày bắt đầu |
| SAL04 | Sale already active | Sale đã được kích hoạt |
| SAL05 | Sale invalid or expired | Sale không hợp lệ hoặc đã hết hạn |
| SAL06 | Product already in sale | Sản phẩm đã có trong sale khác |
| VOU01 | Voucher not found | Voucher không tồn tại |
| VOU02 | Voucher code already exists | Mã voucher đã tồn tại |
| VOU03 | Voucher invalid or expired | Voucher không hợp lệ hoặc hết hạn |
| VOU04 | Voucher out of stock | Voucher đã hết số lượng |
| VOU05 | User not eligible | User không đủ điều kiện dùng voucher |
| VOU06 | Usage limit exceeded | User đã dùng hết số lần cho phép |
| VOU07 | Order value too low | Đơn hàng chưa đủ giá trị tối thiểu |
| VOU08 | Voucher already applied | Voucher đã được sử dụng cho order này |

---

## 5. INTEGRATION VỚI CÁC SERVICE KHÁC

### 5.1. Order Service → Sale Service

**Khi tạo order, Order Service cần:**
1. Validate voucher: `POST /api/v1/vouchers/validate`
2. Apply voucher: `POST /api/v1/vouchers/apply`
3. Nếu order bị hủy: `POST /api/v1/vouchers/cancel`

### 5.2. Product Service → Sale Service

**Product Service cần:**
- Nhận thông báo khi sale được apply/revert
- Có API để Sale Service update giá SKU

### 5.3. Sale Service → Product Service

**Sale Service cần gọi:**
- `GET /api/v1/skus/{id}` - Lấy thông tin SKU
- `PUT /api/v1/skus/{id}/sale-price` - Cập nhật giá sale

---

## 6. SCHEDULED JOBS

| Job | Schedule | Description |
|-----|----------|-------------|
| Sale Activation | */5 * * * * | Kích hoạt sale đã đến start_date |
| Sale Expiration | */5 * * * * | Hết hạn sale đã qua end_date |
| Voucher Activation | */10 * * * * | Kích hoạt voucher đã đến start_date |
| Voucher Expiration | */10 * * * * | Hết hạn voucher đã qua end_date |
| Cleanup Old Data | 0 0 2 * * * | Dọn dẹp data cũ (2h sáng mỗi ngày) |
