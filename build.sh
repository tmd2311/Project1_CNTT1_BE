#!/bin/bash
# ===========================
# Build script for microservices
# ===========================

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Enable BuildKit for cache mount support
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

SERVICE=$1

echo ""
if [ -z "$SERVICE" ]; then
  echo -e "${BOLD}${MAGENTA}🔨 Building all services...${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose build --progress=plain
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ All services built successfully!${NC}"
  else
    echo -e "${YELLOW}⚠️  Build completed with warnings/errors${NC}"
  fi
else
  echo -e "${BOLD}${MAGENTA}🔨 Building service: ${CYAN}$SERVICE${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  docker compose build --progress=plain $SERVICE
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Service $SERVICE built successfully!${NC}"
  else
    echo -e "${YELLOW}⚠️  Build completed with warnings/errors${NC}"
  fi
fi
echo ""
