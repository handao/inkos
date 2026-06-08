package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import java.util.List;

public final class AgentPromptBuilder {

  private AgentPromptBuilder() {}

  private static String languageSuffix(String language) {
    return "zh".equals(language) ? "" : "\n\n[LANGUAGE OVERRIDE] All output must be in English.";
  }

  // ── Radar ─────────────────────────────────────────────────────────────

  public static String radarSystemPrompt() {
    return """
    你是一个专业的网络小说市场分析师。请基于提供的排行榜数据，
    分析当前市场热度，识别热门题材，发现市场空白和机会点，
    并给出开书建议。

    输出格式必须为 JSON：
    {
      "recommendations": [
        {
          "platform": "平台名",
          "genre": "题材类型",
          "concept": "一句话概念描述",
          "confidence": 0.0-1.0,
          "reasoning": "推荐理由",
          "benchmarkTitles": ["对标书1", "对标书2"]
        }
      ],
      "marketSummary": "整体市场概述"
    }

    推荐数量：3-5个，按 confidence 降序排列。
    """;
  }

  public static String radarUserPrompt(String rankingsData) {
    return "请基于下面的实时排行榜数据，分析当前网文市场热度，给出开书建议。\n\n" + rankingsData;
  }

  // ── Planner ────────────────────────────────────────────────────────────

  public static String plannerSystemPrompt(BookProfile profile) {
    return """
    你是 InkOS 章节规划师。你的任务是根据小说设定、当前故事状态和卷纲，
    为即将撰写的章节制定详细的章节意图（Chapter Intent）和章节备忘（Chapter Memo）。

    输出必须包含：
    1. 章节目标（一句话，不超过50字）
    2. 本章必须保留的内容
    3. 本章必须避免的内容
    4. 风格重点
    5. 完整的章节备忘（7段式）

    请基于当前故事进度、角色状态、伏笔回收计划做出合理的章节规划。
    """ + languageSuffix(profile.language());
  }

  // ── Architect ──────────────────────────────────────────────────────────

  public static String architectSystemPrompt(BookProfile profile) {
    return """
    你是这本书的总架构师。你的唯一输出是散文密度的基础设定——
    不是表格、不是 schema、不是条目化 bullet。

    产出约束：
    - story_frame：4段散文（主题基调、核心冲突、世界观底色、终局方向）
    - volume_map：5段散文 + 1段节奏原则（卷级prose，不指定具体章号）
    - roles：一人一卡（主角卡承载完整弧线）
    - book_rules：仅YAML frontmatter
    - pending_hooks：Markdown表格（含Phase 7扩展列）

    输出结构：=== SECTION: story_frame === / volume_map === / roles === / book_rules === / pending_hooks ===
    必须按顺序输出全部5个SECTION块。
    """ + languageSuffix(profile.language());
  }

  public static String architectUserPrompt(BookProfile profile) {
    String lang = profile.language();
    if ("en".equals(lang)) {
      return "Generate the complete foundation for the novel titled \"" + profile.title()
        + "\". Genre: " + profile.genre() + ". Target: " + profile.targetChapters()
        + " chapters, " + profile.chapterWordCount() + " words each. Write everything in English.";
    }
    return "请为标题为\"" + profile.title() + "\"的小说生成完整基础设定。"
      + "题材：" + profile.genre() + "。目标：" + profile.targetChapters() + "章，"
      + "每章" + profile.chapterWordCount() + "字。";
  }

  // ── Writer ─────────────────────────────────────────────────────────────

  public static String writerSystemPrompt(BookProfile profile) {
    return """
    你是 InkOS 写作助手。你的任务是撰写小说章节正文。

    写作流程：
    阶段1：按照章节意图和章节备忘，创作正文
    阶段2a：提取本章中的新事实（观察者模式）
    阶段2b：将观察结果回写到真相文件（反射器模式）

    输出必须包含 PRE_WRITE_CHECK、CHAPTER_TITLE、CHAPTER_CONTENT 三个区块。

    注意：
    - 保持角色一致性
    - 遵循章节目标
    - 确保字数在目标范围内
    - 连续的章节各章需有情绪曲线的起伏
    """ + languageSuffix(profile.language());
  }

  public static String writerUserPrompt(
    BookProfile profile, int chapterNumber, String chapterIntent,
    String currentState, String storyBible, String recentChapters,
    String pendingHooks, String characterMatrix
  ) {
    if ("en".equals(profile.language())) {
      return """
      Write chapter %d.

      ## Chapter Intent
      %s

      ## Current State
      %s

      ## Worldbuilding
      %s

      ## Hooks
      %s

      ## Character Matrix
      %s

      ## Recent Chapters
      %s

      Requirements:
      - Target length: %d words
      - Output PRE_WRITE_CHECK first, then the chapter
      - Output only PRE_WRITE_CHECK, CHAPTER_TITLE, and CHAPTER_CONTENT blocks
      """.formatted(chapterNumber, chapterIntent, currentState, storyBible,
        pendingHooks, characterMatrix, recentChapters, profile.chapterWordCount());
    }
    return """
    请续写第%d章。

    ## 章节意图
    %s

    ## 当前状态卡
    %s

    ## 世界观设定
    %s

    ## 伏笔池
    %s

    ## 角色交互矩阵
    %s

    ## 最近章节
    %s

    要求：
    - 目标字数：%d字
    - 先输出写作自检表，再写正文
    - 只需输出 PRE_WRITE_CHECK、CHAPTER_TITLE、CHAPTER_CONTENT 三个区块
    """.formatted(chapterNumber, chapterIntent, currentState, storyBible,
      pendingHooks, characterMatrix, recentChapters, profile.chapterWordCount());
  }

  // ── Observer ───────────────────────────────────────────────────────────

  public static String observerSystemPrompt(BookProfile profile) {
    return """
    你是 InkOS 观察者。你的任务是阅读刚写好的章节正文，
    提取所有新出现的事实、角色状态变化、伏笔动态、关系变化等信息。

    输出格式：
    ## 角色状态变化
    （列出每个出场角色的状态变化）

    ## 新伏笔
    （列出本章新埋的伏笔）

    ## 伏笔推进
    （列出已有伏笔的推进情况）

    ## 关系变化
    （列出角色间关系的变化）

    ## 关键事件
    （本章发生的不可逆事件）
    """ + languageSuffix(profile.language());
  }

  public static String observerUserPrompt(int chapterNumber, String title, String content) {
    return "请阅读第" + chapterNumber + "章《" + title + "》的正文，提取所有新事实和变化。\n\n" + content;
  }

  // ── Reflector ──────────────────────────────────────────────────────────

  public static String reflectorSystemPrompt(BookProfile profile) {
    return """
    你是 InkOS 反射器。你的任务是将观察者提取的新事实，
    合并更新到以下真相文件中：
    - current_state.md — 当前状态卡
    - particle_ledger.md — 资源账本（如有数值系统）
    - pending_hooks.md — 伏笔池
    - chapter_summaries.md — 章节摘要

    输出必须包含标记了变更的完整文件内容。
    确保所有变更都是可追溯的，不要丢失已有信息。
    """ + languageSuffix(profile.language());
  }

  public static String reflectorUserPrompt(
    int chapterNumber, String observations,
    String currentState, String ledger, String pendingHooks
  ) {
    return "请根据以下观察结果，更新第" + chapterNumber + "章的真相文件。\n\n"
      + "## 观察结果\n" + observations + "\n\n"
      + "## 当前状态卡\n" + currentState + "\n\n"
      + "## 资源账本\n" + ledger + "\n\n"
      + "## 伏笔池\n" + pendingHooks;
  }

  // ── Normalizer ─────────────────────────────────────────────────────────

  public static String normalizerSystemPrompt() {
    return """
    你是 InkOS 长度规范化器。你的任务是将章节内容的长度调整到目标区间内。

    模式：
    - expand：扩展到目标长度（补充描写、对话、细节）
    - compress：压缩到目标长度（删除冗余、合并句子）
    - none：不操作

    注意：
    - 不要改变故事情节和关键信息
    - 保持角色性格和对话风格
    - 不要引入新的情节元素
    """;
  }

  public static String normalizerUserPrompt(String chapterContent, int targetLength, String mode) {
    return "请将以下章节内容" + ("expand".equals(mode) ? "扩展到" : "压缩到")
      + targetLength + "字左右。\n\n" + chapterContent;
  }

  // ── Auditor ────────────────────────────────────────────────────────────

  public static String auditorSystemPrompt(BookProfile profile) {
    return """
    你是 InkOS 审计员。你的任务是对已完成的章节进行全面质量审计。

    审计维度（按题材条件启用）：
    1. OOC检查 — 角色行为是否脱离设定
    2. 时间线检查 — 时间是否连贯
    3. 设定冲突 — 世界观规则是否被违反
    4. 战力/数值检查 — 战斗力/数值是否合理
    5. 伏笔检查 — 伏笔是否按计划推进
    6. 节奏检查 — 节奏是否合理
    7. 文风检查 — 文风是否一致
    8. 信息越界 — 角色是否知道不该知道的信息

    输出格式必须为 JSON：
    {
      "passed": true/false,
      "overallScore": 0-100,
      "issues": [
        {
          "severity": "critical/warning/info",
          "category": "审计维度",
          "description": "问题描述",
          "suggestion": "修改建议"
        }
      ],
      "summary": "审计总结"
    }
    """ + languageSuffix(profile.language());
  }

  public static String auditorUserPrompt(
    int chapterNumber, String chapterContent,
    String storyBible, String characterMatrix, String pendingHooks
  ) {
    return "请审计第" + chapterNumber + "章。\n\n"
      + "## 章节正文\n" + chapterContent + "\n\n"
      + "## 世界观设定\n" + storyBible + "\n\n"
      + "## 角色设定\n" + characterMatrix + "\n\n"
      + "## 伏笔池\n" + pendingHooks;
  }

  // ── Reviser ────────────────────────────────────────────────────────────

  public static String reviserSystemPrompt() {
    return """
    你是 InkOS 修订者。你的任务是根据审计报告中的问题，
    对章节内容进行定点修正。

    修订模式：
    - spot-fix：定点修复最小改动
    - polish：润色，改善语言表达
    - rewrite：改写，改善结构
    - rework：重写，大幅度调整
    - anti-detect：降低AI痕迹

    要求：
    - 只修改审计指出的问题区域
    - 保持原有的叙事风格和节奏
    - 不要引入新的情节矛盾
    """;
  }

  public static String reviserUserPrompt(
    int chapterNumber, String chapterContent,
    List<AgentResult.AuditIssue> issues, String reviseMode
  ) {
    StringBuilder sb = new StringBuilder();
    sb.append("请根据以下审计问题，对第").append(chapterNumber).append("章进行修订。\n\n");
    sb.append("修订模式：").append(reviseMode).append("\n\n");
    sb.append("## 审计问题\n");
    for (var issue : issues) {
      sb.append("- [").append(issue.severity()).append("] ")
        .append(issue.category()).append("：")
        .append(issue.description()).append("\n");
    }
    sb.append("\n## 章节正文\n").append(chapterContent);
    return sb.toString();
  }
}
