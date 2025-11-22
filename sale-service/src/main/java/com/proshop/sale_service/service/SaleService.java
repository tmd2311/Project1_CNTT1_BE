package com.proshop.sale_service.service;


import com.proshop.sale_service.dto.request.SaleCreateRequest;
import com.proshop.sale_service.dto.response.SaleResponse;
import com.proshop.sale_service.dto.request.SaleUpdateRequest;
import com.proshop.sale_service.util.enums.SaleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SaleService {

    /**
     * Tạo mới sale
     */
    SaleResponse createSale(SaleCreateRequest createDTO);

    /**
     * Cập nhật sale
     */
    SaleResponse updateSale(Long id, SaleUpdateRequest updateDTO);

    /**
     * Lấy thông tin sale theo id
     */
    SaleResponse getSaleById(Long id);

    /**
     * Lấy thông tin sale theo code
     */
    SaleResponse getSaleByCode(String code);

    /**
     * Lấy danh sách tất cả sale
     */
    List<SaleResponse> getAllSales();

    /**
     * Lấy danh sách sale với phân trang
     */
    Page<SaleResponse> getAllSales(Pageable pageable);

    /**
     * Lấy danh sách sale đang active
     */
    List<SaleResponse> getActiveSales();

    /**
     * Lấy danh sách sale theo type
     */
    List<SaleResponse> getSalesByType(SaleType saleType);

    /**
     * Validate và apply sale cho đơn hàng
     */
    SaleResponse validateAndApplySale(String code);

    /**
     * Xóa mềm sale
     */
    void deleteSale(Long id);

    /**
     * Kích hoạt/Vô hiệu hóa sale
     */
    SaleResponse toggleSaleStatus(Long id);
}