#!/bin/bash
# ===========================
# Stop script for microservices
# ===========================

# Color codes
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color
BOLD='\033[1m'

SERVICE=$1

echo ""
if [ -z "$SERVICE" ]; then
  echo -e "${BOLD}${RED}🛑 Stopping all services...${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose down
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ All services stopped successfully!${NC}"
  fi
else
  echo -e "${BOLD}${RED}🛑 Stopping service: ${CYAN}$SERVICE${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose stop $SERVICE
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Service $SERVICE stopped successfully!${NC}"
  fi
fi
echo ""
