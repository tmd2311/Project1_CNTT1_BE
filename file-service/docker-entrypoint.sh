#!/bin/sh
set -e

# Create upload directory if it doesn't exist
mkdir -p /uploads/images

# Set proper permissions for upload directory (777 to allow any user)
# This is needed when volume is mounted from host
chmod -R 777 /uploads

# Try to change ownership to spring user
# This may fail if volume is mounted from host, but chmod 777 should handle it
chown -R spring:spring /uploads 2>/dev/null || true

# Switch to spring user and execute the main command
exec su-exec spring "$@"

