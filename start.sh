#!/bin/bash
# ===========================
# Start script for microservices
# ===========================

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color
BOLD='\033[1m'

SERVICE=$1

echo ""
if [ -z "$SERVICE" ]; then
  echo -e "${BOLD}${CYAN}🚀 Starting all services...${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose up -d --no-build
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ All services started successfully!${NC}"
  else
    echo -e "${YELLOW}⚠️  Some services may have issues${NC}"
  fi
else
  echo -e "${BOLD}${CYAN}🚀 Starting service: ${GREEN}$SERVICE${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose up -d --no-build $SERVICE
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Service $SERVICE started successfully!${NC}"
  else
    echo -e "${YELLOW}⚠️  Service $SERVICE may have issues${NC}"
  fi
fi
echo ""
