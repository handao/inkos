#!/bin/bash
set -e
echo "=== InkOS Deploy ==="

if [ -z "$JWT_SECRET" ]; then
  echo "ERROR: JWT_SECRET not set"
  exit 1
fi

echo "Building frontend..."
cd inkos-app && npm run build && cd ..

echo "Building backend Docker image..."
docker compose build backend

echo "Starting all services..."
docker compose up -d --build

echo "=== InkOS Deploy Complete ==="
echo "Frontend: http://localhost"
echo "Backend:  http://localhost:8080/api"
echo "Health:   http://localhost:8080/health"
