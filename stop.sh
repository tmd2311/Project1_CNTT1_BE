#!/bin/bash
# ===========================
# Stop script for microservices
# ===========================

SERVICE=$1

if [ -z "$SERVICE" ]; then
  echo "🛑 Stopping all services..."
  docker-compose down
else
  echo "🛑 Stopping service: $SERVICE"
  docker-compose stop $SERVICE
fi
