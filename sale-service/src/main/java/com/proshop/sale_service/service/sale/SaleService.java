package com.proshop.sale_service.service.sale;

import com.proshop.sale_service.dto.request.AddProductsToSaleRequest;
import com.proshop.sale_service.dto.request.SaleCreateRequest;
import com.proshop.sale_service.dto.response.SaleProductResponse;
import com.proshop.sale_service.dto.response.SaleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service quản lý Sale (Chương trình giảm giá sản phẩm)
 */
public interface SaleService {

    /**
     * Tạo sale mới
     */
    SaleResponse createSale(SaleCreateRequest request, MultipartFile bannerImage, MultipartFile thumbnailImage);

    /**
     * Lấy sale theo ID
     */
    SaleResponse getSaleById(Long id);

    /**
     * Lấy sale theo code
     */
    SaleResponse getSaleByCode(String code);

    /**
     * Lấy danh sách tất cả sales
     */
    List<SaleResponse> getAllSales();

    /**
     * Lấy danh sách sales đang active
     */
    List<SaleResponse> getActiveSales();

    /**
     * Cập nhật sale
     */
    SaleResponse updateSale(Long id, SaleCreateRequest request, MultipartFile bannerImage, MultipartFile thumbnailImage);

    /**
     * Xóa mềm sale
     */
    void deleteSale(Long id);

    /**
     * Thêm sản phẩm vào sale
     */
    void addProductsToSale(Long saleId, AddProductsToSaleRequest request);

    /**
     * Xóa sản phẩm khỏi sale
     */
    void removeProductFromSale(Long saleId, Long productId);

    /**
     * Xóa SKU khỏi sale
     */
    void removeSKUFromSale(Long saleId, UUID skuId);

    /**
     * Lấy danh sách sản phẩm trong sale
     */
    List<SaleProductResponse> getSaleProducts(Long saleId);

    /**
     * Kích hoạt sale (manual)
     */
    SaleResponse activateSale(Long saleId);

    /**
     * Tạm dừng sale
     */
    SaleResponse pauseSale(Long saleId);

    /**
     * Áp dụng sale vào sản phẩm (update giá SKU)
     */
    void applySaleToProducts(Long saleId);

    /**
     * Hoàn giá gốc (revert sale)
     */
    void revertSalePrices(Long saleId);
}
