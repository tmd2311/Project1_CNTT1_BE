#!/bin/bash
# ===========================
# Reset script for microservices
# ===========================

SERVICE=$1

if [ -z "$SERVICE" ]; then
  echo "♻️  Resetting all services (remove containers, networks, and volumes)..."
  docker-compose down -v --remove-orphans
else
  echo "♻️  Resetting service: $SERVICE"
  docker-compose rm -sfv $SERVICE
fi
