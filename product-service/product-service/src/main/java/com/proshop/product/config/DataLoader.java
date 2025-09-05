package com.proshop.product.config;

import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.BrandRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner initDatabase(
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      BrandRepository brandRepository) {

    return args -> {
      // ✅ Chỉ chạy khi DB rỗng
      if (productRepository.count() == 0) {

        // Category
        CategoryEntity laptopCategory = CategoryEntity.builder()
            .name("Laptop")
            .slug("laptop")
            .build();
        categoryRepository.save(laptopCategory);

        // Brand
        BrandEntity dellBrand = BrandEntity.builder()
            .name("Dell")
            .slug("dell")
            .logoUrl("https://example.com/logo/dell.png")
            .build();
        brandRepository.save(dellBrand);

        BrandEntity hpBrand = BrandEntity.builder()
            .name("HP")
            .slug("hp")
            .logoUrl("https://example.com/logo/hp.png")
            .build();
        brandRepository.save(hpBrand);

        // Specs mẫu chung
        Map<String, Object> baseSpecs = new HashMap<>();
        baseSpecs.put("CPU", "Intel Core i7");
        baseSpecs.put("RAM", "16GB");
        baseSpecs.put("Storage", "512GB SSD");
        baseSpecs.put("Display", "15.6 inch FHD");

        // Tạo 10 sản phẩm
        for (int i = 1; i <= 10; i++) {
          BrandEntity brand = (i % 2 == 0) ? dellBrand : hpBrand;

          ProductEntity product = ProductEntity.builder()
              .name((brand.getName()) + " Laptop " + i)
              .category(laptopCategory)
              .brand(brand)
              .description("Mẫu laptop số " + i + " cho lập trình viên và designer")
              .specs(new HashMap<>(baseSpecs)) // copy specs tránh shared map
              .createdAt(LocalDateTime.now())
              .updatedAt(LocalDateTime.now())
              .build();

          productRepository.save(product);
        }

        System.out.println("✅ Sample 10 products inserted!");
      } else {
        System.out.println("ℹ️ Database already has data, skipping sample insert.");
      }
    };
  }
}

