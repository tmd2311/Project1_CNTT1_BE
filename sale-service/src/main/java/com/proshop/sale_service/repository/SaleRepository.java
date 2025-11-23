package com.proshop.sale_service.repository;

import com.proshop.sale_service.entity.SaleEntity;
import com.proshop.sale_service.util.enums.PromotionStatus;
import com.proshop.sale_service.util.enums.SaleType;
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
public interface SaleRepository extends JpaRepository<SaleEntity, Long> {

    // Tìm sale theo code
    Optional<SaleEntity> findByCode(String code);

    // Tìm sale theo code và chưa bị xóa
    Optional<SaleEntity> findByCodeAndIsDeleteFalse(String code);

    // Tìm tất cả sale chưa bị xóa
    List<SaleEntity> findAllByIsDeleteFalse();

    // Tìm tất cả sale chưa bị xóa với phân trang
    Page<SaleEntity> findAllByIsDeleteFalse(Pageable pageable);

    // Tìm tất cả sale đang active
    List<SaleEntity> findAllByIsActiveTrueAndIsDeleteFalse();

    // Tìm sale theo type
    List<SaleEntity> findAllBySaleTypeAndIsDeleteFalse(SaleType saleType);

    // Tìm sale đang hoạt động trong khoảng thời gian
    @Query("SELECT s FROM SaleEntity s WHERE s.isActive = true AND s.isDelete = false " +
            "AND s.startDate <= :currentDate AND s.endDate >= :currentDate " +
            "AND (s.quantity IS NULL OR s.quantity > s.usedCount)")
    List<SaleEntity> findActiveSales(@Param("currentDate") LocalDateTime currentDate);

    // Tìm sale theo code đang còn hiệu lực
    @Query("SELECT s FROM SaleEntity s WHERE s.code = :code AND s.isActive = true AND s.isDelete = false " +
            "AND s.startDate <= :currentDate AND s.endDate >= :currentDate " +
            "AND (s.quantity IS NULL OR s.quantity > s.usedCount)")
    Optional<SaleEntity> findValidSaleByCode(@Param("code") String code, @Param("currentDate") LocalDateTime currentDate);

    // Kiểm tra code đã tồn tại chưa
    boolean existsByCodeAndIsDeleteFalse(String code);

    // Tìm sale sắp hết hạn
    @Query("SELECT s FROM SaleEntity s WHERE s.isActive = true AND s.isDelete = false " +
            "AND s.endDate BETWEEN :startDate AND :endDate")
    List<SaleEntity> findSalesExpiringSoon(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // ========== FOR SCHEDULER ==========

    /**
     * Tìm sale scheduled cần activate
     */
    List<SaleEntity> findByStatusAndStartDateLessThanEqual(PromotionStatus status, LocalDateTime date);

    /**
     * Tìm sale active cần expire
     */
    List<SaleEntity> findByStatusAndEndDateLessThan(PromotionStatus status, LocalDateTime date);

    /**
     * Tìm sale đã hết số lượng
     */
    @Query("SELECT s FROM SaleEntity s WHERE s.isActive = true AND s.status = 'ACTIVE' " +
            "AND s.quantity IS NOT NULL AND s.usedCount >= s.quantity")
    List<SaleEntity> findFullyUsedSales();

    /**
     * Tìm sale đã hết hạn quá lâu (để cleanup)
     */
    @Query("SELECT s FROM SaleEntity s WHERE s.status = 'EXPIRED' AND s.endDate < :cutoffDate")
    List<SaleEntity> findExpiredBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}