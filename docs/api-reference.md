# InkOS API Reference

Base URL: `http://localhost:8080/api/v1`

Response format (all endpoints):

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

Error response:

```json
{
  "code": 40100,
  "message": "未登录或 Token 已过期"
}
```

---

## Authentication

### Send Verification Code

```
POST /auth/send-code
```

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response:** `200`
```json
{
  "code": 200,
  "message": "success"
}
```

**Errors:**
| Code | Message |
|------|---------|
| 40904 | 验证码发送太频繁 (60s cooldown) |
| 40901 | 邮箱已注册 |
| 40902 | 邮箱不在白名单中 |

### Register

```
POST /auth/register
```

**Request:**
```json
{
  "email": "user@example.com",
  "code": "123456",
  "password": "mypassword",
  "nickname": "MyName"
}
```

**Response:** `201`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "nickname": "MyName",
      "avatarUrl": null,
      "role": "user"
    }
  }
}
```

**Errors:**
| Code | Message |
|------|---------|
| 40903 | 验证码错误或已过期 |
| 40901 | 邮箱已注册 |
| 40003 | 密码格式不正确 |

### Login

```
POST /auth/login
```

**Request:**
```json
{
  "email": "user@example.com",
  "password": "mypassword"
}
```

**Response:** `200` — Same structure as register.

**Errors:**
| Code | Message |
|------|---------|
| 40101 | 邮箱或密码错误 |
| 40905 | 账户已被禁用 |

### Refresh Token

```
POST /auth/refresh
```

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:** `200` — Returns new access + refresh tokens.

**Errors:**
| Code | Message |
|------|---------|
| 40100 | 未登录或 Token 已过期 |

### Get Current User

```
GET /auth/me
```

**Headers:** `Authorization: Bearer <accessToken>`

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "MyName",
    "avatarUrl": null,
    "role": "user"
  }
}
```

---

## Books

### List Books

```
GET /books?page=0&size=20
```

**Headers:** `Authorization: Bearer <accessToken>`

**Query Params:**
| Param | Type | Default | Description |
|-------|------|---------|-------------|
| page | int | 0 | Page number (0-indexed) |
| size | int | 20 | Page size |

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "uuid-string",
        "title": "吞天魔帝",
        "genre": "xuanhuan",
        "status": "ongoing",
        "language": "zh",
        "chaptersWritten": 31,
        "createdAt": "2025-01-01T00:00:00",
        "updatedAt": "2025-06-01T00:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "total": 1,
    "totalPages": 1
  }
}
```

### Create Book

```
POST /books
```

**Request:**
```json
{
  "title": "吞天魔帝",
  "genre": "xuanhuan",
  "language": "zh"
}
```

**Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | yes | Book title |
| genre | string | no | Genre (xuanhuan, xianxia, etc.) |
| language | string | no | Language code (default: `zh`) |

**Response:** `201`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid-string",
    "title": "吞天魔帝",
    "genre": "xuanhuan",
    "status": "draft",
    "language": "zh",
    "chaptersWritten": 0,
    "createdAt": "2025-06-08T00:00:00",
    "updatedAt": "2025-06-08T00:00:00"
  }
}
```

### Get Book

```
GET /books/{id}
```

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid-string",
    "title": "吞天魔帝",
    "genre": "xuanhuan",
    "status": "ongoing",
    "language": "zh",
    "fanficMode": null,
    "chaptersWritten": 5,
    "outline": "...",
    "coverImageUrl": null,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-06-01T00:00:00",
    "chapters": [
      {
        "id": 1,
        "bookId": "uuid-string",
        "chapterNumber": 1,
        "title": "第一章 重生",
        "content": "...",
        "wordCount": 4520,
        "status": "published",
        "version": 1,
        "createdAt": "2025-01-01T00:00:00",
        "updatedAt": "2025-01-01T00:00:00"
      }
    ]
  }
}
```

### Update Book

```
PUT /books/{id}
```

**Request:**
```json
{
  "title": "新书名",
  "genre": "xianxia",
  "status": "ongoing",
  "outline": "大纲内容"
}
```

**Fields:** All fields optional.

### Delete Book

```
DELETE /books/{id}
```

**Response:** `204` (No Content, but wrapped in ApiResponse)

---

## Chapters

### List Chapters

```
GET /books/{bookId}/chapters
```

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "bookId": "uuid-string",
      "chapterNumber": 1,
      "title": "第一章 重生",
      "content": "...",
      "wordCount": 4520,
      "status": "published",
      "version": 1,
      "createdAt": "2025-01-01T00:00:00",
      "updatedAt": "2025-01-01T00:00:00"
    }
  ]
}
```

### Create Chapter

```
POST /books/{bookId}/chapters
```

**Request:**
```json
{
  "chapterNumber": 6,
  "title": "第六章 突破",
  "content": "章节正文..."
}
```

**Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| chapterNumber | int | yes | Chapter number (must be unique per book) |
| title | string | yes | Chapter title |
| content | string | no | Chapter content (optional, can write later) |

### Get Chapter

```
GET /books/{bookId}/chapters/{chapterId}
```

**Note:** The `chapterId` is the auto-increment numeric ID (Long).

### Update Chapter

```
PUT /books/{bookId}/chapters/{chapterId}
```

**Request:**
```json
{
  "title": "新标题",
  "content": "新正文..."
}
```

**Fields:** All fields optional.

### Delete Chapter

```
DELETE /books/{bookId}/chapters/{chapterId}
```

**Response:** `204`

---

## Sessions (AI Writing Sessions)

### List Sessions

```
GET /sessions?bookId=uuid
```

**Query Params:**
| Param | Type | Description |
|-------|------|-------------|
| bookId | string | Filter by book (optional) |

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "sessionId": "uuid-string",
      "bookId": "uuid-string",
      "title": "写作会话",
      "mode": "chat",
      "isDraft": false,
      "isStreaming": false,
      "createdAt": "2025-06-08T00:00:00",
      "updatedAt": "2025-06-08T00:00:00"
    }
  ]
}
```

### Create Session

```
POST /sessions
```

**Request:**
```json
{
  "bookId": "uuid-string",
  "title": "世界构建讨论",
  "mode": "chat"
}
```

**Fields:** All fields optional.

### Get Session

```
GET /sessions/{id}
```

**Response:** `200` — Same as session list but includes `messages` array:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "...",
    "bookId": "...",
    "title": "...",
    "mode": "chat",
    "isDraft": false,
    "isStreaming": false,
    "createdAt": "...",
    "updatedAt": "...",
    "messages": [
      {
        "id": 1,
        "sessionId": "...",
        "role": "user",
        "content": "\"写下一章\"",
        "toolCalls": null,
        "sortOrder": 0,
        "createdAt": "..."
      }
    ]
  }
}
```

### Update Session

```
PUT /sessions/{id}
```

**Request:**
```json
{
  "title": "新标题",
  "isDraft": false,
  "isStreaming": false
}
```

### Delete Session

```
DELETE /sessions/{id}
```

**Response:** `204`

### Send Message

```
POST /sessions/{id}/messages
```

**Request:**
```json
{
  "content": "帮我写下一章"
}
```

**Response:** `201` — Message response object.

---

## LLM Configuration

### List LLM Services

```
GET /llm/services
```

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "serviceType": "moonshot",
      "label": "Moonshot Kimi",
      "baseUrl": "https://api.moonshot.cn/v1",
      "apiType": "openai",
      "models": "kimi-k2.5,kimi-k2",
      "defaultModel": "kimi-k2.5",
      "isCoverProvider": false,
      "isDefault": true
    }
  ]
}
```

### Save LLM Service

```
POST /llm/services
```

**Request:**
```json
{
  "serviceType": "moonshot",
  "label": "Moonshot Kimi",
  "baseUrl": "https://api.moonshot.cn/v1",
  "apiType": "openai",
  "models": ["kimi-k2.5", "kimi-k2"],
  "defaultModel": "kimi-k2.5",
  "isCoverProvider": false
}
```

**Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| serviceType | string | yes | Provider slug (e.g. `moonshot`) |
| label | string | no | Display name |
| baseUrl | string | no | API endpoint URL |
| apiType | string | no | API protocol (`openai`, etc.) |
| models | string[] | no | Available model list |
| defaultModel | string | no | Default model name |
| isCoverProvider | boolean | no | Whether this is a cover image provider |

### Delete LLM Service

```
DELETE /llm/services/{id}
```

### Check API Secret

```
GET /llm/secrets/{serviceKey}
```

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasKey": true
  }
}
```

### Save API Secret

```
PUT /llm/secrets/{serviceKey}
```

**Request:**
```json
{
  "apiKey": "sk-your-api-key"
}
```

**Note:** In production, API keys should be encrypted at rest. The current implementation stores keys as-is; production deployments must add encryption.

---

## Admin

### List Users

```
GET /admin/users?page=0&size=20
```

**Requires:** `ROLE_ADMIN`

**Response:** `200`
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "email": "user@example.com",
        "nickname": "MyName",
        "role": "user",
        "status": "active",
        "lastLoginAt": "2025-06-08T00:00:00",
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "total": 50,
    "totalPages": 3
  }
}
```

### Update User Status

```
PUT /admin/users/{id}/status
```

**Request:**
```json
{
  "status": "disabled"
}
```

**Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | yes | `active` or `disabled` |

### List Allowed Emails (Whitelist)

```
GET /admin/allowed-emails
```

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "emailPattern": "*@example.com",
      "description": "Company domain",
      "createdBy": 1,
      "createdAt": "2025-01-01T00:00:00"
    }
  ]
}
```

### Add Allowed Email

```
POST /admin/allowed-emails
```

**Request:**
```json
{
  "emailPattern": "*@example.com",
  "description": "Company email domain"
}
```

**Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| emailPattern | string | yes | Email pattern (`*` wildcard supported) |
| description | string | no | Notes |

### Delete Allowed Email

```
DELETE /admin/allowed-emails/{id}
```

---

## Error Codes

| Code | Description | HTTP Status |
|------|-------------|-------------|
| 40000 | 请求参数错误 | 400 |
| 40001 | 邮箱格式不正确 | 400 |
| 40002 | 验证码错误 | 400 |
| 40003 | 密码格式不正确 | 400 |
| 40100 | 未登录或 Token 已过期 | 401 |
| 40101 | 邮箱或密码错误 | 401 |
| 40300 | 权限不足 | 403 |
| 40400 | 资源不存在 | 404 |
| 40900 | 资源冲突 | 409 |
| 40901 | 邮箱已注册 | 409 |
| 40902 | 邮箱不在白名单中 | 409 |
| 40903 | 验证码错误或已过期 | 409 |
| 40904 | 验证码发送太频繁 | 409 |
| 40905 | 账户已被禁用 | 403 |
| 42201 | 服务配置无效 | 422 |
| 42900 | 请求太频繁 | 429 |
| 50000 | 服务器内部错误 | 500 |

## Authentication Flow

```
┌─────────┐          ┌──────────┐          ┌─────────┐
│  Client  │          │  Backend  │          │   DB    │
└────┬────┘          └────┬─────┘          └────┬────┘
     │                    │                     │
     │  POST /auth/login  │                     │
     │───────────────────▶│                     │
     │                    │  Verify credentials │
     │                    │────────────────────▶│
     │                    │◀────────────────────│
     │  {accessToken,     │                     │
     │   refreshToken}    │                     │
     │◀───────────────────│                     │
     │                    │                     │
     │  GET /books        │                     │
     │  Authorization:    │                     │
     │  Bearer <token>    │                     │
     │───────────────────▶│                     │
     │                    │  Validate JWT       │
     │                    │  (in filter, no DB) │
     │  { data: [...] }   │                     │
     │◀───────────────────│                     │
     │                    │                     │
     │  POST /auth/refresh│  (when 401)         │
     │───────────────────▶│                     │
     │  {newTokens}       │                     │
     │◀───────────────────│                     │
```

**Token lifecycle:**
- Access token expires in 15 minutes (900,000ms)
- Refresh token expires in 7 days (604,800,000ms)
- Both use HMAC-SHA256 with the configured `JWT_SECRET`
- On 401, the client should call `/auth/refresh` to get new tokens
- The frontend SDK handles this automatically via `tryRefreshToken()`

## Pagination

Paginated endpoints return:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "total": 100,
  "totalPages": 5
}
```

| Field | Type | Description |
|-------|------|-------------|
| content | array | Page items |
| page | int | Current page (0-indexed) |
| size | int | Page size |
| total | long | Total items across all pages |
| totalPages | int | Total number of pages |

Default page size is 20. Maximum page size is not enforced server-side, but values > 100 are not recommended.

## Common Headers

| Header | Value | Required |
|--------|-------|----------|
| Authorization | Bearer `<accessToken>` | Yes (except auth endpoints) |
| Content-Type | application/json | Yes |

## Rate Limiting

Currently not implemented server-side. The frontend enforces a 60-second cooldown between verification code sends.
