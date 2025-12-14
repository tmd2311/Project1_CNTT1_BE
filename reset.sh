#!/bin/bash
# ===========================
# Reset script for microservices
# ===========================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color
BOLD='\033[1m'

SERVICE=$1

echo ""
if [ -z "$SERVICE" ]; then
  echo -e "${BOLD}${YELLOW}♻️  Resetting all services...${NC}"
  echo -e "${RED}⚠️  This will remove containers, networks, and volumes!${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose down -v --remove-orphans
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ All services reset successfully!${NC}"
  fi
else
  echo -e "${BOLD}${YELLOW}♻️  Resetting service: ${CYAN}$SERVICE${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose rm -sfv $SERVICE
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Service $SERVICE reset successfully!${NC}"
  fi
fi
echo ""
