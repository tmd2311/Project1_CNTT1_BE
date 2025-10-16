#!/bin/bash
echo "Building Docker images for all services..."

# Dừng và xóa containers cũ (nếu có)
docker-compose down --remove-orphans

# Build lại toàn bộ services
docker-compose build

echo "Build completed successfully!"
