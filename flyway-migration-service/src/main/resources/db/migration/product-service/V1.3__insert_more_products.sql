-- ============================================
-- ADDITIONAL SAMPLE DATA FOR E-COMMERCE
-- Peripherals & Components
-- ============================================

-- ==============================
-- 🎧 HEADSETS
-- Category: Headset
-- Category ID: c2222222-0004-0000-0000-000000000003
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000021', 'Logitech G733 LIGHTSPEED', 'Tai nghe gaming không dây, âm thanh vòm 7.1, đèn RGB, pin 29 giờ.', 'a1b2c3d4-8888-8888-8888-888888888888', 'c2222222-0004-0000-0000-000000000003', 'http://103.90.225.90:8084/files/headset-logitech-g733.png', NOW()),
('11111111-0000-0000-0000-000000000022', 'Corsair HS80 RGB Wireless', 'Tai nghe gaming cao cấp, âm thanh Dolby Atmos, kết nối đa nền tảng.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0004-0000-0000-000000000003', 'http://103.90.225.90:8084/files/headset-corsair-hs80.png', NOW()),
('11111111-0000-0000-0000-000000000023', 'ASUS ROG Delta S', 'Tai nghe gaming chuyên nghiệp, MQA-Master, ESS Quad-DAC, Hi-Res Audio.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0004-0000-0000-000000000003', 'http://103.90.225.90:8084/files/headset-asus-rog-delta.png', NOW()),
('11111111-0000-0000-0000-000000000024', 'MSI Immerse GH50 Wireless', 'Tai nghe gaming không dây với RGB Mystic Light, âm thanh 7.1.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0004-0000-0000-000000000003', 'http://103.90.225.90:8084/files/headset-msi-gh50.png', NOW()),
('11111111-0000-0000-0000-000000000025', 'Kingston HyperX Cloud III', 'Tai nghe gaming thoải mái, âm thanh rõ ràng, micro chống ồn.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0004-0000-0000-000000000003', 'http://103.90.225.90:8084/files/headset-hyperx-cloud3.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000021', 'http://103.90.225.90:8084/files/headset-logitech-g733.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000021', 'http://103.90.225.90:8084/files/headset-logitech-g733-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000022', 'http://103.90.225.90:8084/files/headset-corsair-hs80.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000022', 'http://103.90.225.90:8084/files/headset-corsair-hs80-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000023', 'http://103.90.225.90:8084/files/headset-asus-rog-delta.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000023', 'http://103.90.225.90:8084/files/headset-asus-rog-delta-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000024', 'http://103.90.225.90:8084/files/headset-msi-gh50.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000024', 'http://103.90.225.90:8084/files/headset-msi-gh50-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000025', 'http://103.90.225.90:8084/files/headset-hyperx-cloud3.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000025', 'http://103.90.225.90:8084/files/headset-hyperx-cloud3-1.png', FALSE);

-- SKUs for Logitech G733
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000021', 'G733-WHITE',
'{"color":"White", "connectivity":"Wireless", "audio":"7.1 Surround", "battery":"29 hours"}',
3290000, NULL, 30, '893123457001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000021', 'G733-BLACK',
'{"color":"Black", "connectivity":"Wireless", "audio":"7.1 Surround", "battery":"29 hours"}',
3290000, NULL, 25, '893123457002', TRUE, NOW(), NOW());

-- SKUs for Corsair HS80
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000022', 'HS80-RGB-WIRELESS',
'{"connectivity":"Wireless 2.4GHz", "audio":"Dolby Atmos", "battery":"20 hours", "rgb":true}',
3790000, NULL, 20, '893123457003', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000022', 'HS80-WIRED',
'{"connectivity":"USB Wired", "audio":"Dolby Atmos", "rgb":true}',
2790000, NULL, 28, '893123457004', TRUE, NOW(), NOW());

-- SKUs for ASUS ROG Delta S
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000023', 'DELTA-S-WIRED',
'{"connectivity":"USB-C", "audio":"Hi-Res ESS Quad-DAC", "mic":"AI Noise Cancelling"}',
4290000, NULL, 15, '893123457005', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000023', 'DELTA-S-WIRELESS',
'{"connectivity":"2.4GHz + Bluetooth", "audio":"Hi-Res ESS Quad-DAC", "battery":"25 hours"}',
5290000, NULL, 10, '893123457006', TRUE, NOW(), NOW());

-- SKUs for MSI Immerse GH50
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000024', 'GH50-WIRELESS',
'{"connectivity":"Wireless 2.4GHz", "audio":"7.1 Virtual Surround", "rgb":"Mystic Light"}',
2990000, NULL, 22, '893123457007', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000024', 'GH50-WIRED',
'{"connectivity":"USB", "audio":"7.1 Virtual Surround", "rgb":"Mystic Light"}',
2290000, NULL, 30, '893123457008', TRUE, NOW(), NOW());

-- SKUs for HyperX Cloud III
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000025', 'CLOUD3-WIRED',
'{"connectivity":"3.5mm + USB", "audio":"53mm Drivers", "mic":"Detachable"}',
2490000, NULL, 35, '893123457009', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000025', 'CLOUD3-WIRELESS',
'{"connectivity":"Wireless 2.4GHz", "audio":"53mm Drivers", "battery":"120 hours"}',
3490000, NULL, 18, '893123457010', TRUE, NOW(), NOW());


-- ==============================
-- ⌨️ KEYBOARDS
-- Category: Keyboard
-- Category ID: c2222222-0004-0000-0000-000000000001
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000031', 'Logitech G Pro X', 'Bàn phím cơ gaming chuyên nghiệp, switch hot-swap, RGB đầy đủ.', 'a1b2c3d4-8888-8888-8888-888888888888', 'c2222222-0004-0000-0000-000000000001', 'http://103.90.225.90:8084/files/keyboard-logitech-gprox.png', NOW()),
('11111111-0000-0000-0000-000000000032', 'Corsair K70 RGB Pro', 'Bàn phím cơ cao cấp, khung nhôm, switch Cherry MX, RGB per-key.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0004-0000-0000-000000000001', 'http://103.90.225.90:8084/files/keyboard-corsair-k70.png', NOW()),
('11111111-0000-0000-0000-000000000033', 'ASUS ROG Strix Scope RX', 'Bàn phím cơ gaming với ROG RX Optical Switch, phản hồi cực nhanh.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0004-0000-0000-000000000001', 'http://103.90.225.90:8084/files/keyboard-asus-rog-strix.png', NOW()),
('11111111-0000-0000-0000-000000000034', 'MSI Vigor GK71 Sonic', 'Bàn phím cơ gaming với Cherry MX Switch, thiết kế RGB đỉnh cao.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0004-0000-0000-000000000001', 'http://103.90.225.90:8084/files/keyboard-msi-gk71.png', NOW()),
('11111111-0000-0000-0000-000000000035', 'Kingston HyperX Alloy Origins', 'Bàn phím cơ compact, HyperX Red Switch, khung thép bền bỉ.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0004-0000-0000-000000000001', 'http://103.90.225.90:8084/files/keyboard-hyperx-origins.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000031', 'http://103.90.225.90:8084/files/keyboard-logitech-gprox.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000031', 'http://103.90.225.90:8084/files/keyboard-logitech-gprox-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000032', 'http://103.90.225.90:8084/files/keyboard-corsair-k70.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000032', 'http://103.90.225.90:8084/files/keyboard-corsair-k70-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000033', 'http://103.90.225.90:8084/files/keyboard-asus-rog-strix.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000033', 'http://103.90.225.90:8084/files/keyboard-asus-rog-strix-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000034', 'http://103.90.225.90:8084/files/keyboard-msi-gk71.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000034', 'http://103.90.225.90:8084/files/keyboard-msi-gk71-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000035', 'http://103.90.225.90:8084/files/keyboard-hyperx-origins.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000035', 'http://103.90.225.90:8084/files/keyboard-hyperx-origins-1.png', FALSE);

-- SKUs for Logitech G Pro X
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000031', 'GPROX-CLICKY',
'{"switch":"GX Blue Clicky", "layout":"TKL", "rgb":true, "hotswap":true}',
3490000, NULL, 20, '893123458001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000031', 'GPROX-LINEAR',
'{"switch":"GX Red Linear", "layout":"TKL", "rgb":true, "hotswap":true}',
3490000, NULL, 25, '893123458002', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000031', 'GPROX-TACTILE',
'{"switch":"GX Brown Tactile", "layout":"TKL", "rgb":true, "hotswap":true}',
3490000, NULL, 22, '893123458003', TRUE, NOW(), NOW());

-- SKUs for Corsair K70 RGB Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000032', 'K70-MX-RED',
'{"switch":"Cherry MX Red", "layout":"Full Size", "frame":"Aluminum", "rgb":"Per-Key"}',
3990000, NULL, 18, '893123458004', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000032', 'K70-MX-SPEED',
'{"switch":"Cherry MX Speed", "layout":"Full Size", "frame":"Aluminum", "rgb":"Per-Key"}',
4290000, NULL, 15, '893123458005', TRUE, NOW(), NOW());

-- SKUs for ASUS ROG Strix Scope RX
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000033', 'STRIX-RX-OPTICAL-RED',
'{"switch":"ROG RX Red Optical", "layout":"Full Size", "response":"0.2ms"}',
3790000, NULL, 16, '893123458006', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000033', 'STRIX-RX-OPTICAL-BLUE',
'{"switch":"ROG RX Blue Optical", "layout":"Full Size", "response":"0.2ms"}',
3790000, NULL, 14, '893123458007', TRUE, NOW(), NOW());

-- SKUs for MSI Vigor GK71
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000034', 'GK71-SONIC-RED',
'{"switch":"Cherry MX Red", "layout":"Full Size", "rgb":"Mystic Light"}',
2990000, NULL, 20, '893123458008', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000034', 'GK71-SONIC-BROWN',
'{"switch":"Cherry MX Brown", "layout":"Full Size", "rgb":"Mystic Light"}',
2990000, NULL, 18, '893123458009', TRUE, NOW(), NOW());

-- SKUs for HyperX Alloy Origins
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000035', 'ORIGINS-RED',
'{"switch":"HyperX Red", "layout":"Full Size", "frame":"Steel"}',
2790000, NULL, 28, '893123458010', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000035', 'ORIGINS-AQUA',
'{"switch":"HyperX Aqua", "layout":"Full Size", "frame":"Steel"}',
2790000, NULL, 25, '893123458011', TRUE, NOW(), NOW());


-- ==============================
-- 🖱️ MICE
-- Category: Mouse
-- Category ID: c2222222-0004-0000-0000-000000000002
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000041', 'Logitech G Pro X Superlight', 'Chuột gaming không dây siêu nhẹ (63g), sensor HERO 25K, pin 70 giờ.', 'a1b2c3d4-8888-8888-8888-888888888888', 'c2222222-0004-0000-0000-000000000002', 'http://103.90.225.90:8084/files/mouse-logitech-superlight.png', NOW()),
('11111111-0000-0000-0000-000000000042', 'Corsair Dark Core RGB Pro', 'Chuột gaming không dây cao cấp, 3 chế độ kết nối, sạc Qi wireless.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0004-0000-0000-000000000002', 'http://103.90.225.90:8084/files/mouse-corsair-darkcore.png', NOW()),
('11111111-0000-0000-0000-000000000043', 'ASUS ROG Gladius III', 'Chuột gaming chuyên nghiệp, switch có thể thay, sensor 26000 DPI.', 'a1b2c3d4-3333-3333-3333-333333333333', 'c2222222-0004-0000-0000-000000000002', 'http://103.90.225.90:8084/files/mouse-asus-gladius3.png', NOW()),
('11111111-0000-0000-0000-000000000044', 'MSI Clutch GM41 Lightweight', 'Chuột gaming siêu nhẹ (65g), sensor PixArt PMW3389, RGB 16.8M màu.', 'a1b2c3d4-4444-4444-4444-444444444444', 'c2222222-0004-0000-0000-000000000002', 'http://103.90.225.90:8084/files/mouse-msi-gm41.png', NOW()),
('11111111-0000-0000-0000-000000000045', 'Kingston HyperX Pulsefire Haste', 'Chuột gaming nhẹ (59g), vỏ tổ ong tản nhiệt, TTC Golden switch.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0004-0000-0000-000000000002', 'http://103.90.225.90:8084/files/mouse-hyperx-haste.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'http://103.90.225.90:8084/files/mouse-logitech-superlight.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'http://103.90.225.90:8084/files/mouse-logitech-superlight-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'http://103.90.225.90:8084/files/mouse-logitech-superlight-2.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000042', 'http://103.90.225.90:8084/files/mouse-corsair-darkcore.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000042', 'http://103.90.225.90:8084/files/mouse-corsair-darkcore-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000043', 'http://103.90.225.90:8084/files/mouse-asus-gladius3.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000043', 'http://103.90.225.90:8084/files/mouse-asus-gladius3-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000044', 'http://103.90.225.90:8084/files/mouse-msi-gm41.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000044', 'http://103.90.225.90:8084/files/mouse-msi-gm41-1.png', FALSE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000045', 'http://103.90.225.90:8084/files/mouse-hyperx-haste.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000045', 'http://103.90.225.90:8084/files/mouse-hyperx-haste-1.png', FALSE);

-- SKUs for Logitech G Pro X Superlight
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'SUPERLIGHT-WHITE',
'{"color":"White", "weight":"63g", "sensor":"HERO 25K", "dpi":"25600", "wireless":true}',
3490000, NULL, 22, '893123459001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'SUPERLIGHT-BLACK',
'{"color":"Black", "weight":"63g", "sensor":"HERO 25K", "dpi":"25600", "wireless":true}',
3490000, NULL, 25, '893123459002', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000041', 'SUPERLIGHT-PINK',
'{"color":"Magenta", "weight":"63g", "sensor":"HERO 25K", "dpi":"25600", "wireless":true}',
3690000, NULL, 15, '893123459003', TRUE, NOW(), NOW());

-- SKUs for Corsair Dark Core RGB Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000042', 'DARKCORE-WIRELESS',
'{"connectivity":"2.4GHz + Bluetooth + Wired", "sensor":"18000 DPI", "wireless_charging":true}',
2990000, NULL, 18, '893123459004', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000042', 'DARKCORE-SE',
'{"connectivity":"2.4GHz + Bluetooth + Wired", "sensor":"18000 DPI", "wireless_charging":true, "edition":"Special Edition"}',
3290000, NULL, 12, '893123459005', TRUE, NOW(), NOW());

-- SKUs for ASUS ROG Gladius III
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000043', 'GLADIUS3-WIRED',
'{"connectivity":"Wired USB", "sensor":"26000 DPI", "switch":"Push-Fit II", "rgb":"Aura Sync"}',
1990000, NULL, 30, '893123459006', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000043', 'GLADIUS3-WIRELESS',
'{"connectivity":"2.4GHz + Bluetooth + Wired", "sensor":"26000 DPI", "switch":"Push-Fit II"}',
2790000, NULL, 20, '893123459007', TRUE, NOW(), NOW());

-- SKUs for MSI Clutch GM41
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000044', 'GM41-LIGHTWEIGHT',
'{"weight":"65g", "sensor":"PMW3389", "dpi":"16000", "rgb":"Mystic Light"}',
1490000, NULL, 35, '893123459008', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000044', 'GM41-WIRELESS',
'{"weight":"74g", "sensor":"PMW3389", "dpi":"16000", "wireless":true}',
1990000, NULL, 25, '893123459009', TRUE, NOW(), NOW());

-- SKUs for HyperX Pulsefire Haste
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000045', 'HASTE-WIRED',
'{"weight":"59g", "sensor":"16000 DPI", "cable":"HyperFlex", "design":"Honeycomb"}',
1290000, NULL, 40, '893123459010', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000045', 'HASTE-WIRELESS',
'{"weight":"61g", "sensor":"16000 DPI", "wireless":true, "battery":"100 hours"}',
1790000, NULL, 28, '893123459011', TRUE, NOW(), NOW());


-- ==============================
-- 💾 RAM
-- Category: RAM
-- Category ID: c2222222-0003-0000-0000-000000000002
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000051', 'Corsair Vengeance RGB Pro', 'RAM DDR4 cao cấp với đèn RGB 10 vùng, tản nhiệt nhôm, hỗ trợ XMP 2.0.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0003-0000-0000-000000000002', 'http://103.90.225.90:8084/files/ram-corsair-vengeance.png', NOW()),
('11111111-0000-0000-0000-000000000052', 'Kingston Fury Beast DDR5', 'RAM DDR5 thế hệ mới, tốc độ cao, tản nhiệt hiệu quả, RGB tùy chỉnh.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0003-0000-0000-000000000002', 'http://103.90.225.90:8084/files/ram-kingston-fury.png', NOW()),
('11111111-0000-0000-0000-000000000053', 'Samsung DDR5 Gaming', 'RAM DDR5 hiệu năng cao cho gaming, tốc độ 6400MHz, latency thấp.', 'a1b2c3d4-7777-7777-7777-777777777777', 'c2222222-0003-0000-0000-000000000002', 'http://103.90.225.90:8084/files/ram-samsung-ddr5.png', NOW()),
('11111111-0000-0000-0000-000000000054', 'Corsair Dominator Platinum', 'RAM cao cấp nhất, DHX Cooling, Capellix RGB, hiệu năng đỉnh cao.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0003-0000-0000-000000000002', 'http://103.90.225.90:8084/files/ram-corsair-dominator.png', NOW()),
('11111111-0000-0000-0000-000000000055', 'Kingston HyperX Fury', 'RAM gaming phổ thông, giá tốt, hiệu năng ổn định, tản nhiệt bền bỉ.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0003-0000-0000-000000000002', 'http://103.90.225.90:8084/files/ram-hyperx-fury.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000051', 'http://103.90.225.90:8084/files/ram-corsair-vengeance.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000052', 'http://103.90.225.90:8084/files/ram-kingston-fury.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000053', 'http://103.90.225.90:8084/files/ram-samsung-ddr5.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000054', 'http://103.90.225.90:8084/files/ram-corsair-dominator.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000055', 'http://103.90.225.90:8084/files/ram-hyperx-fury.png', TRUE);

-- SKUs for Corsair Vengeance RGB Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000051', 'VENGEANCE-16GB-3200',
'{"type":"DDR4", "capacity":"16GB (2x8GB)", "speed":"3200MHz", "rgb":true}',
1690000, NULL, 50, '893123460001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000051', 'VENGEANCE-32GB-3600',
'{"type":"DDR4", "capacity":"32GB (2x16GB)", "speed":"3600MHz", "rgb":true}',
2990000, NULL, 35, '893123460002', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000051', 'VENGEANCE-64GB-3600',
'{"type":"DDR4", "capacity":"64GB (2x32GB)", "speed":"3600MHz", "rgb":true}',
5490000, NULL, 20, '893123460003', TRUE, NOW(), NOW());

-- SKUs for Kingston Fury Beast DDR5
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000052', 'FURY-DDR5-16GB-5600',
'{"type":"DDR5", "capacity":"16GB (2x8GB)", "speed":"5600MHz", "rgb":true}',
2490000, NULL, 40, '893123460004', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000052', 'FURY-DDR5-32GB-6000',
'{"type":"DDR5", "capacity":"32GB (2x16GB)", "speed":"6000MHz", "rgb":true}',
3990000, NULL, 30, '893123460005', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000052', 'FURY-DDR5-64GB-6400',
'{"type":"DDR5", "capacity":"64GB (2x32GB)", "speed":"6400MHz", "rgb":true}',
7490000, NULL, 15, '893123460006', TRUE, NOW(), NOW());

-- SKUs for Samsung DDR5 Gaming
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000053', 'SAMSUNG-DDR5-16GB-6000',
'{"type":"DDR5", "capacity":"16GB (2x8GB)", "speed":"6000MHz", "latency":"CL36"}',
2690000, NULL, 35, '893123460007', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000053', 'SAMSUNG-DDR5-32GB-6400',
'{"type":"DDR5", "capacity":"32GB (2x16GB)", "speed":"6400MHz", "latency":"CL32"}',
4290000, NULL, 25, '893123460008', TRUE, NOW(), NOW());

-- SKUs for Corsair Dominator Platinum
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000054', 'DOMINATOR-32GB-6000',
'{"type":"DDR5", "capacity":"32GB (2x16GB)", "speed":"6000MHz", "cooling":"DHX", "rgb":"Capellix"}',
5990000, NULL, 18, '893123460009', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000054', 'DOMINATOR-64GB-6400',
'{"type":"DDR5", "capacity":"64GB (2x32GB)", "speed":"6400MHz", "cooling":"DHX", "rgb":"Capellix"}',
9990000, NULL, 10, '893123460010', TRUE, NOW(), NOW());

-- SKUs for Kingston HyperX Fury
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000055', 'FURY-DDR4-8GB-2666',
'{"type":"DDR4", "capacity":"8GB (1x8GB)", "speed":"2666MHz"}',
690000, NULL, 60, '893123460011', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000055', 'FURY-DDR4-16GB-3200',
'{"type":"DDR4", "capacity":"16GB (2x8GB)", "speed":"3200MHz"}',
1390000, NULL, 50, '893123460012', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000055', 'FURY-DDR4-32GB-3600',
'{"type":"DDR4", "capacity":"32GB (2x16GB)", "speed":"3600MHz"}',
2590000, NULL, 30, '893123460013', TRUE, NOW(), NOW());


-- ==============================
-- 🔧 SSD
-- Category: SSD
-- Category ID: c2222222-0003-0000-0000-000000000003
-- ==============================

-- PRODUCTS
INSERT INTO product (id, name, description, brand_id, category_id, thumbnail_url, created_at) VALUES
('11111111-0000-0000-0000-000000000061', 'Samsung 980 Pro', 'SSD NVMe Gen4 cao cấp, tốc độ đọc 7000MB/s, hiệu năng đỉnh cao cho gaming.', 'a1b2c3d4-7777-7777-7777-777777777777', 'c2222222-0003-0000-0000-000000000003', 'http://103.90.225.90:8084/files/ssd-samsung-980pro.png', NOW()),
('11111111-0000-0000-0000-000000000062', 'Kingston KC3000', 'SSD NVMe PCIe 4.0 hiệu năng cao, tốc độ 7000MB/s đọc, bền bỉ.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0003-0000-0000-000000000003', 'http://103.90.225.90:8084/files/ssd-kingston-kc3000.png', NOW()),
('11111111-0000-0000-0000-000000000063', 'Samsung 870 EVO', 'SSD SATA 2.5" phổ thông, độ bền cao, giá tốt cho nâng cấp laptop/PC.', 'a1b2c3d4-7777-7777-7777-777777777777', 'c2222222-0003-0000-0000-000000000003', 'http://103.90.225.90:8084/files/ssd-samsung-870evo.png', NOW()),
('11111111-0000-0000-0000-000000000064', 'Corsair MP600 Pro', 'SSD Gen4 với tản nhiệt, tốc độ cực nhanh, phù hợp cho workstation.', 'a1b2c3d4-5555-5555-5555-555555555555', 'c2222222-0003-0000-0000-000000000003', 'http://103.90.225.90:8084/files/ssd-corsair-mp600.png', NOW()),
('11111111-0000-0000-0000-000000000065', 'Kingston A2000', 'SSD NVMe Gen3 giá rẻ, hiệu năng tốt cho người dùng phổ thông.', 'a1b2c3d4-6666-6666-6666-666666666666', 'c2222222-0003-0000-0000-000000000003', 'http://103.90.225.90:8084/files/ssd-kingston-a2000.png', NOW());

INSERT INTO product_image (id, product_id, url, is_primary) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000061', 'http://103.90.225.90:8084/files/ssd-samsung-980pro.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000062', 'http://103.90.225.90:8084/files/ssd-kingston-kc3000.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000063', 'http://103.90.225.90:8084/files/ssd-samsung-870evo.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000064', 'http://103.90.225.90:8084/files/ssd-corsair-mp600.png', TRUE),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000065', 'http://103.90.225.90:8084/files/ssd-kingston-a2000.png', TRUE);

-- SKUs for Samsung 980 Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000061', 'SAMSUNG-980PRO-500GB',
'{"capacity":"500GB", "interface":"NVMe PCIe Gen4", "read":"6900MB/s", "write":"5000MB/s"}',
2290000, NULL, 40, '893123461001', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000061', 'SAMSUNG-980PRO-1TB',
'{"capacity":"1TB", "interface":"NVMe PCIe Gen4", "read":"7000MB/s", "write":"5100MB/s"}',
3690000, NULL, 35, '893123461002', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000061', 'SAMSUNG-980PRO-2TB',
'{"capacity":"2TB", "interface":"NVMe PCIe Gen4", "read":"7000MB/s", "write":"5100MB/s"}',
6990000, NULL, 20, '893123461003', TRUE, NOW(), NOW());

-- SKUs for Kingston KC3000
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000062', 'KC3000-512GB',
'{"capacity":"512GB", "interface":"NVMe PCIe 4.0", "read":"7000MB/s", "write":"3900MB/s"}',
2190000, NULL, 38, '893123461004', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000062', 'KC3000-1TB',
'{"capacity":"1TB", "interface":"NVMe PCIe 4.0", "read":"7000MB/s", "write":"6000MB/s"}',
3490000, NULL, 30, '893123461005', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000062', 'KC3000-2TB',
'{"capacity":"2TB", "interface":"NVMe PCIe 4.0", "read":"7000MB/s", "write":"7000MB/s"}',
6490000, NULL, 18, '893123461006', TRUE, NOW(), NOW());

-- SKUs for Samsung 870 EVO
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000063', 'SAMSUNG-870EVO-250GB',
'{"capacity":"250GB", "interface":"SATA 2.5\"", "read":"560MB/s", "write":"530MB/s"}',
990000, NULL, 55, '893123461007', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000063', 'SAMSUNG-870EVO-500GB',
'{"capacity":"500GB", "interface":"SATA 2.5\"", "read":"560MB/s", "write":"530MB/s"}',
1590000, NULL, 50, '893123461008', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000063', 'SAMSUNG-870EVO-1TB',
'{"capacity":"1TB", "interface":"SATA 2.5\"", "read":"560MB/s", "write":"530MB/s"}',
2690000, NULL, 40, '893123461009', TRUE, NOW(), NOW());

-- SKUs for Corsair MP600 Pro
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000064', 'MP600PRO-1TB',
'{"capacity":"1TB", "interface":"NVMe Gen4", "read":"7100MB/s", "write":"5800MB/s", "heatsink":true}',
3890000, NULL, 25, '893123461010', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000064', 'MP600PRO-2TB',
'{"capacity":"2TB", "interface":"NVMe Gen4", "read":"7100MB/s", "write":"6800MB/s", "heatsink":true}',
7290000, NULL, 15, '893123461011', TRUE, NOW(), NOW());

-- SKUs for Kingston A2000
INSERT INTO sku (id, product_id, sku_code, specs, price, discount_price, stock, barcode, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), '11111111-0000-0000-0000-000000000065', 'A2000-250GB',
'{"capacity":"250GB", "interface":"NVMe PCIe Gen3", "read":"2200MB/s", "write":"2000MB/s"}',
890000, NULL, 60, '893123461012', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000065', 'A2000-500GB',
'{"capacity":"500GB", "interface":"NVMe PCIe Gen3", "read":"2200MB/s", "write":"2000MB/s"}',
1490000, NULL, 50, '893123461013', TRUE, NOW(), NOW()),
(gen_random_uuid(), '11111111-0000-0000-0000-000000000065', 'A2000-1TB',
'{"capacity":"1TB", "interface":"NVMe PCIe Gen3", "read":"2200MB/s", "write":"2000MB/s"}',
2490000, NULL, 35, '893123461014', TRUE, NOW(), NOW());
