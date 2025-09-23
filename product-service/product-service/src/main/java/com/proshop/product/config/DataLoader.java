package com.proshop.product.config;

import com.proshop.product.entity.CategoryImageEntity;
import com.proshop.product.entity.ProductEntity;
import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.entity.BrandEntity;
import com.proshop.product.entity.SKUEntity;
import com.proshop.product.entity.ProductImageEntity;
import com.proshop.product.repository.CategoryImageRepository;
import com.proshop.product.repository.ProductRepository;
import com.proshop.product.repository.CategoryRepository;
import com.proshop.product.repository.BrandRepository;
import com.proshop.product.repository.SKURepository;
import com.proshop.product.repository.ProductImageRepository;
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
        SKURepository skuRepository,
        CategoryImageRepository categoryImageRepository,
        ProductImageRepository productImageRepository // 👈 thêm repository cho product image
    ) {

        return args -> {
            if (productRepository.count() == 0) {

                // ================= CATEGORY + IMAGE =================
                CategoryEntity televisions = CategoryEntity.builder()
                    .name("Televisions")
                    .slug("televisions")
                    .build();

                CategoryEntity laptops = CategoryEntity.builder()
                    .name("Laptop & PC")
                    .slug("laptop-pc")
                    .build();

                CategoryEntity mobiles = CategoryEntity.builder()
                    .name("Mobile & Tablets")
                    .slug("mobile-tablets")
                    .build();

                CategoryEntity games = CategoryEntity.builder()
                    .name("Games & Videos")
                    .slug("games-videos")
                    .build();

                CategoryEntity appliances = CategoryEntity.builder()
                    .name("Home Appliances")
                    .slug("home-appliances")
                    .build();

                CategoryEntity sports = CategoryEntity.builder()
                    .name("Health & Sports")
                    .slug("health-sports")
                    .build();

                CategoryEntity watches = CategoryEntity.builder()
                    .name("Watches")
                    .slug("watches")
                    .build();

                CategoryEntity televisions2 = CategoryEntity.builder()
                    .name("Televisions")
                    .slug("televisions-2")
                    .build();

                categoryRepository.saveAll(
                    java.util.List.of(televisions, laptops, mobiles, games, appliances, sports, watches, televisions2)
                );

                categoryImageRepository.saveAll(java.util.List.of(
                    CategoryImageEntity.builder().category(televisions).url("/images/categories/categories-01.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(laptops).url("/images/categories/categories-02.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(mobiles).url("/images/categories/categories-03.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(games).url("/images/categories/categories-04.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(appliances).url("/images/categories/categories-05.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(sports).url("/images/categories/categories-06.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(watches).url("/images/categories/categories-07.png").isPrimary(true).build(),
                    CategoryImageEntity.builder().category(televisions2).url("/images/categories/categories-04.png").isPrimary(true).build()
                ));

                // ================= BRAND =================
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

                // ================= SAMPLE PRODUCTS =================
                Map<String, Object> baseSpecs = new HashMap<>();
                baseSpecs.put("CPU", "Intel Core i7");
                baseSpecs.put("Display", "15.6 inch FHD");

                for (int i = 1; i <= 10; i++) {
                    BrandEntity brand = (i % 2 == 0) ? dellBrand : hpBrand;

                    ProductEntity product = ProductEntity.builder()
                        .name(brand.getName() + " Laptop " + i)
                        .category(laptops) // cho vào Laptop & PC
                        .brand(brand)
                        .description("Mẫu laptop số " + i + " cho lập trình viên và designer")
                        .specs(new HashMap<>(baseSpecs))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                    productRepository.save(product);

                    // ================= PRODUCT IMAGES =================
                    ProductImageEntity primaryImage = ProductImageEntity.builder()
                        .product(product)
                        .url("/images/products/product-" + i + "-1.png")
                        .isPrimary(true)
                        .build();
                    productImageRepository.save(primaryImage);

                    for (int imgIndex = 2; imgIndex <= 3; imgIndex++) {
                        ProductImageEntity extraImage = ProductImageEntity.builder()
                            .product(product)
                            .url("/images/products/product-" + i + "-" + imgIndex + ".png")
                            .isPrimary(false)
                            .build();
                        productImageRepository.save(extraImage);
                    }

                    // ================= SKU =================
                    SKUEntity sku1 = SKUEntity.builder()
                        .product(product)
                        .skuCode("SKU-" + brand.getSlug().toUpperCase() + "-" + i + "-8GB")
                        .specs(Map.of("RAM", "8GB", "Storage", "512GB SSD", "Color", "Silver"))
                        .price(12000000.0 + i * 500000)
                        .discountPrice(11500000.0 + i * 400000)
                        .stock(20 + i)
                        .barcode("BARCODE-" + i + "-8GB")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                    SKUEntity sku2 = SKUEntity.builder()
                        .product(product)
                        .skuCode("SKU-" + brand.getSlug().toUpperCase() + "-" + i + "-16GB")
                        .specs(Map.of("RAM", "16GB", "Storage", "1TB SSD", "Color", "Black"))
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

                System.out.println("Sample categories, brands, products, SKUs and images inserted!");
            } else {
                System.out.println("Database already has data, skipping sample insert.");
            }
        };
    }
}
