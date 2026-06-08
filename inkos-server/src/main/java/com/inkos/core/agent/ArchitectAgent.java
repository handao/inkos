package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ArchitectAgent extends BaseAgent {

  @Override
  public String getName() {
    return "architect";
  }

  @Override
  public String getRole() {
    return "小说架构师 — 生成完整的基础设定（story_frame/volume_map/roles/book_rules/pending_hooks）";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    String externalContext = (String) extra.getOrDefault("externalContext", "");
    String reviewFeedback = (String) extra.getOrDefault("reviewFeedback", "");
    String genreBody = (String) extra.getOrDefault("genreBody", "");

    String systemPrompt = AgentPromptBuilder.architectSystemPrompt(profile);
    String userPrompt = AgentPromptBuilder.architectUserPrompt(profile);
    if (!externalContext.isBlank()) {
      userPrompt += "\n\n## 外部指令\n" + externalContext;
    }
    if (!reviewFeedback.isBlank()) {
      userPrompt += "\n\n## 评审反馈（请据此修改）\n" + reviewFeedback;
    }
    if (!genreBody.isBlank()) {
      userPrompt += "\n\n## 题材底色\n" + genreBody;
    }

    return executeLlm(ctx, systemPrompt, userPrompt, 0.8, 16384)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "profile", profile.title())
          );
        }
        return result;
      });
  }
}
