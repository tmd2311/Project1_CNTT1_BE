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

                // ========== CATEGORY HIERARCHY FOR PC STORE ==========

                // 🖥️ LEVEL 1: LAPTOPS
                CategoryEntity laptopsCategory = CategoryEntity.builder()
                        .name("Laptops")
                        .slug("laptops")
                        .parent(null)
                        .build();
                categoryRepository.save(laptopsCategory);

                // Laptop Sub-categories (Level 2)
                CategoryEntity gamingLaptopsCategory = CategoryEntity.builder()
                        .name("Gaming Laptops")
                        .slug("gaming-laptops")
                        .parent(laptopsCategory)
                        .build();
                categoryRepository.save(gamingLaptopsCategory);

                CategoryEntity officeLaptopsCategory = CategoryEntity.builder()
                        .name("Office Laptops")
                        .slug("office-laptops")
                        .parent(laptopsCategory)
                        .build();
                categoryRepository.save(officeLaptopsCategory);

                CategoryEntity workstationLaptopsCategory = CategoryEntity.builder()
                        .name("Workstation Laptops")
                        .slug("workstation-laptops")
                        .parent(laptopsCategory)
                        .build();
                categoryRepository.save(workstationLaptopsCategory);

                CategoryEntity ultraBookCategory = CategoryEntity.builder()
                        .name("Ultrabooks")
                        .slug("ultrabooks")
                        .parent(laptopsCategory)
                        .build();
                categoryRepository.save(ultraBookCategory);

                // 🖥️ LEVEL 1: DESKTOP PCs
                CategoryEntity desktopPCsCategory = CategoryEntity.builder()
                        .name("Desktop PCs")
                        .slug("desktop-pcs")
                        .parent(null)
                        .build();
                categoryRepository.save(desktopPCsCategory);

                // Desktop PC Sub-categories (Level 2)
                CategoryEntity prebuiltPCsCategory = CategoryEntity.builder()
                        .name("Pre-built PCs")
                        .slug("prebuilt-pcs")
                        .parent(desktopPCsCategory)
                        .build();
                categoryRepository.save(prebuiltPCsCategory);

                CategoryEntity gamingPCsCategory = CategoryEntity.builder()
                        .name("Gaming PCs")
                        .slug("gaming-pcs")
                        .parent(prebuiltPCsCategory)
                        .build();
                categoryRepository.save(gamingPCsCategory);

                CategoryEntity officePCsCategory = CategoryEntity.builder()
                        .name("Office PCs")
                        .slug("office-pcs")
                        .parent(prebuiltPCsCategory)
                        .build();
                categoryRepository.save(officePCsCategory);

                CategoryEntity workstationPCsCategory = CategoryEntity.builder()
                        .name("Workstation PCs")
                        .slug("workstation-pcs")
                        .parent(prebuiltPCsCategory)
                        .build();
                categoryRepository.save(workstationPCsCategory);

                // 🔧 LEVEL 1: PC COMPONENTS
                CategoryEntity pcComponentsCategory = CategoryEntity.builder()
                        .name("PC Components")
                        .slug("pc-components")
                        .parent(null)
                        .build();
                categoryRepository.save(pcComponentsCategory);

                // Core Components (Level 2)
                CategoryEntity cpuCategory = CategoryEntity.builder()
                        .name("CPUs (Processors)")
                        .slug("cpus")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(cpuCategory);

                CategoryEntity gpuCategory = CategoryEntity.builder()
                        .name("Graphics Cards (GPUs)")
                        .slug("graphics-cards")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(gpuCategory);

                CategoryEntity motherboardCategory = CategoryEntity.builder()
                        .name("Motherboards")
                        .slug("motherboards")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(motherboardCategory);

                CategoryEntity ramCategory = CategoryEntity.builder()
                        .name("RAM Memory")
                        .slug("ram-memory")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(ramCategory);

                CategoryEntity storageCategory = CategoryEntity.builder()
                        .name("Storage")
                        .slug("storage")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(storageCategory);

                CategoryEntity psuCategory = CategoryEntity.builder()
                        .name("Power Supplies (PSU)")
                        .slug("power-supplies")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(psuCategory);

                CategoryEntity coolingCategory = CategoryEntity.builder()
                        .name("Cooling Systems")
                        .slug("cooling-systems")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(coolingCategory);

                CategoryEntity casesCategory = CategoryEntity.builder()
                        .name("PC Cases")
                        .slug("pc-cases")
                        .parent(pcComponentsCategory)
                        .build();
                categoryRepository.save(casesCategory);

                // Storage Sub-categories (Level 3)
                CategoryEntity ssdCategory = CategoryEntity.builder()
                        .name("SSDs")
                        .slug("ssds")
                        .parent(storageCategory)
                        .build();
                categoryRepository.save(ssdCategory);

                CategoryEntity hddCategory = CategoryEntity.builder()
                        .name("HDDs")
                        .slug("hdds")
                        .parent(storageCategory)
                        .build();
                categoryRepository.save(hddCategory);

                CategoryEntity nvmeSSDCategory = CategoryEntity.builder()
                        .name("NVMe SSDs")
                        .slug("nvme-ssds")
                        .parent(ssdCategory)
                        .build();
                categoryRepository.save(nvmeSSDCategory);

                CategoryEntity sata3SSDCategory = CategoryEntity.builder()
                        .name("SATA III SSDs")
                        .slug("sata3-ssds")
                        .parent(ssdCategory)
                        .build();
                categoryRepository.save(sata3SSDCategory);

                // Cooling Sub-categories (Level 3)
                CategoryEntity cpuCoolersCategory = CategoryEntity.builder()
                        .name("CPU Coolers")
                        .slug("cpu-coolers")
                        .parent(coolingCategory)
                        .build();
                categoryRepository.save(cpuCoolersCategory);

                CategoryEntity caseFansCategory = CategoryEntity.builder()
                        .name("Case Fans")
                        .slug("case-fans")
                        .parent(coolingCategory)
                        .build();
                categoryRepository.save(caseFansCategory);

                CategoryEntity liquidCoolingCategory = CategoryEntity.builder()
                        .name("Liquid Cooling")
                        .slug("liquid-cooling")
                        .parent(coolingCategory)
                        .build();
                categoryRepository.save(liquidCoolingCategory);

                // 🖱️ LEVEL 1: PERIPHERALS
                CategoryEntity peripheralsCategory = CategoryEntity.builder()
                        .name("Peripherals")
                        .slug("peripherals")
                        .parent(null)
                        .build();
                categoryRepository.save(peripheralsCategory);

                // Peripheral Sub-categories (Level 2)
                CategoryEntity monitorsCategory = CategoryEntity.builder()
                        .name("Monitors")
                        .slug("monitors")
                        .parent(peripheralsCategory)
                        .build();
                categoryRepository.save(monitorsCategory);

                CategoryEntity keyboardsCategory = CategoryEntity.builder()
                        .name("Keyboards")
                        .slug("keyboards")
                        .parent(peripheralsCategory)
                        .build();
                categoryRepository.save(keyboardsCategory);

                CategoryEntity mouseCategory = CategoryEntity.builder()
                        .name("Mice")
                        .slug("mice")
                        .parent(peripheralsCategory)
                        .build();
                categoryRepository.save(mouseCategory);

                CategoryEntity headphonesCategory = CategoryEntity.builder()
                        .name("Headphones & Audio")
                        .slug("headphones-audio")
                        .parent(peripheralsCategory)
                        .build();
                categoryRepository.save(headphonesCategory);

                // ========== BRANDS ==========

                // Laptop Brands
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

                BrandEntity asusLaptopBrand = BrandEntity.builder()
                        .name("ASUS")
                        .slug("asus")
                        .logoUrl("https://example.com/logo/asus.png")
                        .build();
                brandRepository.save(asusLaptopBrand);

                BrandEntity msiBrand = BrandEntity.builder()
                        .name("MSI")
                        .slug("msi")
                        .logoUrl("https://example.com/logo/msi.png")
                        .build();
                brandRepository.save(msiBrand);

                // CPU Brands
                BrandEntity intelBrand = BrandEntity.builder()
                        .name("Intel")
                        .slug("intel")
                        .logoUrl("https://example.com/logo/intel.png")
                        .build();
                brandRepository.save(intelBrand);

                BrandEntity amdBrand = BrandEntity.builder()
                        .name("AMD")
                        .slug("amd")
                        .logoUrl("https://example.com/logo/amd.png")
                        .build();
                brandRepository.save(amdBrand);

                // GPU Brands
                BrandEntity nvidiaBrand = BrandEntity.builder()
                        .name("NVIDIA")
                        .slug("nvidia")
                        .logoUrl("https://example.com/logo/nvidia.png")
                        .build();
                brandRepository.save(nvidiaBrand);

                // Memory & Storage Brands
                BrandEntity corsairBrand = BrandEntity.builder()
                        .name("Corsair")
                        .slug("corsair")
                        .logoUrl("https://example.com/logo/corsair.png")
                        .build();
                brandRepository.save(corsairBrand);

                BrandEntity kingstonBrand = BrandEntity.builder()
                        .name("Kingston")
                        .slug("kingston")
                        .logoUrl("https://example.com/logo/kingston.png")
                        .build();
                brandRepository.save(kingstonBrand);

                BrandEntity samsungBrand = BrandEntity.builder()
                        .name("Samsung")
                        .slug("samsung")
                        .logoUrl("https://example.com/logo/samsung.png")
                        .build();
                brandRepository.save(samsungBrand);

                // ========== SAMPLE PRODUCTS ==========

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

    private void createGamingLaptop(String name, BrandEntity brand, CategoryEntity category,
                                    ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "CPU", "Intel Core i7-12700H",
                "GPU", "NVIDIA RTX 4060",
                "Display", "15.6\" FHD 144Hz",
                "Type", "Gaming Laptop"
        );

        ProductEntity laptop = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("Gaming laptop hiệu suất cao với GPU rời và màn hình 144Hz")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(laptop);

        createLaptopSKUs(laptop, brand, skuRepository);
    }

    private void createOfficeLaptop(String name, BrandEntity brand, CategoryEntity category,
                                    ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "CPU", "Intel Core i5-1235U",
                "GPU", "Intel Iris Xe",
                "Display", "15.6\" FHD",
                "Type", "Office Laptop"
        );

        ProductEntity laptop = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("Laptop văn phòng nhẹ nhàng, tiết kiệm pin")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(laptop);

        createLaptopSKUs(laptop, brand, skuRepository);
    }

    private void createCPUProduct(String name, BrandEntity brand, CategoryEntity category,
                                  ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "Socket", brand.getName().equals("Intel") ? "LGA1700" : "AM4",
                "Cores", brand.getName().equals("Intel") ? "12" : "8",
                "Threads", brand.getName().equals("Intel") ? "20" : "16",
                "Base Clock", brand.getName().equals("Intel") ? "3.6 GHz" : "3.8 GHz"
        );

        ProductEntity cpu = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("CPU hiệu suất cao cho gaming và công việc")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(cpu);

        createComponentSKU(cpu, brand, "CPU", skuRepository, 8000000.0);
    }

    private void createGPUProduct(String name, BrandEntity brand, CategoryEntity category,
                                  ProductRepository productRepository, SKURepository skuRepository) {
        Map<String, Object> specs = Map.of(
                "GPU Chip", "NVIDIA RTX 4070",
                "VRAM", "12GB GDDR6X",
                "Memory Bus", "192-bit",
                "Boost Clock", "2610 MHz"
        );

        ProductEntity gpu = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("Card đồ họa RTX 4070 cho gaming 1440p")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(gpu);

        createComponentSKU(gpu, brand, "GPU", skuRepository, 18000000.0);
    }

    private void createRAMProduct(String name, BrandEntity brand, CategoryEntity category,
                                  ProductRepository productRepository, SKURepository skuRepository) {
        boolean is32GB = name.contains("32GB");
        Map<String, Object> specs = Map.of(
                "Capacity", is32GB ? "32GB (2x16GB)" : "16GB (2x8GB)",
                "Speed", "DDR4-3200",
                "Latency", "CL16",
                "Type", "DDR4 DIMM"
        );

        ProductEntity ram = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("RAM DDR4 hiệu suất cao cho gaming và multitasking")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(ram);

        createComponentSKU(ram, brand, "RAM", skuRepository, is32GB ? 4500000.0 : 2200000.0);
    }

    private void createSSDProduct(String name, BrandEntity brand, CategoryEntity category,
                                  ProductRepository productRepository, SKURepository skuRepository) {
        boolean is1TB = name.contains("1TB");
        Map<String, Object> specs = Map.of(
                "Capacity", is1TB ? "1TB" : "500GB",
                "Interface", "PCIe 4.0 x4",
                "Form Factor", "M.2 2280",
                "Read Speed", "7000 MB/s",
                "Write Speed", "6900 MB/s"
        );

        ProductEntity ssd = ProductEntity.builder()
                .name(name)
                .category(category)
                .brand(brand)
                .description("SSD NVMe PCIe 4.0 tốc độ cao")
                .specs(specs)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(ssd);

        createComponentSKU(ssd, brand, "SSD", skuRepository, is1TB ? 3500000.0 : 1800000.0);
    }

    private void createLaptopSKUs(ProductEntity laptop, BrandEntity brand, SKURepository skuRepository) {
        String[] ramConfigs = {"8GB", "16GB", "32GB"};
        String[] storageConfigs = {"512GB", "1TB"};

        int skuIndex = 1;
        for (String ram : ramConfigs) {
            for (String storage : storageConfigs) {
                SKUEntity sku = SKUEntity.builder()
                        .product(laptop)
                        .skuCode(laptop.getName().replaceAll(" ", "-").toUpperCase() + "-" + ram + "-" + storage)
                        .specs(Map.of(
                                "RAM", ram,
                                "Storage", storage + " SSD",
                                "Warranty", "24 months"
                        ))
                        .price(20000000.0 + skuIndex * 2000000)
                        .discountPrice(19000000.0 + skuIndex * 1800000)
                        .stock(10 + skuIndex * 2)
                        .barcode("LAPTOP-" + brand.getSlug().toUpperCase() + "-" + skuIndex)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                skuRepository.save(sku);
                skuIndex++;
            }
        }
    }

    private void createComponentSKU(ProductEntity product, BrandEntity brand, String type,
                                    SKURepository skuRepository, Double basePrice) {
        SKUEntity sku = SKUEntity.builder()
                .product(product)
                .skuCode(product.getName().replaceAll(" ", "-").toUpperCase())
                .specs(Map.of(
                        "Warranty", "36 months",
                        "Condition", "New",
                        "Type", type
                ))
                .price(basePrice)
                .discountPrice(basePrice * 0.95)
                .stock(25)
                .barcode(type + "-" + brand.getSlug().toUpperCase() + "-" + System.currentTimeMillis())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        skuRepository.save(sku);
    }
}