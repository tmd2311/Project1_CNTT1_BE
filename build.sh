#!/bin/bash

# ===========================
# Smart Build Script
# Tự động phát hiện thay đổi và rebuild
# ===========================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# ===========================
# Check if shared libs need rebuild
# ===========================
check_shared_libs_changes() {
    print_info "Checking for shared libraries changes..."

    # Calculate hash of exception-lib and auth-lib
    EXCEPTION_LIB_HASH=$(find exception-lib -type f \( -name "*.java" -o -name "pom.xml" \) -exec sha256sum {} \; 2>/dev/null | sha256sum | cut -d' ' -f1)
    AUTH_LIB_HASH=$(find auth-lib -type f \( -name "*.java" -o -name "pom.xml" \) -exec sha256sum {} \; 2>/dev/null | sha256sum | cut -d' ' -f1)

    CURRENT_HASH="${EXCEPTION_LIB_HASH}-${AUTH_LIB_HASH}"
    HASH_FILE=".shared-libs-hash"

    if [ -f "$HASH_FILE" ]; then
        SAVED_HASH=$(cat "$HASH_FILE")
        if [ "$CURRENT_HASH" = "$SAVED_HASH" ]; then
            print_success "Shared libraries unchanged - skipping rebuild"
            return 1  # No rebuild needed
        else
            print_warning "Shared libraries changed - rebuild required!"
            return 0  # Rebuild needed
        fi
    else
        print_warning "First build - creating shared libs base image"
        return 0  # Rebuild needed
    fi
}

# ===========================
# Build shared libraries
# ===========================
build_shared_libs() {
    print_header "Building Shared Libraries Base Image"

    docker build \
        -f Dockerfile.shared-libs \
        -t proshop-shared-libs:latest \
        . || {
            print_error "Failed to build shared libraries"
            exit 1
        }

    # Save hash
    EXCEPTION_LIB_HASH=$(find exception-lib -type f \( -name "*.java" -o -name "pom.xml" \) -exec sha256sum {} \; 2>/dev/null | sha256sum | cut -d' ' -f1)
    AUTH_LIB_HASH=$(find auth-lib -type f \( -name "*.java" -o -name "pom.xml" \) -exec sha256sum {} \; 2>/dev/null | sha256sum | cut -d' ' -f1)
    CURRENT_HASH="${EXCEPTION_LIB_HASH}-${AUTH_LIB_HASH}"
    echo "$CURRENT_HASH" > .shared-libs-hash

    print_success "Shared libraries base image built successfully"
}

# ===========================
# Build specific service or all services
# ===========================
build_services() {
    SERVICE=$1

    if [ -z "$SERVICE" ]; then
        print_header "Building All Services"
        docker compose build --parallel || {
            print_error "Failed to build services"
            exit 1
        }
        print_success "All services built successfully"
    else
        print_header "Building Service: $SERVICE"
        docker compose build "$SERVICE" || {
            print_error "Failed to build $SERVICE"
            exit 1
        }
        print_success "$SERVICE built successfully"
    fi
}

# ===========================
# Main script
# ===========================
main() {
    print_header "ProShop Smart Build System"

    # Parse arguments
    FORCE_REBUILD_LIBS=false
    SERVICE_NAME=""

    while [[ $# -gt 0 ]]; do
        case $1 in
            --force-libs)
                FORCE_REBUILD_LIBS=true
                shift
                ;;
            --service)
                SERVICE_NAME="$2"
                shift 2
                ;;
            *)
                SERVICE_NAME="$1"
                shift
                ;;
        esac
    done

    # Check if shared libs image exists
    if ! docker image inspect proshop-shared-libs:latest >/dev/null 2>&1; then
        print_warning "Shared libs image not found - building it now..."
        build_shared_libs
        REBUILD_ALL=true
    elif [ "$FORCE_REBUILD_LIBS" = true ]; then
        print_warning "Force rebuilding shared libraries..."
        build_shared_libs
        REBUILD_ALL=true
    else
        if check_shared_libs_changes; then
            build_shared_libs
            REBUILD_ALL=true
        else
            REBUILD_ALL=false
        fi
    fi

    # Build services
    if [ "$REBUILD_ALL" = true ] && [ -z "$SERVICE_NAME" ]; then
        print_warning "Shared libs changed - rebuilding all services"
        build_services
    else
        build_services "$SERVICE_NAME"
    fi

    print_success "Build completed successfully! 🎉"
    echo ""
    print_info "To start services: docker compose up -d"
    print_info "To view logs: docker compose logs -f [service-name]"
}

# Run main
main "$@"
