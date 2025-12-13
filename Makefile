# ===========================
# ProShop Build Makefile
# Tự động phát hiện và rebuild shared libs
# ===========================

.PHONY: help build build-libs build-all start stop restart logs clean

# Default target
help:
	@echo "ProShop Build System"
	@echo "===================="
	@echo ""
	@echo "Available commands:"
	@echo "  make build          - Smart build (auto-detect changes)"
	@echo "  make build-libs     - Force rebuild shared libraries"
	@echo "  make build-all      - Build everything from scratch"
	@echo "  make build SERVICE=<name> - Build specific service"
	@echo ""
	@echo "  make start          - Start all services"
	@echo "  make stop           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs"
	@echo "  make clean          - Clean Docker resources"
	@echo ""
	@echo "Examples:"
	@echo "  make build SERVICE=auth-service"
	@echo "  make logs SERVICE=product-service"

# Smart build - detects changes automatically
build:
	@bash build.sh $(if $(SERVICE),$(SERVICE))

# Force rebuild shared libraries
build-libs:
	@bash build.sh --force-libs

# Build everything from scratch
build-all:
	@bash build.sh --force-libs

# Start services
start:
	docker-compose up -d $(SERVICE)

# Stop services
stop:
	docker-compose down

# Restart services
restart:
	docker-compose restart $(SERVICE)

# View logs
logs:
	docker-compose logs -f $(SERVICE)

# Clean Docker resources
clean:
	@echo "Cleaning Docker resources..."
	docker-compose down -v
	docker system prune -f
	@echo "Clean complete!"

# First time setup
setup:
	@echo "Setting up ProShop..."
	@chmod +x build.sh
	@bash build.sh --force-libs
	@echo "Setup complete! Run 'make start' to start services."
