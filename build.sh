#!/bin/bash

echo "🚀 Bắt đầu build lại toàn bộ dịch vụ..."

# Dừng và xóa container cũ
docker compose down

# Xóa image cũ (nếu có)
echo "🧹 Xóa image cũ..."
docker rmi $(docker images "auth-service" -q) -f 2>/dev/null
docker rmi $(docker images "product-service" -q) -f 2>/dev/null
docker rmi $(docker images "gateway-service" -q) -f 2>/dev/null

# Build lại tất cả image
echo "🔧 Đang build lại image..."
docker compose build --no-cache

# Khởi động lại toàn bộ stack
echo "🌐 Khởi động lại các container..."
docker compose up -d

# Hiển thị danh sách container đang chạy
echo "✅ Danh sách container hiện tại:"
docker ps
