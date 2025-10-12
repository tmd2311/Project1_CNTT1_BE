package com.proshop.product.config;

import com.proshop.product.entity.*;
import com.proshop.product.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
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
        ProductImageRepository productImageRepository
    ) {

        return args -> {
            if (productRepository.count() > 0) {
                System.out.println("ℹ️ Database already has data, skipping sample insert.");
                return;
            }

            // ================= CATEGORY =================
            CategoryEntity laptops = CategoryEntity.builder().name("Laptop & PC").slug("laptop-pc").build();
            CategoryEntity monitors = CategoryEntity.builder().name("Monitors").slug("monitors").build();
            CategoryEntity motherboards = CategoryEntity.builder().name("Motherboards").slug("motherboards").build();
            CategoryEntity ram = CategoryEntity.builder().name("RAM").slug("ram").build();
            CategoryEntity gpu = CategoryEntity.builder().name("Graphics Cards").slug("graphics-cards").build();
            categoryRepository.saveAll(List.of(laptops, monitors, motherboards, ram, gpu));

            // ================= CATEGORY IMAGES =================
            categoryImageRepository.saveAll(List.of(
                CategoryImageEntity.builder().category(laptops).url("/images/categories/categories-02.png").isPrimary(true).build(),
                CategoryImageEntity.builder().category(monitors).url("/images/categories/categories-03.png").isPrimary(true).build(),
                CategoryImageEntity.builder().category(motherboards).url("/images/categories/categories-04.png").isPrimary(true).build(),
                CategoryImageEntity.builder().category(ram).url("/images/categories/categories-05.png").isPrimary(true).build(),
                CategoryImageEntity.builder().category(gpu).url("/images/categories/categories-06.png").isPrimary(true).build()
            ));

            // ================= BRANDS =================
            BrandEntity dell = brandRepository.save(BrandEntity.builder().name("Dell").slug("dell").logoUrl("https://example.com/logo/dell.png").build());
            BrandEntity hp = brandRepository.save(BrandEntity.builder().name("HP").slug("hp").logoUrl("https://example.com/logo/hp.png").build());
            BrandEntity asus = brandRepository.save(BrandEntity.builder().name("ASUS").slug("asus").logoUrl("https://example.com/logo/asus.png").build());
            BrandEntity msi = brandRepository.save(BrandEntity.builder().name("MSI").slug("msi").logoUrl("https://example.com/logo/msi.png").build());
            BrandEntity samsung = brandRepository.save(BrandEntity.builder().name("Samsung").slug("samsung").logoUrl("https://example.com/logo/samsung.png").build());
            BrandEntity kingston = brandRepository.save(BrandEntity.builder().name("Kingston").slug("kingston").logoUrl("https://example.com/logo/kingston.png").build());
            BrandEntity corsair = brandRepository.save(BrandEntity.builder().name("Corsair").slug("corsair").logoUrl("https://example.com/logo/corsair.png").build());

            // ================= SAMPLE PRODUCTS =================
            for (int i = 1; i <= 5; i++) {
                BrandEntity brand = (i % 2 == 0) ? dell : hp;

                ProductEntity product = ProductEntity.builder()
                    .name(brand.getName() + " Laptop " + i)
                    .category(laptops)
                    .brand(brand)
                    .description("Laptop " + i + " mạnh mẽ cho dân lập trình và designer.")
                    .specs(Map.of("CPU", "Intel Core i7", "Display", "15.6 inch FHD"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                productRepository.save(product);

                // Product images
                productImageRepository.save(ProductImageEntity.builder().product(product).url("/images/products/product-" + i + "-1.png").isPrimary(true).build());
                productImageRepository.save(ProductImageEntity.builder().product(product).url("/images/products/product-" + i + "-2.png").isPrimary(false).build());

                // SKUs
                skuRepository.save(SKUEntity.builder()
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
                    .build());

                skuRepository.save(SKUEntity.builder()
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
                    .build());
            }

            // ================= EXTRA PRODUCTS =================
            createMonitorProduct("Samsung Odyssey G5 27\"", samsung, monitors, productRepository, skuRepository);
            createMonitorProduct("ASUS TUF Gaming VG249Q1A", asus, monitors, productRepository, skuRepository);
            createMotherboardProduct("MSI B550M PRO-VDH WIFI", msi, motherboards, productRepository, skuRepository);
            createRAMProduct("Corsair Vengeance RGB Pro 16GB", corsair, ram, productRepository, skuRepository);
            createGPUProduct("ASUS Dual RTX 4080 Super", asus, gpu, productRepository, skuRepository);

            System.out.println("✅ Sample data inserted successfully!");
        };
    }

    // ========== HELPER FUNCTIONS ==========

    private void createMonitorProduct(String name, BrandEntity brand, CategoryEntity category,
        ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
            "Size", name.contains("27") ? "27 inch" : "24 inch",
            "Panel", "VA Curved",
            "Resolution", "2560x1440 QHD",
            "Refresh Rate", "165Hz"
        );

        ProductEntity monitor = productRepository.save(ProductEntity.builder()
            .name(name)
            .category(category)
            .brand(brand)
            .description("Màn hình chơi game cong, tần số quét cao")
            .specs(specs)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

        createComponentSKU(monitor, brand, "Monitor", skuRepository, 6500000.0);
    }

    private void createMotherboardProduct(String name, BrandEntity brand, CategoryEntity category,
        ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
            "Chipset", name.contains("B550") ? "AMD B550" : "Intel B660",
            "Socket", name.contains("AMD") ? "AM4" : "LGA1700",
            "Form Factor", "ATX"
        );

        ProductEntity mb = productRepository.save(ProductEntity.builder()
            .name(name)
            .category(category)
            .brand(brand)
            .description("Mainboard hỗ trợ PCIe 4.0, hiệu năng cao")
            .specs(specs)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

        createComponentSKU(mb, brand, "Motherboard", skuRepository, 4200000.0);
    }

    private void createRAMProduct(String name, BrandEntity brand, CategoryEntity category,
        ProductRepository productRepository, SKURepository skuRepository) {
        ProductEntity ram = productRepository.save(ProductEntity.builder()
            .name(name)
            .category(category)
            .brand(brand)
            .description("RAM hiệu suất cao cho gaming và đồ họa")
            .specs(Map.of("Type", "DDR4", "Speed", "3200MHz"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

        createComponentSKU(ram, brand, "RAM", skuRepository, 1800000.0);
    }

    private void createGPUProduct(String name, BrandEntity brand, CategoryEntity category,
        ProductRepository productRepository, SKURepository skuRepository) {
        ProductEntity gpu = productRepository.save(ProductEntity.builder()
            .name(name)
            .category(category)
            .brand(brand)
            .description("Card đồ họa mạnh mẽ cho gaming và render")
            .specs(Map.of("Memory", "12GB GDDR6X", "Bus", "192-bit"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

        createComponentSKU(gpu, brand, "GPU", skuRepository, 19000000.0);
    }

    private void createComponentSKU(ProductEntity product, BrandEntity brand, String type,
        SKURepository skuRepository, double basePrice) {
        skuRepository.save(SKUEntity.builder()
            .product(product)
            .skuCode("SKU-" + brand.getSlug().toUpperCase() + "-" + type.toUpperCase())
            .specs(Map.of("Warranty", "36 tháng", "Origin", "Chính hãng"))
            .price(basePrice)
            .discountPrice(basePrice * 0.95)
            .stock(10)
            .barcode("BARCODE-" + product.getName().replaceAll("\\s+", "-").toUpperCase())
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());
    }
}
