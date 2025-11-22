package com.proshop.sale_service.repository;

import com.proshop.sale_service.entity.VoucherEntity;
import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.VoucherType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    // Tìm voucher theo code
    Optional<VoucherEntity> findByCode(String code);

    // Tìm voucher theo code và chưa bị xóa
    Optional<VoucherEntity> findByCodeAndIsDeleteFalse(String code);

    // Tìm tất cả voucher chưa bị xóa
    List<VoucherEntity> findAllByIsDeleteFalse();

    // Tìm tất cả voucher chưa bị xóa với phân trang
    Page<VoucherEntity> findAllByIsDeleteFalse(Pageable pageable);

    // Tìm tất cả voucher đang active
    List<VoucherEntity> findAllByIsActiveTrueAndIsDeleteFalse();

    // Tìm voucher theo type
    List<VoucherEntity> findAllByVoucherTypeAndIsDeleteFalse(VoucherType voucherType);

    // Tìm voucher đang hoạt động trong khoảng thời gian
    @Query("SELECT v FROM VoucherEntity v WHERE v.isActive = true AND v.isDelete = false " +
            "AND v.startDate <= :currentDate AND v.endDate >= :currentDate " +
            "AND v.totalQuantity > v.usedCount")
    List<VoucherEntity> findActiveVouchers(@Param("currentDate") LocalDateTime currentDate);

    // Tìm voucher theo code đang còn hiệu lực
    @Query("SELECT v FROM VoucherEntity v WHERE v.code = :code AND v.isActive = true AND v.isDelete = false " +
            "AND v.startDate <= :currentDate AND v.endDate >= :currentDate " +
            "AND v.totalQuantity > v.usedCount")
    Optional<VoucherEntity> findValidVoucherByCode(@Param("code") String code, @Param("currentDate") LocalDateTime currentDate);

    // Kiểm tra code đã tồn tại chưa
    boolean existsByCodeAndIsDeleteFalse(String code);

    // ========== FOR SCHEDULER ==========

    // Tìm voucher scheduled cần activate
    List<VoucherEntity> findByStatusAndStartDateLessThanEqual(PromotionStatus status, LocalDateTime date);

    // Tìm voucher active cần expire
    List<VoucherEntity> findByStatusAndEndDateLessThan(PromotionStatus status, LocalDateTime date);

    // Tìm voucher đã hết số lượng
    @Query("SELECT v FROM VoucherEntity v WHERE v.isActive = true AND v.status = 'ACTIVE' " +
            "AND v.usedCount >= v.totalQuantity")
    List<VoucherEntity> findFullyUsedVouchers();

    // Tìm voucher đã hết hạn quá lâu
    @Query("SELECT v FROM VoucherEntity v WHERE v.status = 'EXPIRED' AND v.endDate < :cutoffDate")
    List<VoucherEntity> findExpiredBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
