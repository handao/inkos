# InkOS — AI Novel Writing Platform

InkOS is an AI-powered novel writing platform with a 10-agent LLM pipeline that helps writers plan, write, audit, and revise novels. It supports 43+ LLM providers with 767 model cards.

## Architecture

```
┌─────────────────────┐     ┌─────────────────────┐
│   uni-app Frontend  │────▶│  Spring Boot Backend │
│  (H5/iOS/Android)   │     │   (REST API v1)     │
└─────────────────────┘     └──────┬──────────────┘
                                   │
                          ┌────────▼──────────────┐
                          │   Core Engine (Java)  │
                          │  ┌──────────────────┐ │
                          │  │ 10-Agent Pipeline│ │
                          │  │ Radar→Planner→...│ │
                          │  └──────────────────┘ │
                          │  ┌──────────────────┐ │
                          │  │ 43 LLM Providers │ │
                          │  └──────────────────┘ │
                          └───────────────────────┘
```

## Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Docker (optional, for containerized deployment)

### Local Development

```bash
# 1. Start MySQL
mysql -u root -e "CREATE DATABASE IF NOT EXISTS inkos_dev"

# 2. Start backend
cd inkos-server
JAVA_HOME=/path/to/jdk21 ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Start frontend (in another terminal)
cd inkos-app
npm install
npm run dev

# 4. Open browser
open http://localhost:4567
```

### Using H2 (No MySQL needed)

```bash
cd inkos-server
JAVA_HOME=/path/to/jdk21 ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

### Docker Production

```bash
# 1. Configure environment
cp .env.example .env
# Edit .env with your settings

# 2. Start all services
bash scripts/start-prod.sh
```

## Backend API

Base URL: `http://localhost:8080/api/v1`

### Authentication
| Method | Path | Description |
|--------|------|-------------|
| POST | /auth/send-code | Send verification code |
| POST | /auth/register | Register new user |
| POST | /auth/login | Login |
| POST | /auth/refresh | Refresh token |
| GET | /auth/me | Get current user |

### Books
| Method | Path | Description |
|--------|------|-------------|
| GET | /books | List books |
| POST | /books | Create book |
| GET | /books/{id} | Get book |
| PUT | /books/{id} | Update book |
| DELETE | /books/{id} | Delete book |

### Chapters
| Method | Path | Description |
|--------|------|-------------|
| GET | /books/{bookId}/chapters | List chapters |
| POST | /books/{bookId}/chapters | Create chapter |
| GET | /chapters/{id} | Get chapter |
| PUT | /chapters/{id} | Update chapter |
| DELETE | /chapters/{id} | Delete chapter |

### AI Writing
| Method | Path | Description |
|--------|------|-------------|
| GET | /sessions | List sessions |
| POST | /sessions | Create session |
| GET | /sessions/{id} | Get session |
| DELETE | /sessions/{id} | Delete session |
| POST | /sessions/{id}/messages | Send message |

### LLM Configuration
| Method | Path | Description |
|--------|------|-------------|
| GET | /llm/services | List services |
| POST | /llm/services | Save service |
| DELETE | /llm/services/{id} | Delete service |
| POST | /llm/secrets | Save secret |

### Admin
| Method | Path | Description |
|--------|------|-------------|
| GET | /admin/users | List users |
| PUT | /admin/users/{id}/status | Update user status |
| GET | /admin/whitelist | List whitelist |
| POST | /admin/whitelist | Add to whitelist |
| DELETE | /admin/whitelist/{id} | Remove from whitelist |

## LLM Providers

InkOS supports 43 LLM providers with 767 model cards:

| Provider | Models | Provider | Models |
|----------|--------|----------|--------|
| OpenAI | 53 | Anthropic | 9 |
| Google Gemini | 26 | DeepSeek | 4 |
| Moonshot (Kimi) | 14 | Zhipu (GLM) | 38 |
| MiniMax | 17 | SiliconCloud | 98 |
| Alibaba Bailian | 32 | Volcengine | 38 |
| Tencent Hunyuan | 20 | Baichuan | 6 |
| StepFun | 13 | WenXin (ERNIE) | 84 |
| Xunfei Spark | 6 | SenseNova | 23 |
| Ollama (local) | 52 | OpenRouter | 60 |
| Mistral | 17 | xAI (Grok) | 11 |
| GitHub Copilot | 21 | +22 more | |

## Core Pipeline

The 10-agent pipeline:

1. **Radar** — Scans novel projects, identifies state
2. **Planner** — Creates chapter plans from outline
3. **Architect** — Designs chapter structure
4. **Writer** — Generates chapter content
5. **Observer** — Tracks characters/plot changes
6. **Reflector** — Updates truth files
7. **Normalizer** — Adjusts content length
8. **Auditor** — Audits quality (32 dimensions)
9. **Reviser** — Revises based on feedback

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| JWT_SECRET | - | JWT signing key (REQUIRED in production) |
| MYSQL_PASSWORD | root | MySQL password |
| SMTP_HOST | localhost | SMTP server |
| SMTP_USERNAME | - | SMTP username |
| SMTP_PASSWORD | - | SMTP password |
| MAIL_FROM | noreply@inkos.app | From address |
| ALLOWED_ORIGINS | http://localhost:5173 | CORS origins |

### inkos.json (CLI)

```json
{
  "service": "openai",
  "model": "gpt-4o",
  "apiKeyEnv": "OPENAI_API_KEY",
  "inputGovernanceMode": "v2",
  "projectRoot": "/path/to/books"
}
```

## Project Structure

```
inkos/
├── inkos-server/          # Spring Boot backend
│   ├── src/main/java/     # Java source
│   ├── src/test/java/     # Tests (187)
│   └── Dockerfile
├── inkos-app/             # uni-app frontend
│   ├── src/pages/         # 16 pages
│   └── src/api/           # API client
├── packages/
│   ├── core/              # TS core engine (reference)
│   └── cli/               # Node.js CLI
├── docker-compose.yml     # Production stack
├── nginx.conf             # Reverse proxy
└── scripts/               # Deployment scripts
```

## Testing

```bash
# Backend
cd inkos-server
JAVA_HOME=/path/to/jdk21 ./mvnw test

# Frontend
cd inkos-app
npm run build
```

## License

MIT
