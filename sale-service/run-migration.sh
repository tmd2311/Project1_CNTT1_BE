#!/bin/bash

# Script để chạy migration thêm column status vào sales table
# Usage: ./run-migration.sh

echo "=========================================="
echo "   SALE SERVICE - DATABASE MIGRATION"
echo "=========================================="
echo ""

# Database config
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-proshop_sale}
DB_USER=${DB_USER:-postgres}

echo "Database: $DB_NAME"
echo "Host: $DB_HOST:$DB_PORT"
echo "User: $DB_USER"
echo ""

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo "❌ Error: psql command not found"
    echo "Please install PostgreSQL client first"
    exit 1
fi

# Run migration
echo "Running migration..."
echo ""

PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME << EOF
-- Add status column
ALTER TABLE sales
ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'SCHEDULED';

-- Update existing sales
UPDATE sales
SET status = CASE
    WHEN end_date < NOW() THEN 'EXPIRED'
    WHEN start_date > NOW() THEN 'SCHEDULED'
    ELSE 'ACTIVE'
END
WHERE status IS NULL OR status = 'SCHEDULED';

-- Verify
SELECT 'Migration completed!' as message;
SELECT COUNT(*) as total_sales, status, COUNT(*) as count_by_status
FROM sales
GROUP BY status;
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Migration completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Rebuild project: mvn clean install"
    echo "2. Start service: mvn spring-boot:run"
else
    echo ""
    echo "❌ Migration failed!"
    echo "Please check error messages above"
    exit 1
fi
