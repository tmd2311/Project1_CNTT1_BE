# 🔧 FIX BUILD ERRORS - QUICK GUIDE

## ❌ LỖI
```
cannot find symbol: method findByStatusAndStartDateLessThanEqual(...)
cannot find symbol: method setStatus(...)
```

## ✅ GIẢI PHÁP (3 BƯỚC)

### BƯỚC 1: Chạy Migration SQL (30 giây)

**Option A: Dùng script tự động**
```bash
cd sale-service
./run-migration.sh
```

**Option B: Chạy SQL thủ công**
```bash
psql -U postgres -d proshop_sale

# Paste SQL này:
ALTER TABLE sales ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'SCHEDULED';

UPDATE sales SET status = CASE
    WHEN end_date < NOW() THEN 'EXPIRED'
    WHEN start_date > NOW() THEN 'SCHEDULED'
    ELSE 'ACTIVE'
END;
```

### BƯỚC 2: Rebuild Project (1 phút)
```bash
cd sale-service
mvn clean install
```

### BƯỚC 3: Start & Test (30 giây)
```bash
mvn spring-boot:run
```

Test:
```bash
curl http://localhost:8087/api/v1/sales-v2
```

---

## 🎯 ĐÃ FIX GÌ?

### 1. SaleEntity.java ✅
- Thêm field `status` (PromotionStatus enum)
- Auto set status dựa trên dates

### 2. SaleRepository.java ✅
- Thêm `findByStatusAndStartDateLessThanEqual()`
- Thêm `findByStatusAndEndDateLessThan()`
- Thêm `findFullyUsedSales()`
- Thêm `findExpiredBefore()`

### 3. Database Migration ✅
- Thêm column `status` vào table `sales`
- Update status cho sales hiện có

---

## ✨ KẾT QUẢ

### Trước:
- ❌ 4 build errors
- ❌ Scheduler không compile

### Sau:
- ✅ Build successful
- ✅ Scheduler hoạt động
- ✅ Auto activate/expire sales

---

## 📞 NẾU VẪN LỖI

### Lỗi: "Column status does not exist"
→ Chưa chạy migration SQL (Bước 1)

### Lỗi: "Cannot find symbol PromotionStatus"
→ Rebuild project: `mvn clean install`

### Lỗi: "Table sales does not exist"
→ Chạy script tạo table `sales` trước
→ Xem: `QUICK_START.md`

---

**Chi tiết**: Xem file `FIX_SUMMARY.md`
