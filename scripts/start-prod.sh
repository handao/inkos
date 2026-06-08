#!/bin/bash
set -e
echo "=== InkOS Production Start ==="

if [ -z "$JWT_SECRET" ]; then
  echo "ERROR: JWT_SECRET not set"
  exit 1
fi

echo "Building frontend..."
cd inkos-app && npm install && npm run build:h5 && cd ..

echo "Building backend..."
cd inkos-server && ./mvnw package -DskipTests -q && cd ..

echo "Starting services..."
docker compose up -d --build

echo "=== InkOS Production Started ==="
echo "Frontend: http://localhost"
echo "Backend:  http://localhost:8080"
