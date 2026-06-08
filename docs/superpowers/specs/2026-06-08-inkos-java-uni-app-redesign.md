# InkOS Java + uni-app 重构 & 商业化改造设计文档

## 1. 概述

将 InkOS 从 TypeScript 单体仓库迁移到 **Java (Spring Boot 3.x) + uni-app (Vue 3)** 全栈架构，并改造为商业化 SaaS 产品。

### 目标架构

```
inkos/
├── inkos-server/          # Spring Boot 3.x REST API + MySQL
├── inkos-app/             # uni-app (Vue 3 + Pinia) 全平台前端
│   ├── src/pages/         # H5 / iOS / Android / 小程序
│   └── ...
├── inkos-admin/           # 独立管理后台（后续实现）
├── inkos-cli/             # 保持现有 Node.js CLI（暂不迁移）
└── docs/superpowers/specs/ # 设计文档
```

## 2. 三阶段迁移路线

### Phase 1A：MVP 后端（当前阶段）
- Spring Boot 3.x 项目搭建
- MySQL 数据库 + JPA/Flyway
- 用户系统（邮箱注册 + 验证码 + 白名单）
- JWT 认证 + 多租户数据隔离
- 核心业务 CRUD API（书籍/章节/会话/消息）
- LLM 服务商配置 + 封面配置 API
- 管理 API（用户/白名单管理）

### Phase 1B：管理后台
- 独立管理面板
- 用户管理、白名单管理
- 后续：订阅/支付系统

### Phase 2：uni-app 前端
- 全新信息架构，移动端优先
- 全平台适配（H5 / iOS / Android / 小程序）

### Phase 3：Core 引擎迁移
- 数据模型层 → LLM Provider → Pipeline → Agents
- 按模块分批移植，分步替换

## 3. 数据库设计

### 3.1 用户 & 认证

```sql
-- 用户表
CREATE TABLE user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(100),
  avatar_url VARCHAR(500),
  role VARCHAR(20) NOT NULL DEFAULT 'user',    -- user / admin
  status VARCHAR(20) NOT NULL DEFAULT 'active', -- active / disabled
  last_login_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 邮箱验证码
CREATE TABLE verification_code (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  code VARCHAR(6) NOT NULL,
  type VARCHAR(20) NOT NULL,                   -- REGISTER / RESET_PASSWORD
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 邮箱白名单
CREATE TABLE allowed_email (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(200),
  created_by BIGINT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 3.2 业务核心

```sql
-- 书籍
CREATE TABLE book (
  id VARCHAR(36) PRIMARY KEY,                  -- UUID
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  genre VARCHAR(50),
  status VARCHAR(20) DEFAULT 'draft',
  language VARCHAR(10) DEFAULT 'zh',
  fanfic_mode VARCHAR(50),
  chapters_written INT DEFAULT 0,
  outline TEXT,
  cover_image_url VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 章节
CREATE TABLE chapter (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id VARCHAR(36) NOT NULL,
  user_id BIGINT NOT NULL,
  chapter_number INT NOT NULL,
  title VARCHAR(200),
  content LONGTEXT,
  word_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'draft',          -- draft / published / revising
  version INT DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 知识库（Story Bible / Truth Files）
-- file_path 为书籍范围内的逻辑路径标识，如 "story_bible.md"、"characters/hero.md"
CREATE TABLE truth_file (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id VARCHAR(36) NOT NULL,
  user_id BIGINT NOT NULL,
  file_path VARCHAR(500) NOT NULL,  -- 逻辑路径，书籍范围内唯一
  content LONGTEXT,
  content_type VARCHAR(50),
  version INT DEFAULT 1,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

### 3.3 会话 & 消息

```sql
-- Agent 会话
CREATE TABLE agent_session (
  session_id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  book_id VARCHAR(36),                         -- NULL = 项目级会话
  title VARCHAR(200),
  mode VARCHAR(20) DEFAULT 'chat',             -- chat / book-create
  is_draft BOOLEAN DEFAULT TRUE,
  is_streaming BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

-- 会话消息
CREATE TABLE agent_message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(36) NOT NULL,
  user_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL,                   -- user / assistant / system
  content JSON,
  tool_calls JSON,
  sort_order INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES agent_session(session_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

### 3.4 配置 & 服务

```sql
-- LLM 服务商
CREATE TABLE llm_service (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  service_type VARCHAR(50) NOT NULL,           -- kkaiapi / openai / google / custom
  label VARCHAR(100),
  base_url VARCHAR(500),
  api_type VARCHAR(20),                        -- images / responses / gemini
  models JSON,
  default_model VARCHAR(100),
  is_default BOOLEAN DEFAULT FALSE,
  sort_order INT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 密钥（加密存储）
CREATE TABLE secret (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  service_key VARCHAR(100) NOT NULL,           -- cover:openai / service:openai
  encrypted_key VARCHAR(500) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id),
  UNIQUE KEY uk_user_service (user_id, service_key)
);

-- 风格配置
CREATE TABLE style_profile (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  book_id VARCHAR(36) NOT NULL,
  user_id BIGINT NOT NULL,
  name VARCHAR(100),
  style_prompt TEXT,
  is_active BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
```

### 3.5 Cover 配置

Cover 配置存储在 `llm_service` 表中。当 `service_type = 'custom'` 时，`base_url`、`api_type`、`default_model` 字段存储自定义封面提供商的配置。对于预置服务商（kkaiapi/openai/google），使用其预设配置。

### 3.6 索引建议

```sql
-- 常用查询索引
CREATE INDEX idx_book_user_id ON book(user_id);
CREATE INDEX idx_chapter_book_id ON chapter(book_id);
CREATE INDEX idx_session_user_id ON agent_session(user_id);
CREATE INDEX idx_session_book_id ON agent_session(book_id);
CREATE INDEX idx_message_session_id ON agent_message(session_id);
CREATE INDEX idx_truth_file_book_id ON truth_file(book_id);
CREATE INDEX idx_verification_email ON verification_code(email, type);
CREATE INDEX idx_allowed_email_pattern ON allowed_email(email_pattern);
CREATE INDEX idx_secret_user_service ON secret(user_id, service_key);
```

### 3.7 Flyway 迁移顺序

1. V1__create_user_tabels.sql（user, verification_code, allowed_email）
2. V2__create_book_tabels.sql（book, chapter, truth_file）
3. V3__create_session_tabels.sql（agent_session, agent_message）
4. V4__create_config_tabels.sql（llm_service, secret, style_profile）

## 4. API 设计

### 4.1 认证 (prefix: /api/v1/auth)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/send-code | 发送邮箱验证码（校验白名单 + 是否已注册） |
| POST | /auth/register | 校验验证码 + 注册（email, code, password, nickname） |
| POST | /auth/login | 登录，返回 JWT（access_token + refresh_token） |
| POST | /auth/refresh | 刷新 Token |
| POST | /auth/logout | 登出 |
| GET | /auth/me | 获取当前用户信息 |

### 4.2 书籍 (prefix: /api/v1/books)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /books | 书籍列表（分页 + 搜索 + 筛选） |
| POST | /books | 创建书籍 |
| GET | /books/{id} | 书籍详情 |
| PUT | /books/{id} | 更新书籍 |
| DELETE | /books/{id} | 删除书籍 |
| GET | /books/{id}/chapters | 章节列表 |
| POST | /books/{id}/chapters | 创建章节 |
| GET | /books/{id}/truth | Truth Files 列表 |
| PUT | /books/{id}/truth/{path} | 保存/更新 Truth File |
| DELETE | /books/{id}/truth/{path} | 删除 Truth File |

### 4.3 会话 (prefix: /api/v1/sessions)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /sessions | 会话列表（按 bookId 筛选） |
| POST | /sessions | 创建草稿会话 |
| GET | /sessions/{id} | 会话详情（含消息） |
| PUT | /sessions/{id} | 更新会话（重命名） |
| DELETE | /sessions/{id} | 删除会话 |
| POST | /sessions/{id}/messages | 发送消息（SSE 流式返回） |
| GET | /sessions/{id}/messages | 获取消息历史 |

### 4.4 LLM 配置 (prefix: /api/v1/llm)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /llm/services | 服务商列表（含预置选项） |
| POST | /llm/services | 添加自定义服务商 |
| PUT | /llm/services/{id} | 更新服务商配置 |
| DELETE | /llm/services/{id} | 删除服务商 |
| PUT | /llm/secrets/{serviceKey} | 保存 API 密钥 |
| GET | /llm/secrets/{serviceKey} | 检查密钥是否存在 |
| GET | /llm/cover/config | 封面配置 |
| PUT | /llm/cover/config | 更新封面配置 |

### 4.5 管理 (prefix: /api/v1/admin)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /admin/users | 用户列表（分页） |
| PUT | /admin/users/{id}/status | 启用/禁用用户 |
| GET | /admin/allowed-emails | 白名单列表 |
| POST | /admin/allowed-emails | 添加白名单 |
| DELETE | /admin/allowed-emails/{id} | 删除白名单 |

## 5. 认证 & 安全

### 5.1 JWT 认证流程
- 登录成功后返回 access_token（15 分钟过期）+ refresh_token（7 天过期）
- access_token 包含：user_id, email, role
- refresh_token 存储在数据库或 Redis
- 每次请求通过 Authorization: Bearer 头传递 JWT
- Spring Security Filter 校验 Token

### 5.2 多租户数据隔离
- 所有业务表包含 user_id
- Repository 层自动追加 user_id 过滤条件
- Service 层从 SecurityContext 获取当前用户
- 防止越权访问（用户 A 无法访问用户 B 的数据）

### 5.3 邮箱验证码
- 6 位数字验证码
- 有效期 10 分钟
- 同一邮箱 60 秒内不可重复发送
- 使用 Spring Mail（JavaMailSender）发送
- SMTP 可配置（支持阿里云邮件 / SendGrid / QQ邮箱等）

### 5.4 白名单机制
- 注册时检查邮箱是否匹配 allowed_email 表中的 pattern
- 支持精确匹配和域名通配（如 `@example.com`）
- 白名单由管理员管理

## 6. 后端架构 (Spring Boot 3.x)

### 6.1 技术栈
- Spring Boot 3.x + Spring Web MVC
- Spring Security + JWT (jjwt)
- Spring Data JPA + Hibernate
- MySQL 8 + Flyway
- Spring Mail (JavaMailSender)
- Lombok
- MapStruct (DTO 映射)

### 6.2 项目模块结构

```
com.inkos
├── InkOsApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── CorsConfig.java
├── controller/
│   ├── AuthController.java
│   ├── BookController.java
│   ├── ChapterController.java
│   ├── SessionController.java
│   ├── LlmServiceController.java
│   └── AdminController.java
├── service/
│   ├── AuthService.java
│   ├── EmailService.java
│   ├── BookService.java
│   ├── SessionService.java
│   └── LlmServiceManager.java
├── repository/
│   ├── UserRepository.java
│   ├── BookRepository.java
│   └── ...
├── entity/
│   ├── User.java
│   ├── Book.java
│   └── ...
├── dto/
│   ├── request/
│   └── response/
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserContext.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── BusinessException.java
```

## 7. 前端信息架构 (uni-app)

### 7.1 TabBar 导航
- **首页** — 项目概览、最近活动、快捷入口
- **文库** — 书籍列表、分类筛选、搜索
- **分析** — 写作统计、进度追踪
- **我的** — 账户信息、设置、退出

### 7.2 页面路由

| 路由 | 页面 | 说明 |
|------|------|------|
| /pages/index/index | 首页 | Dashboard |
| /pages/library/index | 文库 | 书籍列表 |
| /pages/library/detail | 书籍详情 | 设置、章节、Knowledge |
| /pages/workspace/index | 写作台 | 核心 AI 对话界面 |
| /pages/reader/index | 阅读器 | 章节阅读 |
| /pages/analytics/index | 分析 | 写作统计 |
| /pages/profile/index | 我的 | 个人中心 |
| /pages/settings/index | 设置 | 通用设置 |
| /pages/settings/services | 服务商管理 | LLM 配置 |
| /pages/auth/login | 登录 | |
| /pages/auth/register | 注册 | |
| /pages/admin/users | 用户管理 | 管理员 |
| /pages/admin/whitelist | 白名单管理 | 管理员 |

### 7.3 技术栈
- uni-app (Vue 3 + Composition API)
- Pinia 状态管理
- TypeScript
- uni-ui 组件库
- 自定义主题系统

## 8. Core 引擎迁移策略 (Phase 3)

### 步骤 1：数据模型层
- Zod schema → Java Record/POJO + Jakarta Validation
- TypeScript 类型 → Java 类型
- 与 JPA Entity 层统一

### 步骤 2：LLM Provider 层
- 43 个 Endpoint 配置文件 → 策略模式
- HttpClient 替换 undici
- SSE/Streaming 支持

### 步骤 3：Pipeline 引擎
- 10-Agent 管线 → Spring 状态机
- 异步执行 + SSE 事件通知

### 步骤 4：Agent 实现
- 35 个 Agent → Spring Bean
- 公共基类 + 依赖注入
- 可插拔设计

## 9. Phase 1A 开发顺序

1. **Spring Boot 项目脚手架** — pom.xml, application.yml, 基础配置
2. **用户认证** — User entity → JPA → Security → JWT → Auth API
3. **邮件验证码** — 发送 + 校验 + 白名单
4. **管理 API** — 用户管理 + 白名单管理
5. **业务 CRUD** — Book → Chapter → Session → Message
6. **LLM 配置** — 服务商管理 + 密钥管理 + 封面配置
7. **测试 & 完善**

## 10. 约束条件
- CLI 保持现有 Node.js 实现，暂不迁移
- 不处理支付/订阅（后续 Phase）
- 管理后台先只做 API，管理页面后续实现
- 旧 TypeScript Core 保留，直到 Java 版本完全替代
