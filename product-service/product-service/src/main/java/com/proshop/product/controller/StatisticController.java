package com.proshop.product.controller;

import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.ProductBestSellerResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.service.statistic.StatisticService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/statistics")
@RequiredArgsConstructor
public class StatisticController {
  private final StatisticService statisticService;

  @GetMapping("/latest")
  public ResponseEntity<GeneralResponse<List<ProductResponse>>> getLatest(
      @RequestParam(name = "limit", defaultValue = "10") int limit) {
    List<ProductResponse> products = statisticService.getLatestProducts(limit);
    return ResponseEntity.ok(new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, products, null));
  }

  @GetMapping("/best-sellers")
  public ResponseEntity<GeneralResponse<List<ProductBestSellerResponse>>> getBestSellingProducts(
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    List<ProductBestSellerResponse> bestSellers = statisticService.getBestSellingProducts(limit);
    return ResponseEntity.ok(
        new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, bestSellers, null)
    );
  }
}
