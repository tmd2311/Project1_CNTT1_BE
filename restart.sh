#!/bin/bash
echo "Restarting all services..."
docker-compose down
docker-compose up -d
echo "Restart completed!"
