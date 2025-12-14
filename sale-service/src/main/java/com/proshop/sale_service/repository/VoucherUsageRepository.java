package com.proshop.sale_service.repository;

import com.proshop.sale_service.entity.VoucherUsageEntity;
import com.proshop.sale_service.util.enums.VoucherUsageStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsageEntity, Long> {

    // Đếm số lần user đã dùng voucher
    int countByVoucherIdAndUserId(Long voucherId, Long userId);

    // Đếm số lần user đã dùng voucher (chỉ tính status = USED)
    int countByVoucherIdAndUserIdAndStatus(Long voucherId, Long userId, VoucherUsageStatus status);

    // Lấy lịch sử sử dụng voucher của user
    List<VoucherUsageEntity> findByUserId(Long userId);

    // Lấy lịch sử sử dụng 1 voucher cụ thể
    List<VoucherUsageEntity> findByVoucherId(Long voucherId);

    // Lấy voucher usage theo order
    List<VoucherUsageEntity> findByOrderId(Long orderId);

    // Check xem order đã dùng voucher chưa
    boolean existsByOrderId(UUID orderId);

    // Đếm tổng số lần voucher đã được sử dụng
    @Query("SELECT COUNT(vu) FROM VoucherUsageEntity vu WHERE vu.voucherId = :voucherId AND vu.status = 'USED'")
    long countUsedByVoucherId(@Param("voucherId") Long voucherId);
}
