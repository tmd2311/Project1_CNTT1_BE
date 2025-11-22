# 🎯 SALE SERVICE - THIẾT KẾ CHUYÊN NGHIỆP

> Hệ thống quản lý Sale (giảm giá sản phẩm) và Voucher (mã giảm giá đơn hàng) cho nền tảng thương mại điện tử

---

## 📚 TÀI LIỆU THIẾT KẾ

### 🎨 Thiết kế cơ bản
- **[DESIGN_PROPOSAL.md](./DESIGN_PROPOSAL.md)** - Thiết kế database schema, business logic chi tiết
- **[API_ENDPOINTS.md](./API_ENDPOINTS.md)** - Tài liệu API endpoints đầy đủ
- **[IMPLEMENTATION_GUIDE.md](./IMPLEMENTATION_GUIDE.md)** - Hướng dẫn triển khai từng bước

### 🛠️ Tài liệu kỹ thuật
- **[REPOSITORY_ADDITIONS.md](./REPOSITORY_ADDITIONS.md)** - Methods cần thêm vào Repositories
- **[ERROR_CODES_TO_ADD.md](./ERROR_CODES_TO_ADD.md)** - Error codes cần thêm vào exception-lib

---

## 🏗️ KIẾN TRÚC TỔNG QUAN

```
┌─────────────────────────────────────────────────────────────┐
│                     SALE SERVICE                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │   SALE/PROMO     │         │     VOUCHER      │        │
│  │  (Giảm giá SKU)  │         │ (Giảm giá Order) │        │
│  └──────────────────┘         └──────────────────┘        │
│           │                            │                    │
│           ├─ Flash Sale                ├─ Order Discount    │
│           ├─ Category Sale             ├─ Shipping Voucher  │
│           └─ Brand Sale                └─ Gift Voucher      │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                    BATCH SCHEDULER                          │
│  • Auto activate sales/vouchers (every 5 mins)             │
│  • Auto expire sales/vouchers (every 5 mins)               │
│  • Cleanup old data (daily at 2 AM)                        │
├─────────────────────────────────────────────────────────────┤
│                   INTEGRATIONS                              │
│  Product Service ←→ Sale Service ←→ Order Service          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 DATABASE SCHEMA

### Core Tables
1. **sales** - Chương trình giảm giá sản phẩm
2. **sale_products** - Sản phẩm tham gia sale (many-to-many)
3. **vouchers** - Mã giảm giá đơn hàng
4. **voucher_usage** - Lịch sử sử dụng voucher
5. **voucher_users** - Gán voucher cho user cụ thể

Xem chi tiết trong [DESIGN_PROPOSAL.md](./DESIGN_PROPOSAL.md)

---

## ✨ TÍNH NĂNG CHÍNH

### 🎁 Sale Management
- ✅ Tạo/Cập nhật/Xóa chương trình sale
- ✅ Áp dụng sale theo: Product, SKU, Category, Brand
- ✅ Hỗ trợ 3 loại sale: Percentage, Fixed Amount, Buy X Get Y
- ✅ Priority system (nhiều sale cùng lúc)
- ✅ Tự động apply/revert giá SKU theo lịch
- ✅ Upload banner & thumbnail images

### 🎫 Voucher Management
- ✅ Tạo/Cập nhật/Xóa voucher
- ✅ 3 loại voucher: Order Discount, Shipping Discount, Gift
- ✅ Giới hạn số lượng & số lần dùng/user
- ✅ Phạm vi user: All, Specific, New, VIP
- ✅ Validate voucher realtime
- ✅ Tracking lịch sử sử dụng

### ⚙️ Automation
- ✅ Auto activate khi đến start_date
- ✅ Auto expire khi đến end_date
- ✅ Auto deactivate khi hết quota
- ✅ Auto cleanup old data

---

## 📁 CODE STRUCTURE

```
sale-service/
├── entity/
│   ├── SaleEntityV2.java              ✨ NEW
│   ├── SaleProductEntity.java         ✨ NEW
│   ├── VoucherEntity.java             ✨ NEW
│   ├── VoucherUsageEntity.java        ✨ NEW
│   └── VoucherUserEntity.java         ✨ NEW
│
├── util/enums/
│   ├── SaleApplyScope.java            ✨ NEW
│   ├── PromotionStatus.java           ✨ NEW
│   ├── VoucherType.java               ✨ NEW
│   ├── VoucherUserScope.java          ✨ NEW
│   ├── DiscountType.java              ✨ NEW
│   └── VoucherUsageStatus.java        ✨ NEW
│
├── repository/
│   ├── SaleProductRepository.java     ✨ NEW
│   ├── VoucherRepository.java         ✨ NEW
│   ├── VoucherUsageRepository.java    ✨ NEW
│   └── VoucherUserRepository.java     ✨ NEW
│
├── scheduler/
│   ├── SaleScheduler.java             ✨ NEW - Tự động quản lý sale
│   └── VoucherScheduler.java          ✨ NEW - Tự động quản lý voucher
│
├── service/voucher/
│   ├── VoucherService.java            ✨ NEW
│   └── impl/VoucherServiceImpl.java   ✨ NEW - Full implementation
│
└── dto/
    ├── request/
    │   ├── VoucherCreateRequest.java  ✨ NEW
    │   ├── VoucherValidateRequest.java ✨ NEW
    │   └── VoucherApplyRequest.java   ✨ NEW
    └── response/
        ├── VoucherResponse.java           ✨ NEW
        ├── VoucherValidationResponse.java ✨ NEW
        └── VoucherApplyResponse.java      ✨ NEW
```

---

## 🚀 QUICK START

### 1. Thêm Error Codes
Xem [ERROR_CODES_TO_ADD.md](./ERROR_CODES_TO_ADD.md)

### 2. Run Database Migration
```sql
-- Xem IMPLEMENTATION_GUIDE.md để lấy script SQL
-- Hoặc dùng Flyway migration
```

### 3. Enable Scheduling
```java
// SchedulerConfig.java đã được tạo với @EnableScheduling
```

### 4. Start Service
```bash
cd sale-service
mvn spring-boot:run
```

---

## 📖 API EXAMPLES

### Tạo Voucher
```bash
POST http://localhost:8087/api/v1/vouchers
Content-Type: application/json

{
  "code": "WELCOME100",
  "name": "Voucher chào mừng",
  "voucherType": "ORDER_DISCOUNT",
  "discountType": "FIXED_AMOUNT",
  "discountValue": 100000,
  "minOrderValue": 500000,
  "totalQuantity": 1000,
  "usageLimitPerUser": 1,
  "userScope": "NEW_USERS",
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-12-31T23:59:59"
}
```

### Validate Voucher
```bash
POST http://localhost:8087/api/v1/vouchers/validate
Content-Type: application/json

{
  "code": "WELCOME100",
  "userId": 123,
  "orderValue": 750000
}
```

Xem thêm trong [API_ENDPOINTS.md](./API_ENDPOINTS.md)

---

## 🔄 FLOW HOẠT ĐỘNG

### Sale Flow
```
1. Admin tạo sale → Status: SCHEDULED
2. Scheduler kiểm tra (5 phút/lần)
3. Đến start_date → Status: ACTIVE → Apply giá SKU
4. Đến end_date → Status: EXPIRED → Revert giá gốc
```

### Voucher Flow
```
1. User chọn voucher
2. Order Service → POST /vouchers/validate
3. Nếu valid → Order Service → POST /vouchers/apply
4. Sale Service:
   - Tăng used_count
   - Tạo voucher_usage record
   - Trả về discount amount
5. Order tính final price = order_value - discount
```

---

## 🎓 BUSINESS LOGIC QUAN TRỌNG

### Voucher Validation (VoucherServiceImpl.java:122-185)
```java
// 8 bước validation:
1. Tìm voucher theo code
2. Check voucher active
3. Check thời gian (start_date, end_date)
4. Check số lượng còn lại
5. Check user eligibility (scope)
6. Check user usage limit
7. Check min order value
8. Calculate discount amount
```

### Discount Calculation (VoucherServiceImpl.java:390-414)
```java
// Percentage: orderValue * percentage / 100 (có max cap)
// Fixed: Số tiền cố định
// Không vượt quá order value
```

---

## ⚠️ ĐIỂM CẦN LƯU Ý

### 1. Concurrency
Voucher có thể bị **race condition** khi nhiều user dùng cùng lúc.
- **Giải pháp ngắn hạn**: Database transaction isolation
- **Giải pháp dài hạn**: Redis distributed lock

### 2. Integration với Product Service
Sale Service cần gọi Product Service để:
- Lấy giá gốc SKU
- Update giá sale
- Revert giá gốc

→ Cần implement `ProductClient` (Feign)

### 3. Scheduler Timing
- Sale activation/expiration: **Mỗi 5 phút**
- Voucher activation/expiration: **Mỗi 10 phút**
- Cleanup: **2h sáng mỗi ngày**

Có thể điều chỉnh trong `@Scheduled(cron = "...")`

### 4. Image Storage
- Banner & thumbnail URLs được lưu trong database
- File upload thực tế cần integrate với File Service

---

## 📈 ROADMAP

### ✅ Phase 1 - MVP (Completed)
- Database schema design
- Core entities & enums
- Repositories với full query methods
- Schedulers với auto activation/expiration
- Voucher Service với validation logic
- API documentation

### 🚧 Phase 2 - Cần hoàn thiện
- [ ] Sale Service implementation (tương tự VoucherService)
- [ ] Controllers (SaleController, VoucherController)
- [ ] Integration với Product Service
- [ ] Unit tests & Integration tests
- [ ] DataLoader để tạo sample data

### 🎯 Phase 3 - Enhancement
- [ ] Event-driven architecture (Kafka)
- [ ] Redis caching cho performance
- [ ] Analytics & Reporting
- [ ] Admin Dashboard UI

---

## 🤝 INTEGRATION GUIDE

### Cho Order Service
```java
// 1. Validate voucher trước khi checkout
POST /api/v1/vouchers/validate

// 2. Apply voucher khi tạo order
POST /api/v1/vouchers/apply

// 3. Cancel voucher nếu order bị hủy
POST /api/v1/vouchers/cancel
```

### Cho Product Service
```java
// Nhận request từ Sale Service để update giá
PUT /api/v1/skus/{id}/sale-price
PUT /api/v1/skus/{id}/revert-price
```

---

## 📞 SUPPORT

### Tài liệu tham khảo
- [Spring Scheduling](https://spring.io/guides/gs/scheduling-tasks/)
- [Flyway Migration](https://flywaydb.org/documentation/)
- [Spring Transaction](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)

### Câu hỏi thường gặp
**Q: Làm sao để thay đổi thời gian chạy scheduler?**
A: Sửa `@Scheduled(cron = "...")` trong `SaleScheduler.java` và `VoucherScheduler.java`

**Q: Voucher bị race condition khi nhiều user dùng cùng lúc?**
A: Hiện tại dùng DB transaction. Để production, cần implement distributed lock với Redis.

**Q: Làm sao để test scheduler?**
A: Đổi cron expression thành `fixedDelay = 10000` (10s) để test.

---

## 📝 CHANGELOG

### v2.0 - 2025-01-22
- ✨ Thiết kế hoàn chỉnh Sale Service với Voucher support
- ✨ Full entities, enums, repositories
- ✨ Batch schedulers cho auto update
- ✨ VoucherService implementation với validation logic
- ✨ Comprehensive documentation

### v1.0 - 2024-XX-XX
- 🎉 Initial Sale Service với basic functionality

---

**Designed by**: AI Architecture Consultant
**Date**: 2025-01-22
**Status**: Ready for Implementation ✅
