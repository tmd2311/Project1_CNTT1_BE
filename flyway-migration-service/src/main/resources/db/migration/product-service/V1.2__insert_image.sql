-- ============================================
-- COMPREHENSIVE SAMPLE DATA FOR E-COMMERCE
-- 8 Brands, Each with ~10 Products
-- Each Product has 2-4 SKU Variants
-- Total: ~80 Products, ~200 SKUs
-- ============================================


INSERT INTO product_image (id, product_id, url, is_primary)
VALUES
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/may1-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/may1-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/may1-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/may1-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/may2-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/may2-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/may2-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/may2-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/may3-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/may3-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/may3-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/may3-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/may4-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/may4-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/may4-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/may4-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/may5-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/may5-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/may5-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/may5-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/may6-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/may6-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/may6-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/may6-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/may7-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/may7-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/may7-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/may7-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/may8-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/may8-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/may8-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/may8-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/may9-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/may9-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/may9-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/may9-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/may10-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/may10-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/may10-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/may10-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/may11-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/may11-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/may11-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/may11-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/may12-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/may12-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/may12-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/may12-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/may13-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/may13-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/may13-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/may13-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/may14-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/may14-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/may14-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/may14-4.png', FALSE),

    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/may15-1.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/may15-2.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/may15-3.png', FALSE),
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/may15-4.png', FALSE);


