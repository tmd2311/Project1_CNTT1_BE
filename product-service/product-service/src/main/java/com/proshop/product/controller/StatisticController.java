package com.proshop.product.controller;

import com.proshop.auth_lib.utils.JwtUtil;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.product.dto.response.GeneralResponse;
import com.proshop.product.dto.response.InventorySummaryResponse;
import com.proshop.product.dto.response.ProductBestSellerResponse;
import com.proshop.product.dto.response.ProductCountResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.dto.response.ResponseStatus;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.service.statistic.StatisticService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
  private final StatisticService statisticService;
  private final JwtUtil jwtUtil;

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

  // ============================================
  // NEW STATISTICS ENDPOINTS
  // ============================================

  @GetMapping("/count")
  public ResponseEntity<GeneralResponse<ProductCountResponse>> getProductCount(
      HttpServletRequest request) {
    log.info("Getting product count (admin)");
    checkAdminRole(request);
    ProductCountResponse response = statisticService.getProductCount();
    return ResponseEntity.ok(
        new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null)
    );
  }

  @GetMapping("/inventory-summary")
  public ResponseEntity<GeneralResponse<InventorySummaryResponse>> getInventorySummary(
      HttpServletRequest request) {
    log.info("Getting inventory summary (admin)");
    checkAdminRole(request);
    InventorySummaryResponse response = statisticService.getInventorySummary();
    return ResponseEntity.ok(
        new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null)
    );
  }

  @GetMapping("/low-stock")
  public ResponseEntity<GeneralResponse<List<SKUResponse>>> getLowStockSKUs(
      @RequestParam(name = "threshold", defaultValue = "5") Integer threshold,
      HttpServletRequest request) {
    log.info("Getting low stock SKUs with threshold: {} (admin)", threshold);
    checkAdminRole(request);
    List<SKUResponse> response = statisticService.getLowStockSKUs(threshold);
    return ResponseEntity.ok(
        new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, response, null)
    );
  }

  /**
   * Check if user has Admin role from JWT token
   */
  private void checkAdminRole(HttpServletRequest request) {
    List<String> roles = getRoleFromToken(request);

    boolean isAdmin = roles.stream().anyMatch(role -> role.equalsIgnoreCase("Admin"));
    if (!isAdmin) {
      throw new ResException(ResErrorCode.PERMISSION_DENIED,
          "Bạn không có quyền truy cập tài nguyên này (Admin only)");
    }
  }

  /**
   * Extract roles from JWT token
   */
  private List<String> getRoleFromToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.error("Missing or invalid Authorization header");
      throw new ResException(ResErrorCode.UNAUTHORIZED);
    }

    String token = authHeader.substring(7).trim();

    try {
      List<String> roles = jwtUtil.extractRoles(token);
      log.info("Successfully extracted roles from token: {}", roles);
      return roles;
    } catch (Exception e) {
      log.error("Failed to extract roles from token: {}", e.getMessage(), e);
      throw new ResException(ResErrorCode.TOKEN_INVALID);
    }
  }
}
