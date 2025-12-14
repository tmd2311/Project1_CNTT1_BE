-- Migration V1.2: Create revenue table and add includedInRevenue flag to orders table
-- Purpose: Support revenue calculation and tracking

-- Step 1: Add includedInRevenue column to orders table
ALTER TABLE orders
ADD COLUMN included_in_revenue BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index for faster querying
CREATE INDEX idx_orders_included_in_revenue ON orders(included_in_revenue, status);

-- Step 2: Create revenue table
CREATE TABLE revenue (
    id BIGSERIAL PRIMARY KEY,
    revenue_date DATE NOT NULL UNIQUE,
    total_revenue DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    order_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on revenue_date for faster date range queries
CREATE INDEX idx_revenue_date ON revenue(revenue_date);

-- Create index for year/month queries
CREATE INDEX idx_revenue_year_month ON revenue(EXTRACT(YEAR FROM revenue_date), EXTRACT(MONTH FROM revenue_date));

-- Add comments for documentation
COMMENT ON TABLE revenue IS 'Daily revenue summary calculated from completed orders';
COMMENT ON COLUMN revenue.revenue_date IS 'Date for which revenue is calculated';
COMMENT ON COLUMN revenue.total_revenue IS 'Total revenue amount for the date';
COMMENT ON COLUMN revenue.order_count IS 'Number of completed orders for the date';
COMMENT ON COLUMN orders.included_in_revenue IS 'Flag indicating if order has been included in revenue calculation';
