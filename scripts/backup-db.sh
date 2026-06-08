#!/bin/bash
set -e
BACKUP_DIR="${BACKUP_DIR:-./backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
mkdir -p "$BACKUP_DIR"

echo "=== InkOS DB Backup ==="
docker compose exec -T mysql mysqldump \
  -u inkos \
  --password="${MYSQL_PASSWORD:-inkos_pass}" \
  --databases inkos \
  --single-transaction \
  --quick \
  --lock-tables=false \
  > "$BACKUP_DIR/inkos_$TIMESTAMP.sql"

echo "Backup saved to $BACKUP_DIR/inkos_$TIMESTAMP.sql"
gzip "$BACKUP_DIR/inkos_$TIMESTAMP.sql"
echo "Compressed: $BACKUP_DIR/inkos_$TIMESTAMP.sql.gz"
