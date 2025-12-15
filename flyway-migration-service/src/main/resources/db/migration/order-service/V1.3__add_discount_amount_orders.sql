-- Add discount_amount column to orders table
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(100,2) NOT NULL DEFAULT 0;

