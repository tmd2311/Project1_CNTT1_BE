#!/bin/bash
# ===========================
# Build script for microservices
# ===========================

SERVICE=$1

if [ -z "$SERVICE" ]; then
  echo "🔨 Building all services..."
  docker-compose build
else
  echo "🔨 Building service: $SERVICE"
  docker-compose build $SERVICE
fi
