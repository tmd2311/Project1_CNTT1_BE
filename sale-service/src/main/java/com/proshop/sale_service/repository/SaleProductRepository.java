package com.proshop.sale_service.repository;

import com.proshop.sale_service.entity.SaleProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleProductRepository extends JpaRepository<SaleProductEntity, Long> {

    // Tìm tất cả sản phẩm trong 1 sale
    List<SaleProductEntity> findBySaleId(Long saleId);

    // Tìm sản phẩm đã apply sale
    List<SaleProductEntity> findBySaleIdAndIsAppliedTrue(Long saleId);

    // Tìm sản phẩm chưa apply sale
    List<SaleProductEntity> findBySaleIdAndIsAppliedFalse(Long saleId);

    // Tìm sale đang apply cho SKU cụ thể
    @Query("SELECT sp FROM SaleProductEntity sp WHERE sp.skuId = :skuId AND sp.isApplied = true")
    List<SaleProductEntity> findAppliedSalesBySkuId(@Param("skuId") Long skuId);

    // Tìm sale đang apply cho Product cụ thể
    @Query("SELECT sp FROM SaleProductEntity sp WHERE sp.productId = :productId AND sp.isApplied = true")
    List<SaleProductEntity> findAppliedSalesByProductId(@Param("productId") Long productId);

    // Check xem SKU đã có sale chưa
    boolean existsBySkuIdAndIsAppliedTrue(Long skuId);

    // Xóa sản phẩm khỏi sale
    void deleteBySaleIdAndProductId(Long saleId, Long productId);
    void deleteBySaleIdAndSkuId(Long saleId, Long skuId);
}
