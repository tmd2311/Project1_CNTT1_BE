#!/bin/bash
# ===========================
# Start script for microservices
# ===========================

SERVICE=$1

if [ -z "$SERVICE" ]; then
  echo "🚀 Starting all services..."
  docker-compose up -d
else
  echo "🚀 Starting service: $SERVICE"
  docker-compose up -d $SERVICE
fi
