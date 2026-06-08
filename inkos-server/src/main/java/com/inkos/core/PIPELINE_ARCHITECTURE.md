# InkOS Pipeline Architecture (Phase 3 Migration Reference)

## Overview

The InkOS writing pipeline is a sequential agent pipeline that:
1. Plans a chapter → 2. Composes runtime context → 3. Writes a draft → 
4. Reviews/revises → 5. Validates state → 6. Persists artifacts

It lives in `packages/core/src/pipeline/` with the main orchestrator being `PipelineRunner` (3438 lines).

## Key Classes & Interfaces

### PipelineRunner (`runner.ts`)
The central orchestrator. It:
- Holds a `PipelineConfig` (client, model, projectRoot, overrides, etc.)
- Uses `StateManager` for book state persistence
- Maintains a cache of `LLMClient` instances per agent override
- Provides atomic operations: `writeDraft`, `auditDraft`, `reviseDraft`, `planChapter`, `composeChapter`
- Provides compound operations: `writeNextChapter` (write + audit + revise + validate + persist)
- Provides book lifecycle: `initBook`, `initFanficBook`, `reviseFoundation`, `importChapters`
- Uses agents: `ArchitectAgent`, `PlannerAgent`, `WriterAgent`, `ContinuityAuditor`, `ReviserAgent`, `LengthNormalizerAgent`, `ChapterAnalyzerAgent`, `StateValidatorAgent`, `FoundationReviewerAgent`

### Scheduler (`scheduler.ts`)
Automated cron-driven pipeline for unattended writing:
- Runs write cycles on a cron schedule
- Runs radar scans on a separate cron schedule
- Manages concurrent book processing (maxConcurrentBooks)
- Implements quality gates: retry on audit failure, pause after consecutive failures, dimension clustering detection
- Daily chapter cap enforcement
- Detection loop (AI detection + auto-rewrite) after successful audits

### Agent Loop (`agent.ts`)
LLM-driven agent that uses tool-calling to orchestrate the pipeline:
- `runAgentLoop()` creates an Agent with 18 tools (write_draft, audit_chapter, revise_chapter, etc.)
- Uses `chatWithTools()` from provider.ts for function-calling LLM calls
- Each tool maps to a `PipelineRunner` method via `executeAgentTool()`
- Max 20 turns per loop
- Systemic prompt contains full tool documentation and rules for the LLM

## Pipeline Flow (writeNextChapter)

1. **Lock acquisition** — per-book mutex via `StateManager.acquireBookLock()`
2. **Control document loading** — `ensureControlDocuments()`
3. **Governed artifacts creation** — If mode != "legacy":
   - `PlannerAgent.planChapter()` → generates ChapterIntent, ChapterMemo
   - `ComposerAgent.composeGovernedChapter()` → generates ContextPackage, RuleStack, ChapterTrace
   - Persisted to disk for reuse
4. **Write** — `WriterAgent.writeChapter()` with LLM call, returns content + truth file deltas
5. **Length normalization** — if outside hard range, `LengthNormalizerAgent` expands/compresses
6. **Review cycle** — `runChapterReviewCycle()`:
   - Iterative audit → revise loop (max `writingReviewRetries` times)
   - ContinuityAuditor (LLM-based 32-dimension audit)
   - AI tell detection (heuristic)
   - Sensitive word detection
   - Post-write validation (paragraph shape, rules compliance)
   - Hook ledger validation
7. **Hook promotion pass** — lightweight ledger parse, no LLM calls
8. **Persistence** — `persistChapterArtifacts()`:
   - Save chapter file (.md)
   - Save truth files (current_state, pending_hooks, particle_ledger, chapter_summaries, etc.)
   - Sync legacy structured state from markdown
   - Rebuild narrative memory index (SQLite)
   - Update chapter index
   - Create state snapshot
   - Sync current state fact history
9. **Notification** — webhook dispatch, logging

## State Flow Between Agents

State flows through **several mechanisms**:

### 1. On-disk Truth Files
The primary persistence layer. Each book has truth files at `books/{bookId}/story/`:
- `current_state.md` — narrative state (location, protagonist, enemies, known truths)
- `particle_ledger.md` — resource tracking (for numerical system genres)
- `pending_hooks.md` — foreshadowing hooks with status/progress/expected payoff
- `chapter_summaries.md` — per-chapter compressed summaries
- `subplot_board.md` — subplot tracking
- `emotional_arcs.md` — character emotional arcs
- `character_matrix.md` — character interaction matrix
- `style_profile.json` / `style_guide.md` — writing style fingerprint
- `audit_drift.md` — auto-generated correction guidance for next chapter

### 2. AgentContext
Passed to every agent, contains: `{ client, model, projectRoot, bookId, logger, onStreamProgress }`

### 3. WriteChapterInput
The structured input bundle containing:
- `externalContext` — user-provided guidance
- `chapterIntent` — goal, must-keep, must-avoid, style emphasis
- `chapterMemo` — detailed body with thread references
- `contextPackage` — selected context sources with excerpts
- `ruleStack` — layered rules (hard/soft/diagnostic) with override edges
- `lengthSpec` — length targets

### 4. WriteChapterOutput
The structured output from WriterAgent:
- `content` — chapter body text
- `title`, `wordCount`
- `updatedState`, `updatedLedger`, `updatedHooks` — truth file deltas
- `runtimeStateDelta` — structured state changes
- `chapterSummary` — ChapterSummaryRow
- `tokenUsage` — LLM token accounting

### 5. ChapterIndex
Per-book JSON array of `ChapterMeta` entries tracking status, word count, audit issues, etc.

### 6. State Snapshots
Per-chapter structured state derived from markdown truth files, stored at `story/state/` directory.

### 7. SQLite MemoryDB
Narrative memory index for efficient querying (summaries + hooks).

## Key Interfaces to Implement Next

### LLM Endpoint Migration
- Port 43 endpoint definitions from `packages/core/src/llm/providers/endpoints/*.ts`
- Each defines an `InkosEndpoint` with models array, baseUrl, API protocol, temperature ranges
- Need `EndpointRegistry` to load and resolve them

### Pipeline Engine
- Port `PipelineRunner` with all agent orchestration logic
- Port `Scheduler` for automated writing cycles
- Port `Agent Loop` for LLM-driven tool calling

### Agents (in priority order):
1. **PlannerAgent** — chapter intent generation via LLM
2. **ComposerAgent** — context selection + rule stack assembly
3. **WriterAgent** — draft generation via LLM
4. **ContinuityAuditor** — 32-dimension audit via LLM
5. **ReviserAgent** — text revision via LLM
6. **LengthNormalizerAgent** — length expansion/compression via LLM
7. **ChapterAnalyzerAgent** — truth file extraction from chapter text (for imports)
8. **ArchitectAgent** — book foundation generation
9. **FoundationReviewerAgent** — foundation quality review
10. **StateValidatorAgent** — truth file consistency validation
11. **RadarAgent** — market trend scanning

## Design Decisions for Java Migration

1. **Domain models vs JPA entities** — `core.model` package contains pipeline DTOs/domain objects (not persisted). `entity` package has JPA entities.
2. **CompletableFuture** — match the TS async patterns. All LLM calls and pipeline operations are async.
3. **Provider abstraction** — `LlmProvider` interface with `GenericLlmProvider` implementation. Specific providers (OpenAI, Anthropic, Google) can extend as needed.
4. **Endpoint registry** — static bank of 43+ provider definitions with model cards.
5. **State management** — Filesystem-based state (markdown truth files) should use Java NIO. Structured state can use SQLite via JDBC.
6. **Agent system** — Base agent class with shared `AgentContext`. Each agent specializes via composition with the LLM provider.
