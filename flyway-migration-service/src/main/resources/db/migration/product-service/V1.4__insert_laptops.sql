-- ============================================
-- ADDITIONAL LAPTOP PRODUCTS
-- Gaming & Business Laptops
-- ============================================

-- ==============================
-- 💻 GAMING LAPTOPS
-- Category: Gaming Laptop
-- Category ID: c2222222-0001-0000-0000-000000000001
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000071', 'ASUS ROG Strix G16', 'Laptop gaming cao cấp Intel Core i9 Gen 13, RTX 4070, màn hình 16" 240Hz, thiết kế RGB đẹp mắt.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-asus-rog-strix-g16.png', NOW()),
('11111111-0000-0000-0000-000000000072', 'MSI Raider GE78 HX', 'Laptop gaming siêu mạnh i9-13980HX, RTX 4090, màn hình 4K 144Hz, tản nhiệt Cooler Boost 5.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-msi-raider-ge78.png', NOW()),
('11111111-0000-0000-0000-000000000073', 'Dell Alienware M16', 'Laptop gaming premium từ Dell, i9-13900HX, RTX 4080, màn hình QHD+ 240Hz, thiết kế Legend 2.0.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-dell-alienware-m16.png', NOW()),
('11111111-0000-0000-0000-000000000074', 'Lenovo Legion Pro 7i', 'Laptop gaming chuyên nghiệp i9-13900HX, RTX 4080, màn hình 16" 240Hz, tản nhiệt Legion AI.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-lenovo-legion-pro7.png', NOW()),
('11111111-0000-0000-0000-000000000075', 'Acer Predator Helios 16', 'Laptop gaming mạnh mẽ i7-13700HX, RTX 4070, màn hình WQXGA 240Hz, RGB keyboard 4 vùng.', 'a1b2c3d4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-acer-predator-helios16.png', NOW()),
('11111111-0000-0000-0000-000000000076', 'ASUS TUF Gaming F15', 'Laptop gaming phổ thông i5-12500H, RTX 4050, màn hình 144Hz, độ bền chuẩn MIL-STD-810H.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-asus-tuf-f15.png', NOW()),
('11111111-0000-0000-0000-000000000077', 'MSI Cyborg 15', 'Laptop gaming tầm trung i7-12650H, RTX 4060, thiết kế trong suốt độc đáo, giá cả hợp lý.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-msi-cyborg15.png', NOW()),
('11111111-0000-0000-0000-000000000078', 'Lenovo LOQ 15', 'Laptop gaming giá rẻ i5-12450H, RTX 4050, màn hình 144Hz, thiết kế trẻ trung năng động.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/laptop-lenovo-loq15.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
-- ASUS ROG Strix G16
(gen_random_uuid(), '11111111-0000-0000-0000-000000000071', 'http://103.90.225.90:8084/files/laptop-asus-rog-strix-g16.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000071', 'http://103.90.225.90:8084/files/laptop-asus-rog-strix-g16-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000071', 'http://103.90.225.90:8084/files/laptop-asus-rog-strix-g16-2.png', FALSE),
-- MSI Raider GE78
(gen_random_uuid(), '11111111-0000-0000-0000-000000000072', 'http://103.90.225.90:8084/files/laptop-msi-raider-ge78.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000072', 'http://103.90.225.90:8084/files/laptop-msi-raider-ge78-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000072', 'http://103.90.225.90:8084/files/laptop-msi-raider-ge78-2.png', FALSE),
-- Dell Alienware M16
(gen_random_uuid(), '11111111-0000-0000-0000-000000000073', 'http://103.90.225.90:8084/files/laptop-dell-alienware-m16.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000073', 'http://103.90.225.90:8084/files/laptop-dell-alienware-m16-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000073', 'http://103.90.225.90:8084/files/laptop-dell-alienware-m16-2.png', FALSE),
-- Lenovo Legion Pro 7i
(gen_random_uuid(), '11111111-0000-0000-0000-000000000074', 'http://103.90.225.90:8084/files/laptop-lenovo-legion-pro7.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000074', 'http://103.90.225.90:8084/files/laptop-lenovo-legion-pro7-1.png', FALSE),
-- Acer Predator Helios 16
(gen_random_uuid(), '11111111-0000-0000-0000-000000000075', 'http://103.90.225.90:8084/files/laptop-acer-predator-helios16.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000075', 'http://103.90.225.90:8084/files/laptop-acer-predator-helios16-1.png', FALSE),
-- ASUS TUF F15
(gen_random_uuid(), '11111111-0000-0000-0000-000000000076', 'http://103.90.225.90:8084/files/laptop-asus-tuf-f15.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000076', 'http://103.90.225.90:8084/files/laptop-asus-tuf-f15-1.png', FALSE),
-- MSI Cyborg 15
(gen_random_uuid(), '11111111-0000-0000-0000-000000000077', 'http://103.90.225.90:8084/files/laptop-msi-cyborg15.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000077', 'http://103.90.225.90:8084/files/laptop-msi-cyborg15-1.png', FALSE),
-- Lenovo LOQ 15
(gen_random_uuid(), '11111111-0000-0000-0000-000000000078', 'http://103.90.225.90:8084/files/laptop-lenovo-loq15.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000078', 'http://103.90.225.90:8084/files/laptop-lenovo-loq15-1.png', FALSE);

-- SKUs for ASUS ROG Strix G16
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000071', 'ROG-STRIX-G16-I9-4070-32GB',
'{"cpu":"Intel Core i9-13980HX", "gpu":"RTX 4070 8GB", "ram":"32GB DDR5-4800", "storage":"1TB NVMe SSD", "display":"16\" FHD 240Hz", "os":"Windows 11"}',
54990000, NULL, 15, '893123471001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000071', 'ROG-STRIX-G16-I9-4070-16GB',
'{"cpu":"Intel Core i9-13980HX", "gpu":"RTX 4070 8GB", "ram":"16GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"16\" FHD 240Hz", "os":"Windows 11"}',
49990000, NULL, 20, '893123471002', TRUE, NOW(), NOW());

-- SKUs for MSI Raider GE78 HX
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000072', 'RAIDER-GE78-I9-4090-64GB',
'{"cpu":"Intel Core i9-13980HX", "gpu":"RTX 4090 16GB", "ram":"64GB DDR5-5600", "storage":"2TB NVMe SSD", "display":"17\" UHD 144Hz", "os":"Windows 11 Pro"}',
99990000, NULL, 5, '893123471003', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000072', 'RAIDER-GE78-I9-4080-32GB',
'{"cpu":"Intel Core i9-13980HX", "gpu":"RTX 4080 12GB", "ram":"32GB DDR5-5600", "storage":"1TB NVMe SSD", "display":"17\" QHD 240Hz", "os":"Windows 11 Pro"}',
79990000, NULL, 8, '893123471004', TRUE, NOW(), NOW());

-- SKUs for Dell Alienware M16
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000073', 'ALIENWARE-M16-I9-4080-32GB',
'{"cpu":"Intel Core i9-13900HX", "gpu":"RTX 4080 12GB", "ram":"32GB DDR5-4800", "storage":"1TB NVMe SSD", "display":"16\" QHD+ 240Hz", "os":"Windows 11"}',
74990000, NULL, 10, '893123471005', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000073', 'ALIENWARE-M16-I7-4070-16GB',
'{"cpu":"Intel Core i7-13700HX", "gpu":"RTX 4070 8GB", "ram":"16GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"16\" QHD+ 165Hz", "os":"Windows 11"}',
59990000, NULL, 12, '893123471006', TRUE, NOW(), NOW());

-- SKUs for Lenovo Legion Pro 7i
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000074', 'LEGION-PRO7-I9-4080-32GB',
'{"cpu":"Intel Core i9-13900HX", "gpu":"RTX 4080 12GB", "ram":"32GB DDR5-5600", "storage":"1TB NVMe SSD", "display":"16\" WQXGA 240Hz", "os":"Windows 11"}',
72990000, NULL, 12, '893123471007', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000074', 'LEGION-PRO7-I9-4070-16GB',
'{"cpu":"Intel Core i9-13900HX", "gpu":"RTX 4070 8GB", "ram":"16GB DDR5-5600", "storage":"512GB NVMe SSD", "display":"16\" WQXGA 165Hz", "os":"Windows 11"}',
62990000, NULL, 15, '893123471008', TRUE, NOW(), NOW());

-- SKUs for Acer Predator Helios 16
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000075', 'HELIOS16-I7-4070-32GB',
'{"cpu":"Intel Core i7-13700HX", "gpu":"RTX 4070 8GB", "ram":"32GB DDR5-4800", "storage":"1TB NVMe SSD", "display":"16\" WQXGA 240Hz", "os":"Windows 11"}',
52990000, NULL, 18, '893123471009', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000075', 'HELIOS16-I7-4060-16GB',
'{"cpu":"Intel Core i7-13700HX", "gpu":"RTX 4060 8GB", "ram":"16GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"16\" WQXGA 165Hz", "os":"Windows 11"}',
44990000, NULL, 22, '893123471010', TRUE, NOW(), NOW());

-- SKUs for ASUS TUF Gaming F15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000076', 'TUF-F15-I5-4050-16GB',
'{"cpu":"Intel Core i5-12500H", "gpu":"RTX 4050 6GB", "ram":"16GB DDR4-3200", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 144Hz", "os":"Windows 11"}',
24990000, NULL, 30, '893123471011', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000076', 'TUF-F15-I7-4050-16GB',
'{"cpu":"Intel Core i7-12700H", "gpu":"RTX 4050 6GB", "ram":"16GB DDR4-3200", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 144Hz", "os":"Windows 11"}',
27990000, NULL, 25, '893123471012', TRUE, NOW(), NOW());

-- SKUs for MSI Cyborg 15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000077', 'CYBORG15-I7-4060-16GB',
'{"cpu":"Intel Core i7-12650H", "gpu":"RTX 4060 8GB", "ram":"16GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 144Hz", "os":"Windows 11"}',
28990000, NULL, 28, '893123471013', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000077', 'CYBORG15-I5-4050-8GB',
'{"cpu":"Intel Core i5-12450H", "gpu":"RTX 4050 6GB", "ram":"8GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 144Hz", "os":"Windows 11"}',
22990000, NULL, 35, '893123471014', TRUE, NOW(), NOW());

-- SKUs for Lenovo LOQ 15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000078', 'LOQ15-I5-4050-16GB',
'{"cpu":"Intel Core i5-12450H", "gpu":"RTX 4050 6GB", "ram":"16GB DDR5-4800", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 144Hz", "os":"Windows 11"}',
23990000, NULL, 40, '893123471015', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000078', 'LOQ15-I7-4060-16GB',
'{"cpu":"Intel Core i7-13650HX", "gpu":"RTX 4060 8GB", "ram":"16GB DDR5-5200", "storage":"512GB NVMe SSD", "display":"15.6\" FHD 165Hz", "os":"Windows 11"}',
29990000, NULL, 32, '893123471016', TRUE, NOW(), NOW());


-- ==============================
-- 💼 BUSINESS LAPTOPS
-- Category: Business Laptop
-- Category ID: c2222222-0001-0000-0000-000000000002
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000081', 'Dell XPS 13 Plus', 'Ultrabook cao cấp i7 Gen 13, màn hình 13.4" 4K OLED, thiết kế siêu mỏng nhẹ, pin 12 giờ.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-dell-xps13.png', NOW()),
('11111111-0000-0000-0000-000000000082', 'Lenovo ThinkPad X1 Carbon Gen 11', 'Laptop doanh nghiệp chuyên nghiệp, i7 Gen 13, màn hình 14" 2.8K, bàn phím tốt nhất, chuẩn MIL-STD.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-lenovo-x1carbon.png', NOW()),
('11111111-0000-0000-0000-000000000083', 'ASUS Zenbook 14 OLED', 'Laptop văn phòng cao cấp i7-1355U, màn hình 14" 2.8K OLED, siêu mỏng 1.39kg, pin 18 giờ.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-asus-zenbook14.png', NOW()),
('11111111-0000-0000-0000-000000000084', 'Dell Latitude 7440', 'Laptop doanh nghiệp an toàn, i7 Gen 13, màn hình 14" FHD+, bảo mật vân tay + face ID.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-dell-latitude7440.png', NOW()),
('11111111-0000-0000-0000-000000000085', 'Acer Swift 3 OLED', 'Laptop văn phòng giá tốt i5 Gen 13, màn hình 14" 2.8K OLED, nhẹ 1.25kg, pin 12 giờ.', 'a1b2c3d4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-acer-swift3.png', NOW()),
('11111111-0000-0000-0000-000000000086', 'Lenovo ThinkBook 14 G6', 'Laptop doanh nghiệp tầm trung i5-1335U, màn hình 14" WUXGA, camera FHD với Privacy Shutter.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-lenovo-thinkbook14.png', NOW()),
('11111111-0000-0000-0000-000000000087', 'ASUS ExpertBook B9', 'Laptop doanh nghiệp siêu nhẹ 880g, i7-1355U, màn hình 14" FHD, pin 24 giờ, chuẩn MIL-STD.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000002', 'http://103.90.225.90:8084/files/laptop-asus-expertbook-b9.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
-- Dell XPS 13 Plus
(gen_random_uuid(), '11111111-0000-0000-0000-000000000081', 'http://103.90.225.90:8084/files/laptop-dell-xps13.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000081', 'http://103.90.225.90:8084/files/laptop-dell-xps13-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000081', 'http://103.90.225.90:8084/files/laptop-dell-xps13-2.png', FALSE),
-- ThinkPad X1 Carbon
(gen_random_uuid(), '11111111-0000-0000-0000-000000000082', 'http://103.90.225.90:8084/files/laptop-lenovo-x1carbon.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000082', 'http://103.90.225.90:8084/files/laptop-lenovo-x1carbon-1.png', FALSE),
-- Zenbook 14 OLED
(gen_random_uuid(), '11111111-0000-0000-0000-000000000083', 'http://103.90.225.90:8084/files/laptop-asus-zenbook14.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000083', 'http://103.90.225.90:8084/files/laptop-asus-zenbook14-1.png', FALSE),
-- Dell Latitude 7440
(gen_random_uuid(), '11111111-0000-0000-0000-000000000084', 'http://103.90.225.90:8084/files/laptop-dell-latitude7440.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000084', 'http://103.90.225.90:8084/files/laptop-dell-latitude7440-1.png', FALSE),
-- Acer Swift 3
(gen_random_uuid(), '11111111-0000-0000-0000-000000000085', 'http://103.90.225.90:8084/files/laptop-acer-swift3.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000085', 'http://103.90.225.90:8084/files/laptop-acer-swift3-1.png', FALSE),
-- ThinkBook 14
(gen_random_uuid(), '11111111-0000-0000-0000-000000000086', 'http://103.90.225.90:8084/files/laptop-lenovo-thinkbook14.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000086', 'http://103.90.225.90:8084/files/laptop-lenovo-thinkbook14-1.png', FALSE),
-- ExpertBook B9
(gen_random_uuid(), '11111111-0000-0000-0000-000000000087', 'http://103.90.225.90:8084/files/laptop-asus-expertbook-b9.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000087', 'http://103.90.225.90:8084/files/laptop-asus-expertbook-b9-1.png', FALSE);

-- SKUs for Dell XPS 13 Plus
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000081', 'XPS13-I7-4K-32GB',
'{"cpu":"Intel Core i7-1360P", "gpu":"Intel Iris Xe", "ram":"32GB LPDDR5", "storage":"1TB NVMe SSD", "display":"13.4\" UHD+ OLED Touch", "os":"Windows 11 Pro", "weight":"1.24kg"}',
52990000, NULL, 12, '893123481001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000081', 'XPS13-I7-FHD-16GB',
'{"cpu":"Intel Core i7-1360P", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"13.4\" FHD+ Touch", "os":"Windows 11 Pro", "weight":"1.24kg"}',
42990000, NULL, 18, '893123481002', TRUE, NOW(), NOW());

-- SKUs for ThinkPad X1 Carbon Gen 11
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000082', 'X1CARBON-I7-2.8K-32GB',
'{"cpu":"Intel Core i7-1365U", "gpu":"Intel Iris Xe", "ram":"32GB LPDDR5", "storage":"1TB NVMe SSD", "display":"14\" 2.8K OLED Touch", "os":"Windows 11 Pro", "weight":"1.12kg", "mil_std":"810H"}',
59990000, NULL, 10, '893123481003', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000082', 'X1CARBON-I5-WUXGA-16GB',
'{"cpu":"Intel Core i5-1335U", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" WUXGA", "os":"Windows 11 Pro", "weight":"1.12kg", "mil_std":"810H"}',
44990000, NULL, 15, '893123481004', TRUE, NOW(), NOW());

-- SKUs for ASUS Zenbook 14 OLED
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000083', 'ZENBOOK14-I7-2.8K-16GB',
'{"cpu":"Intel Core i7-1355U", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" 2.8K OLED", "os":"Windows 11", "weight":"1.39kg"}',
29990000, NULL, 25, '893123481005', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000083', 'ZENBOOK14-I5-FHD-16GB',
'{"cpu":"Intel Core i5-1335U", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" FHD OLED", "os":"Windows 11", "weight":"1.39kg"}',
24990000, NULL, 30, '893123481006', TRUE, NOW(), NOW());

-- SKUs for Dell Latitude 7440
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000084', 'LATITUDE7440-I7-FHD-32GB',
'{"cpu":"Intel Core i7-1365U", "gpu":"Intel Iris Xe", "ram":"32GB DDR5", "storage":"1TB NVMe SSD", "display":"14\" FHD+", "os":"Windows 11 Pro", "security":"Fingerprint + IR Camera"}',
39990000, NULL, 20, '893123481007', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000084', 'LATITUDE7440-I5-FHD-16GB',
'{"cpu":"Intel Core i5-1345U", "gpu":"Intel Iris Xe", "ram":"16GB DDR5", "storage":"512GB NVMe SSD", "display":"14\" FHD+", "os":"Windows 11 Pro", "security":"Fingerprint + IR Camera"}',
29990000, NULL, 28, '893123481008', TRUE, NOW(), NOW());

-- SKUs for Acer Swift 3 OLED
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000085', 'SWIFT3-I5-2.8K-16GB',
'{"cpu":"Intel Core i5-1340P", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" 2.8K OLED", "os":"Windows 11", "weight":"1.25kg"}',
21990000, NULL, 35, '893123481009', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000085', 'SWIFT3-I7-2.8K-16GB',
'{"cpu":"Intel Core i7-1355U", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" 2.8K OLED", "os":"Windows 11", "weight":"1.25kg"}',
25990000, NULL, 30, '893123481010', TRUE, NOW(), NOW());

-- SKUs for Lenovo ThinkBook 14 G6
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000086', 'THINKBOOK14-I5-WUXGA-16GB',
'{"cpu":"Intel Core i5-1335U", "gpu":"Intel Iris Xe", "ram":"16GB DDR4", "storage":"512GB NVMe SSD", "display":"14\" WUXGA", "os":"Windows 11", "camera":"FHD with Privacy Shutter"}',
19990000, NULL, 40, '893123481011', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000086', 'THINKBOOK14-I7-WUXGA-16GB',
'{"cpu":"Intel Core i7-1355U", "gpu":"Intel Iris Xe", "ram":"16GB DDR4", "storage":"512GB NVMe SSD", "display":"14\" WUXGA", "os":"Windows 11", "camera":"FHD with Privacy Shutter"}',
23990000, NULL, 32, '893123481012', TRUE, NOW(), NOW());

-- SKUs for ASUS ExpertBook B9
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000087', 'EXPERTBOOK-B9-I7-FHD-32GB',
'{"cpu":"Intel Core i7-1355U", "gpu":"Intel Iris Xe", "ram":"32GB LPDDR5", "storage":"1TB NVMe SSD", "display":"14\" FHD", "os":"Windows 11 Pro", "weight":"0.88kg", "mil_std":"810H"}',
49990000, NULL, 8, '893123481013', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000087', 'EXPERTBOOK-B9-I5-FHD-16GB',
'{"cpu":"Intel Core i5-1335U", "gpu":"Intel Iris Xe", "ram":"16GB LPDDR5", "storage":"512GB NVMe SSD", "display":"14\" FHD", "os":"Windows 11 Pro", "weight":"0.88kg", "mil_std":"810H"}',
36990000, NULL, 12, '893123481014', TRUE, NOW(), NOW());


-- ============================================
-- ✅ COMPLETED: Laptop Products Migration
-- Total Products: 15 (8 Gaming + 7 Business)
-- Total SKUs: 31
-- ============================================
