package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlannerAgent extends BaseAgent {

  @Override
  public String getName() {
    return "planner";
  }

  @Override
  public String getRole() {
    return "章节规划师 — 分析故事状态，生成章节意图和章节备忘";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String storyState = extra.getOrDefault("storyState", "").toString();
    String currentFocus = extra.getOrDefault("currentFocus", "").toString();
    String authorIntent = extra.getOrDefault("authorIntent", "").toString();
    String volumeOutline = extra.getOrDefault("volumeOutline", "").toString();
    String characterContext = extra.getOrDefault("characterContext", "").toString();

    String systemPrompt = AgentPromptBuilder.plannerSystemPrompt(profile);
    String userPrompt = buildUserPrompt(
      profile, chapterNumber, storyState, currentFocus,
      authorIntent, volumeOutline, characterContext
    );

    return executeLlm(ctx, systemPrompt, userPrompt, 0.7, 8192)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "chapterNumber", chapterNumber)
          );
        }
        return result;
      });
  }

  private String buildUserPrompt(
    BookProfile profile, int chapterNumber, String storyState,
    String currentFocus, String authorIntent, String volumeOutline,
    String characterContext
  ) {
    String lang = profile.language();
    if ("en".equals(lang)) {
      return """
        Plan chapter %d for "%s".

        ## Current Story State
        %s

        ## Current Focus
        %s

        ## Author Intent
        %s

        ## Volume Outline
        %s

        ## Character Context
        %s

        Provide:
        1. Chapter goal (one sentence, max 50 chars)
        2. Must-keep elements
        3. Must-avoid pitfalls
        4. Style emphasis
        5. Full chapter memo (7 sections with thread refs)
        """.formatted(chapterNumber, profile.title(), storyState,
        currentFocus, authorIntent, volumeOutline, characterContext);
    }
    return """
      请为《%s》规划第%d章。

      ## 当前故事状态
      %s

      ## 当前聚焦
      %s

      ## 作者意图
      %s

      ## 卷纲
      %s

      ## 角色上下文
      %s

      输出内容：
      1. 章节目标（一句话，不超过50字）
      2. 必须保留的内容
      3. 必须避免的陷阱
      4. 风格重点
      5. 完整章节备忘（7段式，含thread refs）
      """.formatted(profile.title(), chapterNumber, storyState,
      currentFocus, authorIntent, volumeOutline, characterContext);
  }
}
