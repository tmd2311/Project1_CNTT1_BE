package com.proshop.product.service.statistic.impl;


import com.proshop.product.client.OrderClient;
import com.proshop.product.dto.response.BestSellerResponse;
import com.proshop.product.dto.response.InventorySummaryResponse;
import com.proshop.product.dto.response.ProductBestSellerResponse;
import com.proshop.product.dto.response.ProductCountResponse;
import com.proshop.product.dto.response.ProductResponse;
import com.proshop.product.dto.response.SKUResponse;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.ProductImageEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.SKURepository;
import com.proshop.product.service.statistic.StatisticService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

  private final ProductRepository productRepository;
  private final OrderClient orderClient;
  private final SKURepository skuRepository;

  @Override
  public List<ProductBestSellerResponse> getBestSellingProducts(int limit) {
    List<BestSellerResponse> bestSellerResponses = orderClient.getBestSellers();
    if (bestSellerResponses == null || bestSellerResponses.isEmpty()) {
      return Collections.emptyList();
    }

    List<BestSellerResponse> topList = bestSellerResponses.stream()
        .limit(Math.max(0, limit))
        .toList();

    Map<UUID, Long> totalsByProduct = topList.stream()
        .collect(Collectors.toMap(
            BestSellerResponse::getProductId,
            BestSellerResponse::getTotalSold,
            Long::sum,
            LinkedHashMap::new
        ));

    List<UUID> productIds = new ArrayList<>(totalsByProduct.keySet());

    List<ProductEntity> products = productRepository.findAllById(productIds);

    Map<UUID, ProductEntity> productMap = products.stream()
        .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

    List<ProductBestSellerResponse> result = new ArrayList<>();
    for (UUID productId : totalsByProduct.keySet()) {
      ProductEntity product = productMap.get(productId);
      if (product == null) {
        continue;
      }

      Long sold = totalsByProduct.get(productId);

      Double price = null;
      if (product.getSkus() != null && !product.getSkus().isEmpty()) {
        price = product.getSkus().get(0).getPrice();
      }

      ProductBestSellerResponse response = new ProductBestSellerResponse(
          product.getId().toString(),
          product.getName(),
          price,
          product.getThumbnailUrl(),
          sold
      );

      result.add(response);
    }

    return result;
  }



  @Override
  public List<ProductResponse> getLatestProducts(int limit) {
    return productRepository.findLatestProducts(PageRequest.of(0, limit))
        .stream()
        .map(this::convertToDTO)
        .toList();
  }

  // Helper method convert Entity to DTO
  private ProductResponse convertToDTO(ProductEntity entity) {
    ProductResponse dto = new ProductResponse();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setSpecs(entity.getSpecs());

    if (entity.getBrand() != null) {
      dto.setBrandName(entity.getBrand().getName());
    }

    if (entity.getCategory() != null) {
      dto.setCategoryName(entity.getCategory().getName());
    }

    // Get min price from SKUs
    if (entity.getSkus() != null && !entity.getSkus().isEmpty()) {
      dto.setPrice(entity.getSkus().stream()
          .filter(sku -> Boolean.TRUE.equals(sku.getIsActive()))
          .map(SKUEntity::getPrice)
          .filter(Objects::nonNull)
          .min(Double::compareTo)
          .orElse(null));
    }
    if (entity.getImages() != null && !entity.getImages().isEmpty()) {
      entity.getImages().stream()
          .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
          .findFirst()
          .ifPresent(primaryImage -> dto.setThumbnailUrl(primaryImage.getUrl()));
    }

    if (entity.getImages() != null && !entity.getImages().isEmpty()) {
      List<String> imageUrls = entity.getImages().stream()
          .filter(img -> Boolean.FALSE.equals(img.getIsPrimary()))
          .map(ProductImageEntity::getUrl)
          .toList();
      dto.setImages(imageUrls);
    } else {
      dto.setImages(Collections.emptyList());
    }

    return dto;
  }

  // ============================================
  // NEW STATISTICS METHODS IMPLEMENTATION
  // ============================================

  @Override
  public ProductCountResponse getProductCount() {
    Long total = productRepository.count();
    Long active = skuRepository.countActiveSKUs();

    return ProductCountResponse.builder()
        .total(total)
        .active(active)
        .build();
  }

  @Override
  public InventorySummaryResponse getInventorySummary() {
    int threshold = 5;
    Long totalProducts = productRepository.count();
    Long lowStock = skuRepository.countLowStockSKUs(threshold);
    Long outOfStock = skuRepository.countOutOfStockSKUs();

    return InventorySummaryResponse.builder()
        .totalProducts(totalProducts)
        .lowStock(lowStock)
        .outOfStock(outOfStock)
        .lowStockThreshold(threshold)
        .build();
  }

  @Override
  public List<SKUResponse> getLowStockSKUs(Integer threshold) {
    if (threshold == null) {
      threshold = 5;
    }

    List<SKUEntity> lowStockSKUs = skuRepository.findLowStockSKUs(threshold);

    return lowStockSKUs.stream()
        .map(this::convertToSKUResponse)
        .toList();
  }

  // Helper method to convert SKUEntity to SKUResponse
  private SKUResponse convertToSKUResponse(SKUEntity entity) {
    return SKUResponse.builder()
        .id(entity.getId())
        .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
        .skuCode(entity.getSkuCode())
        .specs(entity.getSpecs())
        .price(entity.getPrice())
        .discountPrice(entity.getDiscountPrice())
        .salePrice(entity.getSalePrice())
        .saleId(entity.getSaleId())
        .stock(entity.getStock())
        .barcode(entity.getBarcode())
        .isActive(entity.getIsActive())
        .build();
  }
}
