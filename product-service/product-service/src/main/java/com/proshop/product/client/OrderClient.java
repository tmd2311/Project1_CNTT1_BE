package com.proshop.product.client;

import com.proshop.auth_lib.config.FeignAuthConfig;
import com.proshop.product.config.FeignConfig;
import com.proshop.product.dto.response.BestSellerResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "oder-service",
    url = "http://oder-serice:8083",
    configuration = {FeignConfig.class, FeignAuthConfig.class}
)
public interface OrderClient {
  @GetMapping("/api/order/best-sellers")
  List<BestSellerResponse> getBestSellers();
}
