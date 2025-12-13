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
    (gen_random_uuid(), '11111111-0000-0000-0000-000000000001', 'http://103.90.225.90:8084/files/may1-4.png', FALSE);

--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000002', 'http://103.90.225.90:8084/files/d2c52469-f79f-41e1-9227-eef9ce3a49c2.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000003', 'http://103.90.225.90:8084/files/5acbea9c-4b7a-4be2-87b0-5e33fa3a6c7e.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000004', 'http://103.90.225.90:8084/files/818d3292-4e30-4bc3-8cb7-06946de74a15.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000005', 'http://103.90.225.90:8084/files/1fade80b-5ebc-48cc-9774-88be3f0accd6.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000006', 'http://103.90.225.90:8084/files/f2977c71-a191-43c2-b756-eb286349ff93.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000007', 'http://103.90.225.90:8084/files/c43110b1-d172-45b2-a7a7-ef4c2f1dd155.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000008', 'http://103.90.225.90:8084/files/f487dce4-ce68-47b0-946b-eb29b104973d.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000009', 'http://103.90.225.90:8084/files/31cebb20-5d9a-4846-8b5b-05da91e5687b.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000010', 'http://103.90.225.90:8084/files/81e52765-5a67-42b9-91f3-24ef12852477.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000011', 'http://103.90.225.90:8084/files/0c324548-e657-4f5d-9eb0-2b55e22884bf.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000012', 'http://103.90.225.90:8084/files/1bec5bb1-e1a2-4390-9542-589f57474d6e.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000013', 'http://103.90.225.90:8084/files/ef0e7574-dc7c-4f93-ba8f-b91812993566.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000014', 'http://103.90.225.90:8084/files/53d1f7ad-76b4-43e3-90f2-ce76e398288d.png', FALSE),
--     (gen_random_uuid(), '11111111-0000-0000-0000-000000000015', 'http://103.90.225.90:8084/files/a1aa98df-8614-4e3f-9e7b-82ad52764dc6.png', FALSE);


