-- ============================================
-- COMPREHENSIVE SAMPLE DATA FOR E-COMMERCE
-- 8 Brands, Each with ~10 Products
-- Each Product has 2-4 SKU Variants
-- Total: ~80 Products, ~200 SKUs
-- ============================================

-- ==================== BRANDS ====================
INSERT INTO brand (id, name, slug, logo_url) VALUES
                                                 ('a1b2c3d4-1111-1111-1111-111111111111', 'Dell', 'dell', 'http://103.90.225.90:8084/files/6a5ee4ff-1161-4a86-b96f-c04b576f9a58.png'),
                                                 ('a1b2c3d4-2222-2222-2222-222222222222', 'HP', 'hp', 'http://103.90.225.90:8084/files/8a3801db-0541-49fa-ac37-99dfedfaf54e.png'),
                                                 ('a1b2c3d4-3333-3333-3333-333333333333', 'Asus', 'asus', 'http://103.90.225.90:8084/files/dd242b91-e96a-4e58-8c8f-7c2d1cb03a8b.png'),
                                                 ('a1b2c3d4-4444-4444-4444-444444444444', 'MSI', 'msi', 'http://103.90.225.90:8084/files/eb89dc15-4062-492f-9381-cc88f1e29502.png'),
                                                 ('a1b2c3d4-5555-5555-5555-555555555555', 'Corsair', 'corsair', 'http://103.90.225.90:8084/files/efb3db53-3e97-4823-aa1a-c1c7f12fa0ff.png'),
                                                 ('a1b2c3d4-6666-6666-6666-666666666666', 'Kingston', 'kingston', 'http://103.90.225.90:8084/files/eec0d072-6e37-4a9a-ac31-dfd0e898647e.png'),
                                                 ('a1b2c3d4-7777-7777-7777-777777777777', 'Samsung', 'samsung', 'http://103.90.225.90:8084/files/59439743-7fb3-4426-a1b1-6b4b251a8c64.png'),
                                                 ('a1b2c3d4-8888-8888-8888-888888888888', 'Logitech', 'logitech', 'http://103.90.225.90:8084/files/1bc16683-97de-4d35-92c6-96fbac188317.png'),
                                                 ('a1b2c3d4-9999-9999-9999-999999999999', 'Lenovo', 'lenovo', 'http://103.90.225.90:8084/files/23719dd5-a04c-4635-9cf1-62668e84066d.png'),
                                                 ('a1b2c3d4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Acer', 'acer', 'http://103.90.225.90:8084/files/d78a7b8a-8ed6-4139-81e7-01513638afcd.png');

-- ==================== CATEGORIES ====================
-- ROOT CATEGORIES
INSERT INTO category (id, name, slug, image_url, parent_id) VALUES
                                                                ('c1111111-0000-0000-0000-000000000001', 'Laptop', 'laptop', 'http://103.90.225.90:8084/files/c86070ed-2ed5-44bb-9be5-45aee1195fe7.png', NULL),
                                                                ('c1111111-0000-0000-0000-000000000002', 'PC', 'desktop-pc', 'http://103.90.225.90:8084/files/a8b99339-c0dd-45d2-b069-33ef017e8343.png', NULL),
                                                                ('c1111111-0000-0000-0000-000000000003', 'Linh kiện PC', 'pc-components', 'http://103.90.225.90:8084/files/cc8e95d8-4e91-403e-b4c0-eb9a2fe179c6.png', NULL),
                                                                ('c1111111-0000-0000-0000-000000000004', 'Phụ kiện', 'peripherals', 'http://103.90.225.90:8084/files/fb34a7f5-83b8-4c53-a3f7-603b061d032e.png', NULL);

-- LAPTOP SUBCATEGORIES
INSERT INTO category (id, name, slug, image_url, parent_id) VALUES
                                                                ('c2222222-0001-0000-0000-000000000001', 'Gaming Laptop', 'gaming-laptop', NULL, 'c1111111-0000-0000-0000-000000000001'),
                                                                ('c2222222-0001-0000-0000-000000000002', 'Business Laptop', 'business-laptop', NULL, 'c1111111-0000-0000-0000-000000000001'),
                                                                ('c2222222-0001-0000-0000-000000000003', 'Ultrabook', 'ultrabook', NULL, 'c1111111-0000-0000-0000-000000000001');

-- PC COMPONENTS SUBCATEGORIES
INSERT INTO category (id, name, slug, image_url, parent_id) VALUES
                                                                ('c2222222-0003-0000-0000-000000000001', 'CPU', 'cpu', NULL, 'c1111111-0000-0000-0000-000000000003'),
                                                                ('c2222222-0003-0000-0000-000000000002', 'RAM', 'ram', NULL, 'c1111111-0000-0000-0000-000000000003'),
                                                                ('c2222222-0003-0000-0000-000000000003', 'SSD', 'ssd', NULL, 'c1111111-0000-0000-0000-000000000003'),
                                                                ('c2222222-0003-0000-0000-000000000004', 'Graphics Card', 'graphics-card', NULL, 'c1111111-0000-0000-0000-000000000003');

-- PERIPHERALS SUBCATEGORIES
INSERT INTO category (id, name, slug, image_url, parent_id) VALUES
                                                                ('c2222222-0004-0000-0000-000000000001', 'Keyboard', 'keyboard', NULL, 'c1111111-0000-0000-0000-000000000004'),
                                                                ('c2222222-0004-0000-0000-000000000002', 'Mouse', 'mouse', NULL, 'c1111111-0000-0000-0000-000000000004'),
                                                                ('c2222222-0004-0000-0000-000000000003', 'Headset', 'headset', NULL, 'c1111111-0000-0000-0000-000000000004');




-- ==============================
-- 🎮 GAMING LAPTOPS
-- Category: Gaming Laptop
-- Category ID: c2222222-0001-0000-0000-000000000001
-- ==============================

-- ==============================
-- PRODUCTS
-- ==============================
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at)
VALUES
    ('11111111-0000-0000-0000-000000000001', 'ASUS TUF Gaming A15', 'Laptop gaming hiệu năng cao với CPU Ryzen và GPU RTX, thiết kế bền bỉ chuẩn quân đội.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/5dac08c9-09f4-4374-a272-c743b5262e30.png', NOW()),
    ('11111111-0000-0000-0000-000000000002', 'MSI Katana 15', 'Laptop gaming mạnh mẽ, card đồ họa RTX 4060, tối ưu cho game thủ chuyên nghiệp.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/d2c52469-f79f-41e1-9227-eef9ce3a49c2.png', NOW()),
    ('11111111-0000-0000-0000-000000000003', 'Lenovo Legion 5 Pro', 'Máy tính xách tay gaming hiệu năng cao với màn hình QHD 165Hz và hệ thống tản nhiệt Coldfront.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/5acbea9c-4b7a-4be2-87b0-5e33fa3a6c7e.png', NOW()),
    ('11111111-0000-0000-0000-000000000004', 'Acer Nitro 5', 'Laptop gaming phổ thông với giá cả phải chăng, phù hợp sinh viên và game thủ casual.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/818d3292-4e30-4bc3-8cb7-06946de74a15.png', NOW()),
    ('11111111-0000-0000-0000-000000000005', 'Dell G15', 'Laptop gaming mạnh mẽ từ Dell, trang bị RTX 4050, hiệu năng bền bỉ.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000001', 'http://103.90.225.90:8084/files/1fade80b-5ebc-48cc-9774-88be3f0accd6.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/5dac08c9-09f4-4374-a272-c743b5262e30.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/d2c52469-f79f-41e1-9227-eef9ce3a49c2.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/5acbea9c-4b7a-4be2-87b0-5e33fa3a6c7e.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/818d3292-4e30-4bc3-8cb7-06946de74a15.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/1fade80b-5ebc-48cc-9774-88be3f0accd6.png', TRUE);



-- ==============================
-- SKUs
-- ==============================
-- ==============================
-- SKUs (Đúng cấu trúc bảng hiện tại)
-- ==============================

-- ASUS TUF Gaming A15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'ASUS-TUF-R7-4060',
    '{"cpu":"Ryzen 7 7735HS", "ram":"16GB", "storage":"512GB SSD", "gpu":"RTX 4060"}',
    26990000, NULL, 15, '893123456001', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'ASUS-TUF-R5-4050',
    '{"cpu":"Ryzen 5 7535HS", "ram":"8GB", "storage":"512GB SSD", "gpu":"RTX 4050"}',
    23990000, NULL, 20, '893123456002', TRUE, NOW(), NOW());

-- MSI Katana 15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'MSI-KATANA-I7-4060',
     '{"cpu":"Core i7-13620H", "ram":"16GB", "storage":"1TB SSD", "gpu":"RTX 4060"}',
     29990000, NULL, 10, '893123456003', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'MSI-KATANA-I5-4050',
     '{"cpu":"Core i5-13420H", "ram":"8GB", "storage":"512GB SSD", "gpu":"RTX 4050"}',
     25990000, NULL, 15, '893123456004', TRUE, NOW(), NOW());

-- Lenovo Legion 5 Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'LEGION5-R7-4070',
     '{"cpu":"Ryzen 7 7840HS", "ram":"16GB", "storage":"1TB SSD", "gpu":"RTX 4070"}',
     36990000, NULL, 8, '893123456005', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'LEGION5-R5-4060',
     '{"cpu":"Ryzen 5 7640HS", "ram":"16GB", "storage":"512GB SSD", "gpu":"RTX 4060"}',
     31990000, NULL, 12, '893123456006', TRUE, NOW(), NOW());

-- Acer Nitro 5
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'NITRO5-I5-4050',
     '{"cpu":"Core i5-13420H", "ram":"8GB", "storage":"512GB SSD", "gpu":"RTX 4050"}',
     22990000, NULL, 25, '893123456007', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'NITRO5-I7-4060',
     '{"cpu":"Core i7-13620H", "ram":"16GB", "storage":"1TB SSD", "gpu":"RTX 4060"}',
     26990000, NULL, 10, '893123456008', TRUE, NOW(), NOW());

-- Dell G15
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'DELLG15-I7-4060',
     '{"cpu":"Core i7-13650HX", "ram":"16GB", "storage":"1TB SSD", "gpu":"RTX 4060"}',
     30990000, NULL, 9, '893123456009', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'DELLG15-I5-4050',
     '{"cpu":"Core i5-13450HX", "ram":"8GB", "storage":"512GB SSD", "gpu":"RTX 4050"}',
     26990000, NULL, 14, '893123456010', TRUE, NOW(), NOW());



-- ==============================
-- 🧳 BUSINESS LAPTOPS
-- Category ID: c2222222-0001-0000-0000-000000000002
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
                                                                                                  ('11111111-0000-0000-0000-000000000006', 'Dell Latitude 5540', 'Laptop doanh nhân cao cấp, hiệu suất ổn định, pin lâu, độ bền vượt trội.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000002', 'https://example.com/products/dell-latitude-5540.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000007', 'HP EliteBook 840 G10', 'Thiết kế mỏng nhẹ, bảo mật cao cấp, hướng tới người dùng doanh nghiệp.', 'a1b2c3d4-2222-2222-2222-222222222222', 'c2222222-0001-0000-0000-000000000002', 'https://example.com/products/hp-elitebook-840.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000008', 'Lenovo ThinkPad X13', 'Laptop doanh nhân chuẩn mực với bàn phím trứ danh và độ bền đạt chuẩn MIL-STD.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000002', 'https://example.com/products/thinkpad-x13.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000009', 'ASUS ExpertBook B5', 'Laptop doanh nghiệp siêu nhẹ, pin cực lâu, độ bền đạt chuẩn quân đội.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000002', 'https://example.com/products/asus-expertbook-b5.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000010', 'Acer TravelMate P4', 'Laptop văn phòng ổn định, bảo mật cao, thiết kế chuyên nghiệp.', 'a1b2c3d4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2222222-0001-0000-0000-000000000002', 'https://example.com/products/acer-travelmate-p4.jpg', NOW());


INSERT INTO product_image (id, product_id, url, is_primary)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/f2977c71-a191-43c2-b756-eb286349ff93.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/c43110b1-d172-45b2-a7a7-ef4c2f1dd155.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/f487dce4-ce68-47b0-946b-eb29b104973d.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/31cebb20-5d9a-4846-8b5b-05da91e5687b.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/81e52765-5a67-42b9-91f3-24ef12852477.png', TRUE);


-- SKUs
-- Dell Latitude 5540
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'LAT5540-I5-1335U',
     '{"cpu":"Core i5-1335U", "ram":"16GB", "storage":"512GB SSD", "gpu":"Iris Xe"}',
     25990000, NULL, 20, '893123456101', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'LAT5540-I7-1355U',
     '{"cpu":"Core i7-1355U", "ram":"16GB", "storage":"1TB SSD", "gpu":"Iris Xe"}',
     28990000, NULL, 10, '893123456102', TRUE, NOW(), NOW());

-- HP EliteBook 840 G10
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'HP840G10-I5-1335U',
     '{"cpu":"Core i5-1335U", "ram":"16GB", "storage":"512GB SSD", "os":"Windows 11 Pro"}',
     27990000, NULL, 15, '893123456103', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'HP840G10-I7-1355U',
     '{"cpu":"Core i7-1355U", "ram":"32GB", "storage":"1TB SSD", "os":"Windows 11 Pro"}',
     31990000, NULL, 8, '893123456104', TRUE, NOW(), NOW());

-- Lenovo ThinkPad X13
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'THINKX13-R5-6650U',
     '{"cpu":"Ryzen 5 PRO 6650U", "ram":"16GB", "storage":"512GB SSD"}',
     24990000, NULL, 12, '893123456105', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'THINKX13-R7-6850U',
     '{"cpu":"Ryzen 7 PRO 6850U", "ram":"32GB", "storage":"1TB SSD"}',
     28990000, NULL, 9, '893123456106', TRUE, NOW(), NOW());

-- ASUS ExpertBook B5
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'B5-I5-1340P',
     '{"cpu":"Core i5-1340P", "ram":"16GB", "storage":"512GB SSD"}',
     23990000, NULL, 18, '893123456107', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'B5-I7-1360P',
     '{"cpu":"Core i7-1360P", "ram":"32GB", "storage":"1TB SSD"}',
     27990000, NULL, 10, '893123456108', TRUE, NOW(), NOW());

-- Acer TravelMate P4
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'P4-I5-1335U',
     '{"cpu":"Core i5-1335U", "ram":"8GB", "storage":"512GB SSD"}',
     19990000, NULL, 25, '893123456109', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'P4-I7-1355U',
     '{"cpu":"Core i7-1355U", "ram":"16GB", "storage":"1TB SSD"}',
     23990000, NULL, 15, '893123456110', TRUE, NOW(), NOW());



-- ==============================
-- 💼 ULTRABOOKS
-- Category ID: c2222222-0001-0000-0000-000000000003
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
                                                                                                  ('11111111-0000-0000-0000-000000000011', 'Dell XPS 13 Plus', 'Ultrabook cao cấp với viền siêu mỏng, hiệu năng mạnh mẽ, thiết kế tinh tế.', 'a1b2c3d4-1111-1111-1111-111111111111', 'c2222222-0001-0000-0000-000000000003', 'https://example.com/products/dell-xps-13-plus.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000012', 'ASUS ZenBook 14 OLED', 'Ultrabook sang trọng, màn OLED rực rỡ, trọng lượng siêu nhẹ.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0001-0000-0000-000000000003', 'https://example.com/products/asus-zenbook-14-oled.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000013', 'HP Spectre x360', 'Ultrabook 2-trong-1 với màn hình cảm ứng, xoay gập 360°, pin trâu.', 'a1b2c3d4-2222-2222-2222-222222222222', 'c2222222-0001-0000-0000-000000000003', 'https://example.com/products/hp-spectre-x360.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000014', 'Lenovo Yoga Slim 7i', 'Ultrabook mỏng nhẹ, hiệu năng mạnh, pin lâu, màn hình 2.8K.', 'a1b2c3d4-9999-9999-9999-999999999999', 'c2222222-0001-0000-0000-000000000003', 'https://example.com/products/yoga-slim-7i.jpg', NOW()),
                                                                                                  ('11111111-0000-0000-0000-000000000015', 'Acer Swift Go 14 OLED', 'Ultrabook OLED, nhẹ chỉ 1.25kg, hiệu năng vượt trội với Intel Gen 13.', 'a1b2c3d4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2222222-0001-0000-0000-000000000003', 'https://example.com/products/acer-swift-go-14.jpg', NOW());

INSERT INTO product_image (id, product_id, url, is_primary)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/0c324548-e657-4f5d-9eb0-2b55e22884bf.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/1bec5bb1-e1a2-4390-9542-589f57474d6e.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/ef0e7574-dc7c-4f93-ba8f-b91812993566.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/53d1f7ad-76b4-43e3-90f2-ce76e398288d.png', TRUE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/a1aa98df-8614-4e3f-9e7b-82ad52764dc6.png', TRUE);

-- SKUs for Dell XPS 13 Plus
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'XPS13-I5-16-512',
     '{"cpu":"Core i5-1340P", "ram":"16GB", "storage":"512GB SSD", "gpu":"Iris Xe"}',
     34990000, NULL, 20, '893123456101', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'XPS13-I7-32-1T',
     '{"cpu":"Core i7-1360P", "ram":"32GB", "storage":"1TB SSD", "gpu":"Iris Xe"}',
     41990000, NULL, 10, '893123456102', TRUE, NOW(), NOW());

-- SKUs for ASUS ZenBook 14 OLED
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'ZEN14-I5-16-512',
     '{"cpu":"Core i5-1340P", "ram":"16GB", "storage":"512GB SSD", "display":"14 OLED 2.8K"}',
     27990000, NULL, 25, '893123456103', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'ZEN14-I7-32-1T',
     '{"cpu":"Core i7-1360P", "ram":"32GB", "storage":"1TB SSD", "display":"14 OLED 2.8K"}',
     31990000, NULL, 12, '893123456104', TRUE, NOW(), NOW());

-- SKUs for HP Spectre x360
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'SPECTRE-I5-16-512',
     '{"cpu":"Core i5-1335U", "ram":"16GB", "storage":"512GB SSD", "display":"Touch 13.5 FHD", "convertible":true}',
     31990000, NULL, 18, '893123456105', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'SPECTRE-I7-32-1T',
     '{"cpu":"Core i7-1355U", "ram":"32GB", "storage":"1TB SSD", "display":"Touch 13.5 2.8K", "convertible":true}',
     36990000, NULL, 10, '893123456106', TRUE, NOW(), NOW());

-- SKUs for Lenovo Yoga Slim 7i
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'YOGA7I-I5-16-512',
     '{"cpu":"Core i5-1340P", "ram":"16GB", "storage":"512GB SSD", "display":"14 2.8K"}',
     25990000, NULL, 22, '893123456107', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'YOGA7I-I7-32-1T',
     '{"cpu":"Core i7-1360P", "ram":"32GB", "storage":"1TB SSD", "display":"14 2.8K"}',
     29990000, NULL, 14, '893123456108', TRUE, NOW(), NOW());

-- SKUs for Acer Swift Go 14 OLED
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'SWIFT14-I5-16-512',
     '{"cpu":"Core i5-1340P", "ram":"16GB", "storage":"512GB SSD", "display":"14 OLED 2.8K"}',
     23990000, NULL, 28, '893123456109', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'SWIFT14-I7-32-1T',
     '{"cpu":"Core i7-1360P", "ram":"32GB", "storage":"1TB SSD", "display":"14 OLED 2.8K"}',
     28990000, NULL, 12, '893123456110', TRUE, NOW(), NOW());


