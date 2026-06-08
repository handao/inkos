# InkOS — Agent 指南

## 一句话
10 个 Agent 组成的 LLM 管线（雷达→规划师→编排师→建筑师→写手→观察者→反射器→归一化器→审计员→修订者），自动生成、审计、修订小说。Web Studio（Vite+React+Hono）+ CLI（Commander.js+Ink TUI）+ **Java Spring Boot 后端**（REST API v1）+ **uni-app 前端**（H5/iOS/Android）。

## 项目结构

| 组件 | 位置 | 技术栈 | 角色 |
|------|------|--------|------|
| CLI | `packages/cli` | Commander.js + Ink TUI | 命令行工具 |
| Core | `packages/core` | TypeScript | 管线引擎（Node 参考实现） |
| Studio | `packages/studio` | Vite + React + Hono | Web 工作台 |
| Server | `inkos-server/` | Spring Boot 3.4 + JPA + Security | SaaS 后端 REST API |
| App | `inkos-app/` | uni-app + Vue 3 + Pinia | 移动端/Web 客户端 |
| Pipeline | `inkos-server/.../core/` | Java 21 + CompletableFuture | Phase 3 管线引擎迁移 |

## 单体仓库 (Node.js CLI 包)

pnpm workspace，包位于 `packages/`：

| 包 | npm 名 | 角色 | 构建前提 |
|---------|-----|------|-------------------|
| `packages/core` | `@actalk/inkos-core` | 管线引擎、Agent、LLM 提供商、状态管理 | — |
| `packages/cli` | `@actalk/inkos` | CLI、TUI、命令 | 先构建 `core` |
| `packages/studio` | `@actalk/inkos-studio` | Web 工作台 | 先构建 `core` |

`cli` 和 `studio` 通过 `workspace:*` 依赖 `core`——必须先构建 core。

## 后端架构 (Phase 3 Java Engine)

### 技术栈
- Spring Boot 3.4.4, Java 21
- Spring Security + JWT (jjwt 0.12.6) + BCrypt
- Spring Data JPA + Flyway + MySQL 8.0 / H2
- Spring Mail (SMTP)
- Lombok, Maven wrapper

### 目录结构
```
inkos-server/
├── src/main/java/com/inkos/
│   ├── common/               # ApiResponse, PagedResponse
│   ├── config/               # SecurityConfig, CorsConfig
│   ├── controller/           # 7 REST controllers
│   ├── core/                 # Phase 3 管线引擎
│   │   ├── agent/            # 15 agent classes
│   │   ├── llm/              # 44 endpoint definitions + provider
│   │   └── pipeline/         # PipelineRunner, Scheduler
│   ├── dto/                  # 14 request + 10 response DTOs
│   ├── entity/               # 10 JPA entities
│   ├── exception/            # ErrorCode enum (23 codes) + handler
│   ├── repository/           # 10 Spring Data repositories
│   ├── security/             # JWT filter, provider, config
│   └── service/              # 6 service classes
├── src/main/resources/
│   ├── application.yml       # Default config (MySQL, Flyway, SMTP)
│   ├── application-{dev,prod,h2}.yml
│   └── db/migration/         # V1-V4 Flyway migrations
└── src/test/java/            # 15 test files, 181 test methods
```

### API 端点概览
| 前缀 | 控制器 | 端点数 | 鉴权 |
|---------|-----------|--------|-------------|
| `/api/v1/auth` | AuthController | 5 | 混合 |
| `/api/v1/books` | BookController | 5 | Bearer JWT |
| `/api/v1/books/{bookId}/chapters` | ChapterController | 5 | Bearer JWT |
| `/api/v1/sessions` | SessionController | 6 | Bearer JWT |
| `/api/v1/llm` | LlmConfigController | 5 | Bearer JWT |
| `/api/v1/admin` | AdminController | 5 | ROLE_ADMIN |
| `/health` | HealthController | 1 | 公开 |

### Phase 3 管线引擎 (Java)
- 44 个 LLM 端点定义（从 TS 移植）
- 15 个 Agent 类：WriterAgent, PlannerAgent, ArchitectAgent, AuditorAgent 等
- PipelineRunner + PipelineScheduler
- 支持异步 LLM 调用 (CompletableFuture)
- 详情见 `inkos-server/.../core/PIPELINE_ARCHITECTURE.md`

### DB Schema (4 个迁移文件)
- V1: users, verification_code, allowed_email
- V2: book, chapter, truth_file
- V3: agent_session, agent_message
- V4: llm_service_config, secret

## 关键命令

```bash
pnpm install                  # 安装全部（CI 中使用 --frozen-lockfile）
pnpm build                    # 构建所有包（按 core→cli、core→studio 顺序）
pnpm dev                      # 对所有包并行启动监听模式
pnpm test                     # 运行全部测试
pnpm typecheck                # 类型检查所有包（需要先 build）
pnpm lint                     # 对所有包运行 lint
pnpm release                  # = pnpm build && pnpm test
pnpm verify:publish-manifests # 检查 workspace:* 是否可发布
```

### 单包过滤

```bash
pnpm --filter @actalk/inkos-core test
pnpm --filter @actalk/inkos test          # pretest 会自动先构建 core
pnpm --filter @actalk/inkos-core typecheck
```

### Studio 开发（客户端 + 服务端分离）

```bash
# 在 packages/studio 目录下运行：
pnpm dev          # 同时运行 Vite 客户端（端口 4567）+ Hono API 服务端（端口 4569）
pnpm dev:client   # 仅 Vite
pnpm dev:server   # 仅 Hono API（tsx watch）
```

需要 `INKOS_PROJECT_ROOT=../..`（已在 studio dev 脚本中设置）。

### 后端开发 (Java)

```bash
cd inkos-server

# 使用 H2（开发快速启动，无需 MySQL）
JAVA_HOME=/path/to/jdk21 ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# 使用 MySQL（需要本地 MySQL）
mysql -u root -e "CREATE DATABASE IF NOT EXISTS inkos_dev"
JAVA_HOME=/path/to/jdk21 ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 运行测试
JAVA_HOME=/path/to/jdk21 ./mvnw test
```

### 前端开发 (uni-app)

```bash
cd inkos-app
npm install
npm run dev:h5       # H5 开发
npm run dev:app      # 原生 App 开发
npm run build:h5     # 生产构建
```

### Docker 生产部署

```bash
# 一键启动
bash scripts/start-prod.sh
# 停止
bash scripts/stop-prod.sh
# 手动 docker compose
docker compose up -d --build
```

## 代码规范

- TypeScript strict 模式，Node16 模块解析，ESM（`"type": "module"`）
- 2 空格缩进，函数不超过 50 行，文件不超过 800 行
- 不可变模式：使用 `{ ...obj, key: val }` 而非直接修改
- 静默 `catch {}` 必须写注释说明原因
- 测试文件位于源码旁的 `__tests__/` 目录，使用 Vitest
- 测试中需 mock LLM 调用——不要发起真实 API 请求
- Java 代码使用 Spring Boot 规范 + Lombok —— 见 `inkos-server/` 结构

## 测试

```bash
# Node.js 包测试
pnpm --filter @actalk/inkos-core test   # core 测试
pnpm --filter @actalk/inkos test        # CLI 测试（自动先构建 core）
pnpm test                               # 全部 Node 测试

# Java 后端测试（181 个测试方法，15 个文件）
cd inkos-server && JAVA_HOME=/path/to/jdk21 ./mvnw test
```

Vitest 配置：`packages/core/vitest.config.ts`、`packages/studio/vitest.config.ts`。
JUnit 5 + Mockito：`inkos-server/src/test/java/com/inkos/`。

## 发布流程

推送 `v*` 标签 → CI 构建并在 npm 发布 canary → 冒烟测试 → 发布 `latest` → 创建 GitHub Release。
发布脚本：`scripts/prepare-package-for-publish.mjs` 负责将 workspace 协议替换为实际版本。
`workspace:*` 绝不允许出现在发布的 tarball 中（CI 强制检查）。

## CLI 常见陷阱

- 项目只有一本书时，book-id 可省略（自动检测）
- 任何内容命令加上 `--json` 可输出结构化数据
- `inputGovernanceMode` 默认为 `v2`——在 `inkos.json` 中设为 `"legacy"` 可跳过 plan+compose
- 优先使用 `--api-key-env <VAR>` 而非 `--api-key <literal>`，避免 API Key 出现在 shell 历史中
- `--service google --model kimi-k2.5` 会报错（服务商与模型不匹配）

## LLM 配置链

Studio 服务配置 → `.inkos/secrets.json` → `~/.inkos/.env` → 项目 `.env` → 环境变量 → CLI 参数。
后者覆盖前者。Studio 完全忽略环境变量（仅使用服务配置和 secrets）。

## 根目录关键文件

| 路径 | 用途 |
|------|---------|
| `skills/SKILL.md` | OpenClaw 技能描述文件 |
| `scripts/verify-no-workspace-protocol.mjs` | 发布前检查脚本 |
| `scripts/prepare-package-for-publish.mjs` | 发布时版本替换脚本 |
| `docs/api-reference.md` | REST API 参考文档 |
| `docs/deployment-guide.md` | 生产部署指南 |
| `docs/user-guide.md` | 用户操作指南 |

## 运行时数据（已 gitignore）

`.inkos/`、`books/`、`inkos.json`、`prompt/`、`CLAUDE.md`——用户项目数据，不属于仓库代码。
