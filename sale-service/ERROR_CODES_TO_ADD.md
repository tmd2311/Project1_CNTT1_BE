# ERROR CODES CẦN THÊM VÀO EXCEPTION-LIB

Thêm các error codes sau vào file `exception-lib/src/main/java/com/proshop/exceptionlib/enums/ResErrorCode.java`:

```java
// ========== VOUCHER ERROR CODES ==========

VOUCHER_NOT_FOUND("VOU01", "Voucher not found", "Voucher không tồn tại"),
VOUCHER_CODE_ALREADY_EXISTS("VOU02", "Voucher code already exists", "Mã voucher đã tồn tại"),
VOUCHER_INVALID_OR_EXPIRED("VOU03", "Voucher invalid or expired", "Voucher không hợp lệ hoặc đã hết hạn"),
VOUCHER_OUT_OF_STOCK("VOU04", "Voucher out of stock", "Voucher đã hết số lượng"),
VOUCHER_USER_NOT_ELIGIBLE("VOU05", "User not eligible for voucher", "User không đủ điều kiện sử dụng voucher"),
VOUCHER_USAGE_LIMIT_EXCEEDED("VOU06", "Voucher usage limit exceeded", "Đã sử dụng hết số lần cho phép"),
VOUCHER_ORDER_VALUE_TOO_LOW("VOU07", "Order value too low", "Đơn hàng chưa đủ giá trị tối thiểu"),
VOUCHER_ALREADY_APPLIED("VOU08", "Voucher already applied to order", "Đơn hàng đã sử dụng voucher"),
VOUCHER_INVALID_DATE("VOU09", "Invalid voucher date range", "Thời gian voucher không hợp lệ"),
VOUCHER_INVALID_OPERATION("VOU10", "Invalid voucher operation", "Thao tác không hợp lệ với voucher này"),

// ========== SALE ERROR CODES (nếu chưa có) ==========

SALE_ALREADY_DELETED("SAL07", "Sale already deleted", "Sale đã bị xóa"),
SALE_DELETED_STATUS_CHANGE("SAL08", "Cannot change status of deleted sale", "Không thể thay đổi trạng thái sale đã xóa"),
```

**Cách thêm:**
1. Mở file `exception-lib/src/main/java/com/proshop/exceptionlib/enums/ResErrorCode.java`
2. Thêm các enum values trên vào cuối file (trước dấu chấm phẩy cuối cùng)
3. Rebuild project
