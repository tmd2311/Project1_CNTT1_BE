-- Migration: Add sku_id column to cart_item table
-- Author: System
-- Date: 2025-11-23

-- Add sku_id column to cart_item table
ALTER TABLE cart_item
ADD COLUMN sku_id UUID;

-- Update existing records (if any) with a placeholder or handle manually
-- Note: You may need to update existing cart items with proper sku_id values
-- based on your business logic before making this column NOT NULL

-- After data migration, make the column NOT NULL
-- ALTER TABLE cart_item
-- ALTER COLUMN sku_id SET NOT NULL;

-- Add index for better query performance
CREATE INDEX idx_cart_item_sku_id ON cart_item(sku_id);

-- Add composite index for common queries
CREATE INDEX idx_cart_item_user_product_sku ON cart_item(cart_id, product_id, sku_id);

-- Add comment
COMMENT ON COLUMN cart_item.sku_id IS 'UUID of the SKU (Stock Keeping Unit) for this cart item';
