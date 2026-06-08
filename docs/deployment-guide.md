# InkOS Deployment Guide

## Production Requirements

| Component | Version | Notes |
|-----------|---------|-------|
| Java | 21+ (JDK) | Required for backend build & runtime |
| Node.js | 18+ | Required for frontend build |
| MySQL | 8.0+ | Primary database |
| Docker | 24+ | Optional, for containerized deployment |
| Nginx | 1.24+ | Reverse proxy |
| SMTP Server | - | For email notifications (QQ Mail, SendGrid, etc.) |
| RAM | 4GB+ | Minimum recommended |
| Disk | 10GB+ | For data storage |

---

## Docker Deployment (Recommended)

### 1. Clone & Prepare

```bash
git clone https://github.com/Narcooo/inkos.git
cd inkos
```

### 2. Configure Environment

```bash
cp .env.example .env
```

Edit `.env` with your production values:

```bash
# Required
JWT_SECRET=your-256-bit-jwt-secret-here-must-be-long-enough
MYSQL_ROOT_PASSWORD=strong_root_password
MYSQL_PASSWORD=strong_inkos_password

# SMTP (required for registration)
SMTP_HOST=smtp.qq.com
SMTP_PORT=587
SMTP_USERNAME=your-email@qq.com
SMTP_PASSWORD=your-smtp-authorization-code
MAIL_FROM=your-email@qq.com

# CORS (comma-separated)
ALLOWED_ORIGINS=https://inkos.yourdomain.com
```

### 3. Build & Start

```bash
bash scripts/start-prod.sh
```

This script:
1. Builds the frontend (`inkos-app`)
2. Builds the backend JAR (`inkos-server`)
3. Starts MySQL, backend, and frontend via Docker Compose

### 4. Verify

```
Frontend: http://localhost:80
Backend:  http://localhost:8080/health
```

### 5. Stop

```bash
bash scripts/stop-prod.sh
```

---

## Manual Deployment

### Database Setup

```sql
CREATE DATABASE IF NOT EXISTS inkos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'inkos'@'%' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON inkos.* TO 'inkos'@'%';
FLUSH PRIVILEGES;
```

### Backend (Spring Boot)

```bash
cd inkos-server

# Build
JAVA_HOME=/path/to/jdk21 ./mvnw package -DskipTests

# Run
JAVA_HOME=/path/to/jdk21 java -jar target/inkos-server-0.1.0-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --inkos.jwt.secret='your-jwt-secret' \
  --spring.datasource.password='your-db-password' \
  --inkos.mail.from='noreply@inkos.app' \
  --spring.mail.host='smtp.qq.com' \
  --spring.mail.port=587 \
  --spring.mail.username='your-email@qq.com' \
  --spring.mail.password='your-smtp-password'
```

Or use environment variables (see `application-prod.yml` for all options):

```bash
export JWT_SECRET='your-jwt-secret'
export MYSQL_PASSWORD='your-db-password'
export SMTP_HOST='smtp.qq.com'
export SMTP_USERNAME='your-email@qq.com'
export SMTP_PASSWORD='your-smtp-password'
export MAIL_FROM='noreply@inkos.app'
export ALLOWED_ORIGINS='https://inkos.yourdomain.com'

JAVA_HOME=/path/to/jdk21 java -jar target/inkos-server-*.jar --spring.profiles.active=prod
```

### Frontend (uni-app)

```bash
cd inkos-app

# Install dependencies
npm install

# Build for production
npm run build:h5
```

Output goes to `inkos-app/dist/build/h5/` which can be served by Nginx.

---

## Nginx Configuration

```nginx
server {
    listen 443 ssl http2;
    server_name inkos.yourdomain.com;

    ssl_certificate /etc/ssl/certs/inkos.crt;
    ssl_certificate_key /etc/ssl/private/inkos.key;

    # Frontend static files
    root /var/www/inkos-app/dist/build/h5;
    index index.html;

    # Gzip
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    gzip_min_length 1000;

    # API proxy
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
        proxy_send_timeout 60s;

        # Large chapter content
        client_max_body_size 50m;
    }

    # SPA fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}
```

---

## Docker Compose Production Stack

See `docker-compose.yml` at repo root:

```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: inkos
      MYSQL_USER: inkos
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]

  backend:
    build: ./inkos-server
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      JWT_SECRET: ${JWT_SECRET}
      SMTP_HOST: ${SMTP_HOST}
      SMTP_USERNAME: ${SMTP_USERNAME}
      SMTP_PASSWORD: ${SMTP_PASSWORD}
      MAIL_FROM: ${MAIL_FROM}
      ALLOWED_ORIGINS: ${ALLOWED_ORIGINS}
    depends_on:
      mysql:
        condition: service_healthy

  frontend:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./inkos-app/dist:/usr/share/nginx/html:ro
      - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
    depends_on:
      - backend
```

Run:

```bash
docker compose up -d
docker compose logs -f    # tail logs
docker compose down       # stop
```

---

## MySQL Setup (Production Hardening)

```sql
-- Recommended settings
SET GLOBAL max_connections = 200;
SET GLOBAL innodb_buffer_pool_size = 1G;
SET GLOBAL innodb_log_file_size = 256M;
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 2;
```

**Backup script** (add to crontab):

```bash
#!/bin/bash
# /usr/local/bin/backup-inkos.sh
BACKUP_DIR=/backups/mysql
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -h localhost -u root -p$MYSQL_ROOT_PASSWORD inkos \
  --single-transaction --routines --triggers \
  | gzip > $BACKUP_DIR/inkos_$DATE.sql.gz

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

Crontab (daily at 3 AM):
```
0 3 * * * /usr/local/bin/backup-inkos.sh
```

---

## SMTP Configuration

### QQ Mail

| Setting | Value |
|---------|-------|
| SMTP Host | smtp.qq.com |
| Port | 587 (TLS) |
| Auth | Yes |
| Username | your-email@qq.com |
| Password | SMTP authorization code (not email password) |

Generate authorization code: QQ Mail → Settings → Account → POP3/IMAP/SMTP service → Generate.

### SendGrid

| Setting | Value |
|---------|-------|
| SMTP Host | smtp.sendgrid.net |
| Port | 587 |
| Username | apikey |
| Password | SG.xxxxxxxx API key |

### Testing SMTP

```bash
# Test from command line
openssl s_client -starttls smtp -connect smtp.qq.com:587
```

---

## Monitoring

### Health Check

```
GET /health
```

Response:
```json
{
  "status": "UP",
  "timestamp": "2025-06-08T00:00:00Z",
  "version": "0.1.0"
}
```

### Logging

Backend logs to stdout by default. Configure log rotation:

```yaml
# logging in application-prod.yml
logging:
  file:
    path: /var/log/inkos
    name: /var/log/inkos/server.log
  logback:
    rollingpolicy:
      max-history: 30
      max-file-size: 100MB
```

### Docker Logging

```bash
# View logs
docker compose logs -f backend
docker compose logs -f frontend

# Docker log rotation (in docker-compose.yml)
services:
  backend:
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"
```

---

## Backup Strategy

| Data | Method | Frequency | Retention |
|------|--------|-----------|-----------|
| MySQL | `mysqldump` | Daily | 30 days |
| Truth files | Filesystem backup | Daily | 30 days |
| Application config | Git | On change | Indefinite |
| Docker volumes | Volume backup | Weekly | 90 days |

### Full Backup

```bash
#!/bin/bash
# /usr/local/bin/backup-inkos-full.sh
DATE=$(date +%Y%m%d)
BACKUP_DIR=/backups/inkos/full/$DATE
mkdir -p $BACKUP_DIR

# Database
docker compose exec -T mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD inkos \
  | gzip > $BACKUP_DIR/database.sql.gz

# Docker volumes
docker run --rm -v inkos_mysql_data:/data -v $BACKUP_DIR:/backup \
  alpine tar czf /backup/mysql-volume.tar.gz -C /data .

echo "Backup complete: $BACKUP_DIR"
```

---

## Troubleshooting

### Backend won't start

| Symptom | Check | Fix |
|---------|-------|-----|
| `Access denied for user` | DB credentials | Verify `MYSQL_PASSWORD` |
| `Unknown database` | Database exists? | `CREATE DATABASE IF NOT EXISTS inkos` |
| `Port 8080 already in use` | Port conflict | `lsof -i :8080`, kill the process |
| `Flyway migration failed` | Schema mismatch | Check `V1`-`V4` migration files |
| `JWT secret too short` | Secret length | HMAC-SHA256 requires 256-bit key |

### Frontend issues

| Symptom | Check | Fix |
|---------|-------|-----|
| Blank page on load | Build output exists? | `ls dist/build/h5/` |
| API calls fail with 401 | Token expired | Check token refresh logic |
| CORS errors | `ALLOWED_ORIGINS` | Match the frontend URL exactly |
| `Failed to fetch` | Backend reachable? | `curl localhost:8080/health` |

### Docker issues

| Symptom | Check | Fix |
|---------|-------|-----|
| Container exits immediately | Check logs | `docker compose logs backend` |
| MySQL health check fails | MySQL booting | Wait 30s, containers depend on health |
| Frontend can't reach backend | Docker network | Services communicate via Docker DNS |
| Permission denied | Volume mounts | Check `./inkos-app/dist` exists |

### Performance tuning

- Increase `spring.datasource.hikari.maximum-pool-size` for high concurrency
- Set `server.tomcat.max-threads` to match expected load
- Enable JPA query caching for read-heavy workloads
- Use Nginx caching for static assets
- Consider read replicas for MySQL at scale

---

## Security Checklist

- [ ] Change `JWT_SECRET` to a strong random value (256 bits minimum)
- [ ] Change default MySQL passwords
- [ ] Enable HTTPS with valid SSL certificate
- [ ] Set restrictive `ALLOWED_ORIGINS`
- [ ] Disable `allowCredentials` if not using cookies
- [ ] Implement API rate limiting
- [ ] Encrypt API keys at rest in the `secret` table
- [ ] Regular security updates (Java, Node.js, dependencies)
- [ ] Enable MySQL SSL for remote connections
- [ ] Use a WAF (Cloudflare, AWS WAF) for production
- [ ] Run as non-root user in Docker containers

## Scaling

- **Horizontal scaling:** Run multiple backend instances behind a load balancer
- **Session affinity:** Not required (stateless JWT auth)
- **Database scaling:** Add read replicas for query-heavy workloads
- **Caching:** Add Redis for session caching and rate limiting
- **File storage:** Use S3-compatible storage for truth files instead of local filesystem
