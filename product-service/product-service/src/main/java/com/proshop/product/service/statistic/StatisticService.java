package com.proshop.product.service.statistic;

import com.proshop.product.dto.response.InventorySummaryResponse;
import com.proshop.product.dto.response.ProductBestSellerResponse;
import com.proshop.product.dto.response.ProductCountResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.dto.response.SKUResponse;
import java.util.List;

public interface StatisticService {
  List<ProductBestSellerResponse> getBestSellingProducts(int limit);
  List<ProductResponse> getLatestProducts(int limit);

  // ============================================
  // NEW STATISTICS METHODS
  // ============================================

  /**
   * Get product count (total and active)
   */
  ProductCountResponse getProductCount();

  /**
   * Get inventory summary (total, low stock, out of stock)
   */
  InventorySummaryResponse getInventorySummary();

  /**
   * Get low stock SKUs
   */
  List<SKUResponse> getLowStockSKUs(Integer threshold);
}
