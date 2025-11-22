# CÁC METHODS CẦN THÊM VÀO REPOSITORIES

## SaleRepository.java

Thêm các methods sau vào file `SaleRepository.java` để hỗ trợ Scheduler:

```java
import com.proshop.sale_service.util.enums.PromotionStatus;

// ========== FOR SCHEDULER ==========

// Tìm sale scheduled cần activate
List<SaleEntity> findByStatusAndStartDateLessThanEqual(PromotionStatus status, LocalDateTime date);

// Tìm sale active cần expire
List<SaleEntity> findByStatusAndEndDateLessThan(PromotionStatus status, LocalDateTime date);

// Tìm sale đã hết số lượng
@Query("SELECT s FROM SaleEntity s WHERE s.isActive = true AND s.status = 'ACTIVE' " +
        "AND s.usedCount >= s.quantity")
List<SaleEntity> findFullyUsedSales();

// Tìm sale đã hết hạn quá lâu (để cleanup)
@Query("SELECT s FROM SaleEntity s WHERE s.status = 'EXPIRED' AND s.endDate < :cutoffDate")
List<SaleEntity> findExpiredBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
```

**LƯU Ý**: Nếu bạn đang dùng `SaleEntity` cũ (chưa có field `status`), bạn cần:
1. Migrate sang `SaleEntityV2` hoặc
2. Thêm field `status` vào `SaleEntity` hiện tại
