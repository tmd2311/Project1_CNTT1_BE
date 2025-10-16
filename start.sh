#!/bin/bash
echo "Starting all services..."
docker-compose up -d

echo "All services started successfully!"
echo "Access Consul UI at: http://localhost:8500"
echo "Gateway available at: http://localhost:8080"
