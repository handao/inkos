#!/bin/bash
set -e
echo "=== InkOS Production Stop ==="
docker compose down
echo "=== InkOS Production Stopped ==="
