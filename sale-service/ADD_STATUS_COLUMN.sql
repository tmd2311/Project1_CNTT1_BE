-- Migration: Add status column to sales table
-- Chạy script này để thêm column status vào table sales

-- 1. Thêm column status
ALTER TABLE sales
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'SCHEDULED';

-- 2. Update status cho các sale hiện có dựa trên dates
UPDATE sales
SET status = CASE
    WHEN end_date < NOW() THEN 'EXPIRED'
    WHEN start_date > NOW() THEN 'SCHEDULED'
    ELSE 'ACTIVE'
END
WHERE status IS NULL OR status = 'SCHEDULED';

-- 3. Set default constraint
ALTER TABLE sales
ALTER COLUMN status SET DEFAULT 'SCHEDULED';

-- Done!
-- Service sẽ tự động set status khi tạo mới sale
