package com.proshop.sale_service.repository;

import com.proshop.sale_service.entity.VoucherUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherUserRepository extends JpaRepository<VoucherUserEntity, Long> {

    // Tìm voucher của user
    List<VoucherUserEntity> findByUserId(Long userId);

    // Tìm voucher còn active của user
    List<VoucherUserEntity> findByUserIdAndIsActiveTrue(Long userId);

    // Tìm user của voucher
    List<VoucherUserEntity> findByVoucherId(Long voucherId);

    // Check xem user có được assign voucher không
    Optional<VoucherUserEntity> findByVoucherIdAndUserId(Long voucherId, Long userId);

    // Check xem user có voucher active không
    boolean existsByVoucherIdAndUserIdAndIsActiveTrue(Long voucherId, Long userId);

    // Xóa user khỏi voucher
    void deleteByVoucherIdAndUserId(Long voucherId, Long userId);
}
