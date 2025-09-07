package com.proshop.product.config;

import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.SKURepository;
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
            BrandRepository brandRepository,
            SKURepository skuRepository) {

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
                baseSpecs.put("Display", "15.6 inch FHD");

                // Tạo 10 sản phẩm
                for (int i = 1; i <= 10; i++) {
                    BrandEntity brand = (i % 2 == 0) ? dellBrand : hpBrand;

                    ProductEntity product = ProductEntity.builder()
                            .name(brand.getName() + " Laptop " + i)
                            .category(laptopCategory)
                            .brand(brand)
                            .description("Mẫu laptop số " + i + " cho lập trình viên và designer")
                            .specs(new HashMap<>(baseSpecs)) // copy specs tránh shared map
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    productRepository.save(product);

                    // SKU 1 - RAM 8GB
                    SKUEntity sku1 = SKUEntity.builder()
                            .product(product)
                            .skuCode("SKU-" + brand.getSlug().toUpperCase() + "-" + i + "-8GB")
                            .specs(Map.of(
                                    "RAM", "8GB",
                                    "Storage", "512GB SSD",
                                    "Color", "Silver"
                            ))
                            .price(12000000.0 + i * 500000)
                            .discountPrice(11500000.0 + i * 400000)
                            .stock(20 + i)
                            .barcode("BARCODE-" + i + "-8GB")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    // SKU 2 - RAM 16GB
                    SKUEntity sku2 = SKUEntity.builder()
                            .product(product)
                            .skuCode("SKU-" + brand.getSlug().toUpperCase() + "-" + i + "-16GB")
                            .specs(Map.of(
                                    "RAM", "16GB",
                                    "Storage", "1TB SSD",
                                    "Color", "Black"
                            ))
                            .price(14000000.0 + i * 500000)
                            .discountPrice(13500000.0 + i * 400000)
                            .stock(15 + i)
                            .barcode("BARCODE-" + i + "-16GB")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    skuRepository.save(sku1);
                    skuRepository.save(sku2);
                }

                System.out.println("✅ Sample 10 products with SKUs inserted!");
            } else {
                System.out.println("ℹ️ Database already has data, skipping sample insert.");
            }
        };
    }
}
