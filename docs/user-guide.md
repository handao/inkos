# InkOS User Guide

InkOS is an AI-powered novel writing platform. This guide covers both the **uni-app frontend** (H5/iOS/Android) and the **CLI** for advanced users.

---

## 1. Getting Started

### Accessing the Platform

| Method | URL / Command | Notes |
|--------|---------------|-------|
| Web (H5) | `http://localhost:4567` | Local dev |
| iOS/Android | uni-app build | `npm run build:app` |
| CLI | `inkos` | Requires Node.js |

### Architecture Overview

```
User → Frontend (uni-app) → REST API → Spring Boot → MySQL
                                     → AI Pipeline → LLM Providers
```

---

## 2. Creating an Account

### Registration Flow

1. Open the app → **Login** page
2. Tap **Register**
3. Enter your email → Tap **Send Code**
4. Check your email for a 6-digit verification code
5. Enter code, password (6+ chars), and nickname
6. Tap **Register**

### Whitelist

If registration fails with "邮箱不在白名单中", the admin must add your email domain to the whitelist (see Admin Panel section).

### Login

- Use email + password
- Session persists across app restarts
- Token auto-refresh handles expiry

---

## 3. Creating a Book

### Web/Mobile App

1. Go to **文库** (Library) tab
2. Tap **+** or **新建作品**
3. Enter:
   - **Title** — Book title
   - **Genre** — Genre (e.g., 玄幻, 仙侠, 都市)
   - **Language** — Writing language
4. Tap **Create**

### CLI

```bash
inkos book create --title "吞天魔帝" --genre xuanhuan
inkos book create --title "My Novel" --genre fantasy --lang en
```

### Using a Brief (CLI)

Create a book from your ideas document:

```bash
inkos book create --title "吞天魔帝" --genre xuanhuan --brief my-ideas.md
```

The Architect agent generates the story bible and rules from your brief.

### Book Status

| Status | Description |
|--------|-------------|
| draft | Planning phase |
| ongoing | Actively writing |
| completed | Finished |

---

## 4. Writing Chapters

### AI-Assisted Writing

The AI pipeline generates chapters through a multi-agent process.

#### In the App

1. Open a book → you'll see the chapter list
2. Tap **AI Writing** or go to the session tab
3. Type messages like:
   - "Write the next chapter"
   - "Continue from chapter 5"
   - "Write a chapter where the protagonist discovers the ancient tomb"
4. The AI generates a draft which appears in the session

#### Via CLI (Full Pipeline)

```bash
inkos write next           # Write next chapter (auto-detects book)
inkos write next --count 5 # Write 5 chapters in sequence
inkos write next --words 3000  # Target 3000 words
```

#### Via CLI (Atomic Commands)

```bash
inkos plan chapter 吞天魔帝 --context "本章重点写师徒矛盾"
inkos compose chapter 吞天魔帝
inkos draft 吞天魔帝
inkos audit 吞天魔帝 31
inkos revise 吞天魔帝 31
```

### Understanding the Pipeline

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│ Planner  │──▶│Composer │──▶│ Writer  │──▶│ Auditor │──▶│ Reviser │
│ (intent) │   │(context)│   │ (draft) │   │ (check) │   │ (fix)   │
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
                                                  │
                                          ┌───────▼────────┐
                                          │  Normalizer    │
                                          │ (length adjust)│
                                          └────────────────┘
```

### Chapter Status

| Status | Description |
|--------|-------------|
| draft | Created but not generated |
| generated | AI-generated draft |
| revised | After audit + revision cycle |
| published | Approved and final |

---

## 5. Managing LLM Providers

### What Are LLM Providers?

LLM (Large Language Model) providers are the AI services that power writing. InkOS supports 43 providers including OpenAI, Google Gemini, Moonshot (Kimi), DeepSeek, and more.

### Configuring Providers (In App)

1. Go to **设置** (Settings) → **LLM 服务配置**
2. Tap **+ Add Service**
3. Select a provider (e.g., Moonshot, Google Gemini, OpenAI)
4. Enter:
   - **Label** — Display name
   - **Base URL** — API endpoint (pre-filled for common providers)
   - **Default Model** — e.g., `kimi-k2.5`, `gemini-2.5-flash`
   - **API Key** — Your API key (stored securely)
5. Tap **Test Connection** to verify
6. Save

### Configuring Providers (CLI)

```bash
# Global env config
inkos config set-global \
  --provider moonshot \
  --base-url https://api.moonshot.cn/v1 \
  --api-key sk-your-key \
  --model kimi-k2.5

# One-time override for a command
inkos write next --service google --model gemini-2.5-flash
```

### Multi-Model Routing

Assign different models to different agents:

```bash
inkos config set-model writer gpt-4o --provider openai
inkos config set-model auditor claude-3-haiku --provider anthropic
inkos config show-models
```

### Provider Bank

InkOS includes built-in configurations for 43 providers with 767 model cards. The provider bank knows each provider's base URL, API protocol, and supported models.

---

## 6. Review Cycle (Audit → Revise)

### How Review Works

After the Writer generates a draft, the **Auditor** checks it against 32+ quality dimensions:

| Category | What's Checked |
|----------|----------------|
| Character memory | Characters remembering things they shouldn't |
| Plot consistency | Timeline, locations, logic |
| Resource tracking | Items, money, resources |
| Foreshadowing | Hooks paid off vs. abandoned |
| Narrative rhythm | Pacing, chapter structure |
| Emotional arcs | Character growth consistency |
| Writing style | AI-味 detection, repetitive patterns |

### Review Results

| Outcome | Action |
|---------|--------|
| Pass | Chapter saved as-is |
| Minor issues | Notes added, chapter saved |
| Major issues | Automatic revision (1 round by default) |
| Critical | Paused for human review |

### In the App

- Session messages show audit results
- Chapter status updates (generated → revised → published)
- Review notes are visible in chapter details

### Via CLI

```bash
# Review pending drafts
inkos review list

# Approve all pending
inkos review approve-all

# Manual audit of specific chapter
inkos audit 吞天魔帝 31

# Manual revision
inkos revise 吞天魔帝 31
```

### Adjusting Review Retries

```bash
# Increase auto-revision rounds (default: 1)
inkos config set writing.reviewRetries 3
```

---

## 7. Library & Reading

### Library Tab

- Lists all your books
- Shows status, chapter count, last updated
- Pull down to refresh

### Reading Interface

- Tap a book → **Reader** view
- Swipe or tap to turn pages
- Shows chapter title, word count
- Reading progress is tracked

### Search

- Search across all books by title

---

## 8. Analytics

The **Analytics** tab shows:
- Total books and chapters
- Writing progress over time
- Genre distribution
- Word count statistics

---

## 9. Short Story Writing (CLI)

Generate a complete short story in one command:

```bash
inkos short run \
  --direction "都市短篇 婚姻反转 女主证据反杀" \
  --chapters 12 \
  --chars 1000
```

Output: `shorts/<story-name>/final/` containing:
- `full.md` — Complete story
- `sales-package.md` — Blurb and selling points
- `cover-prompt.md` — AI cover generation prompt
- `cover.png` — Generated cover (if cover service configured)

### Cover Generation

```bash
# Set up cover service in Studio → Model Config
# Then generate in chat or via CLI
```

---

## 10. Admin Panel

### Access

The admin panel is available at the **管理中心** tab (visible to admin users only).

### Features

#### User Management
| Feature | Description |
|---------|-------------|
| List users | View all registered users |
| Search | Filter by email/nickname |
| Status | Enable or disable accounts |

#### Whitelist Management
| Feature | Description |
|---------|-------------|
| List | View all approved email patterns |
| Add | Add a new email pattern (`*@example.com`) |
| Remove | Delete a pattern |
| Note | Add descriptions for each pattern |

#### How Whitelist Works

- When the whitelist is **empty**, all emails can register (open registration)
- When **any patterns exist**, only matching emails can register
- Wildcard `*` matches any prefix: `*@company.com`
- Exact patterns match exactly: `user@gmail.com`

### Making a User Admin

```sql
-- Direct DB update (admin-only operation)
UPDATE users SET role = 'admin' WHERE email = 'admin@example.com';
```

---

## 11. Export

### CLI Export

```bash
# Export as TXT
inkos export 吞天魔帝

# Export as EPUB (for Kindle/phones)
inkos export 吞天魔帝 --format epub

# Export approved chapters only
inkos export 吞天魔帝 --approved-only

# Custom output path
inkos export 吞天魔帝 --output ./exports/
```

---

## 12. CLI Reference

### Essential Commands

| Command | Description |
|---------|-------------|
| `inkos init [name]` | Initialize a project |
| `inkos book create` | Create a new book |
| `inkos write next` | Full pipeline: plan → compose → write → audit → revise |
| `inkos status` | Show project status |
| `inkos doctor` | Diagnose configuration issues |
| `inkos export` | Export book to file |
| `inkos studio` | Start the web Studio |

### Tips

- In single-book projects, book IDs are auto-detected
- Add `--json` to any command for structured output
- Use `--api-key-env <VAR>` instead of `--api-key` for security
- Run `inkos doctor` first if something doesn't work

---

## 13. Truth Files (Advanced)

InkOS maintains 7 truth files per book for long-term memory:

| File | Purpose |
|------|---------|
| `current_state.md` | World state: locations, relationships, info |
| `particle_ledger.md` | Resource tracking: items, money |
| `pending_hooks.md` | Unresolved foreshadowing |
| `chapter_summaries.md` | Per-chapter summaries |
| `subplot_board.md` | Subplot tracking |
| `emotional_arcs.md` | Character emotional arcs |
| `character_matrix.md` | Character interaction matrix |

These files are automatically maintained by the AI pipeline. You can view them in the CLI:

```bash
inkos status                  # Shows truth file health
cat books/book-id/story/*.md  # Direct file access
```

---

## 14. Fan Fiction (CLI)

```bash
# Create a fanfic book from source material
inkos fanfic init --from source.txt --mode canon

# Modes: canon (canon continuation), au (alternate universe),
#         ooc (character重塑), cp (CP-oriented)

# Import existing chapters for continuation
inkos import chapters --from my-novel.txt
```

---

## 15. Style Imitation (CLI)

```bash
# Analyze a reference text for style fingerprint
inkos style analyze reference.txt

# Import the style into a book
inkos style import reference.txt 吞天魔帝
```

---

## 16. Notifications & Daemon (CLI)

```bash
# Start daemon for unattended writing
inkos up

# Stop daemon
inkos down

# Configure notifications
export INKOS_TELEGRAM_BOT_TOKEN=xxx
export INKOS_TELEGRAM_CHAT_ID=xxx
export INKOS_FEISHU_WEBHOOK_URL=xxx
export INKOS_WECOM_WEBHOOK_URL=xxx
```

---

## Support

- GitHub Issues: https://github.com/Narcooo/inkos/issues
- WeChat Group: See QR code in README.md
- License: AGPL-3.0
