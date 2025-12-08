package com.proshop.product.service.statistic;

import com.proshop.product.dto.response.ProductBestSellerResponse;
import com.proshop.product.dto.response.ProductResponse;
import java.util.List;

public interface StatisticService {
  List<ProductBestSellerResponse> getBestSellingProducts(int limit);
  List<ProductResponse> getLatestProducts(int limit);
}
