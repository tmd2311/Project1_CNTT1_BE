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
                brandRepository.save(samsungBrand);

                // ========== SAMPLE PRODUCTS ==========
                // ========== NEW COMPONENTS: MONITORS, MOTHERBOARDS, RAM, GPUS ==========

// 🖥️ MONITORS
                createMonitorProduct("Samsung Odyssey G5 27\"", samsungBrand, monitorsCategory, productRepository, skuRepository);
                createMonitorProduct("ASUS TUF Gaming VG249Q1A", asusLaptopBrand, monitorsCategory, productRepository, skuRepository);
                createMonitorProduct("MSI G27C4 E2", msiBrand, monitorsCategory, productRepository, skuRepository);

// 🧩 MOTHERBOARDS
                createMotherboardProduct("ASUS PRIME B660M-A WIFI D4", asusLaptopBrand, motherboardCategory, productRepository, skuRepository);
                createMotherboardProduct("MSI B550M PRO-VDH WIFI", msiBrand, motherboardCategory, productRepository, skuRepository);
                createMotherboardProduct("Gigabyte Z790 AORUS ELITE AX", asusLaptopBrand, motherboardCategory, productRepository, skuRepository);

// 💾 RAM (Additional Models)
                createRAMProduct("Corsair Vengeance RGB Pro 16GB", corsairBrand, ramCategory, productRepository, skuRepository);
                createRAMProduct("Kingston Fury Beast RGB 32GB", kingstonBrand, ramCategory, productRepository, skuRepository);

// 🎮 GRAPHICS CARDS (Additional GPUs)
                createGPUProduct("MSI RTX 4060 Ti Gaming X", msiBrand, gpuCategory, productRepository, skuRepository);
                createGPUProduct("ASUS Dual RTX 4080 Super", asusLaptopBrand, gpuCategory, productRepository, skuRepository);
                createGPUProduct("Gigabyte RTX 3060 Windforce OC", asusLaptopBrand, gpuCategory, productRepository, skuRepository);


                // Gaming Laptops
                createGamingLaptop("ASUS ROG Strix G15", asusLaptopBrand, gamingLaptopsCategory, productRepository, skuRepository);
                createGamingLaptop("MSI Gaming GF63", msiBrand, gamingLaptopsCategory, productRepository, skuRepository);

                // Office Laptops
                createOfficeLaptop("Dell Inspiron 15 3000", dellBrand, officeLaptopsCategory, productRepository, skuRepository);
                createOfficeLaptop("HP Pavilion 15", hpBrand, officeLaptopsCategory, productRepository, skuRepository);

                // CPUs
                createCPUProduct("Intel Core i7-12700K", intelBrand, cpuCategory, productRepository, skuRepository);
                createCPUProduct("AMD Ryzen 7 5800X", amdBrand, cpuCategory, productRepository, skuRepository);

                // Graphics Cards
                createGPUProduct("ASUS RTX 4070", asusLaptopBrand, gpuCategory, productRepository, skuRepository);

                // RAM
                createRAMProduct("Corsair Vengeance LPX 16GB", corsairBrand, ramCategory, productRepository, skuRepository);
                createRAMProduct("Kingston Fury Beast 32GB", kingstonBrand, ramCategory, productRepository, skuRepository);

                // SSDs
                createSSDProduct("Samsung 980 PRO 1TB", samsungBrand, nvmeSSDCategory, productRepository, skuRepository);
                createSSDProduct("Kingston NV2 500GB", kingstonBrand, nvmeSSDCategory, productRepository, skuRepository);

                System.out.println("✅ PC Store sample data inserted!");
                System.out.println("📁 Category Structure:");
                System.out.println("   🖥️ Laptops → Gaming, Office, Workstation, Ultrabooks");
                System.out.println("   🖥️ Desktop PCs → Pre-built (Gaming, Office, Workstation)");
                System.out.println("   🔧 PC Components → CPU, GPU, Motherboard, RAM, Storage, PSU, Cooling, Cases");
                System.out.println("   💾 Storage → SSDs (NVMe, SATA III), HDDs");
                System.out.println("   🌀 Cooling → CPU Coolers, Case Fans, Liquid Cooling");
                System.out.println("   🖱️ Peripherals → Monitors, Keyboards, Mice, Headphones");

            } else {
                System.out.println("ℹ️ Database already has data, skipping sample insert.");
            }
        };
    }

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
    private void createMonitorProduct(String name, BrandEntity brand, CategoryEntity category,
                                      ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "Size", name.contains("27") ? "27 inch" : "24 inch",
                "Panel", "VA 1500R Curved",
                "Resolution", "2560x1440 QHD",
                "Refresh Rate", "165Hz",
                "Response Time", "1ms"
        );

        ProductEntity monitor = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("Màn hình chơi game cong tần số quét cao, hiển thị sống động")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(monitor);

        createComponentSKU(monitor, brand, "Monitor", skuRepository, 6500000.0);
    }

    private void createMotherboardProduct(String name, BrandEntity brand, CategoryEntity category,
                                          ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "Chipset", name.contains("B550") ? "AMD B550" : (name.contains("Z790") ? "Intel Z790" : "Intel B660"),
                "Socket", name.contains("AMD") ? "AM4" : "LGA1700",
                "Form Factor", "ATX",
                "RAM Support", "DDR4 / DDR5",
                "M.2 Slots", "2"
        );

        ProductEntity mb = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("Mainboard chất lượng cao, hỗ trợ overclock và PCIe 4.0")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(mb);

        createComponentSKU(mb, brand, "Motherboard", skuRepository, 4200000.0);
    }

}
