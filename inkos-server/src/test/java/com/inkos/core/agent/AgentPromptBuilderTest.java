package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AgentPromptBuilderTest {

  static BookProfile zhProfile() {
    return new BookProfile("b1", 1L, "凡人修仙传", "qidian", "仙侠", "active",
      500, 3000, "zh", "none", 10, "主角从凡人开始修炼", "cover.jpg",
      LocalDateTime.now(), LocalDateTime.now(), null);
  }

  static BookProfile enProfile() {
    return new BookProfile("b2", 2L, "The Lost Kingdom", "tomato", "fantasy", "active",
      200, 2500, "en", "none", 5, "A kingdom lost to time", "cover.jpg",
      LocalDateTime.now(), LocalDateTime.now(), null);
  }

  @Nested
  class SystemPromptTests {

    @Test
    void radarSystemPrompt_shouldContainMarketAnalysis() {
      String p = AgentPromptBuilder.radarSystemPrompt();
      assertAll(
        () -> assertNotNull(p),
        () -> assertFalse(p.isBlank()),
        () -> assertTrue(p.contains("recommendations")),
        () -> assertTrue(p.contains("JSON"))
      );
    }

    @Test
    void plannerSystemPrompt_shouldContainChapterPlanning() {
      String p = AgentPromptBuilder.plannerSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("章节规划师")),
        () -> assertTrue(p.contains("章节意图")),
        () -> assertTrue(p.contains("章节备忘"))
      );
    }

    @Test
    void architectSystemPrompt_shouldContainSectionBlocks() {
      String p = AgentPromptBuilder.architectSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("总架构师")),
        () -> assertTrue(p.contains("SECTION:")),
        () -> assertTrue(p.contains("story_frame"))
      );
    }

    @Test
    void writerSystemPrompt_shouldContainWritingBlocks() {
      String p = AgentPromptBuilder.writerSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("写作助手")),
        () -> assertTrue(p.contains("PRE_WRITE_CHECK")),
        () -> assertTrue(p.contains("CHAPTER_TITLE"))
      );
    }

    @Test
    void observerSystemPrompt_shouldContainExtractInstructions() {
      String p = AgentPromptBuilder.observerSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("观察者")),
        () -> assertTrue(p.contains("角色状态变化")),
        () -> assertTrue(p.contains("新伏笔"))
      );
    }

    @Test
    void reflectorSystemPrompt_shouldContainTruthFiles() {
      String p = AgentPromptBuilder.reflectorSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("反射器")),
        () -> assertTrue(p.contains("真相文件")),
        () -> assertTrue(p.contains("current_state.md"))
      );
    }

    @Test
    void normalizerSystemPrompt_shouldContainLengthModes() {
      String p = AgentPromptBuilder.normalizerSystemPrompt();
      assertAll(
        () -> assertNotNull(p),
        () -> assertFalse(p.isBlank()),
        () -> assertTrue(p.contains("expand")),
        () -> assertTrue(p.contains("compress"))
      );
    }

    @Test
    void auditorSystemPrompt_shouldContainAuditDimensions() {
      String p = AgentPromptBuilder.auditorSystemPrompt(zhProfile());
      assertAll(
        () -> assertTrue(p.contains("审计员")),
        () -> assertTrue(p.contains("OOC检查")),
        () -> assertTrue(p.contains("passed"))
      );
    }

    @Test
    void reviserSystemPrompt_shouldContainReviseModes() {
      String p = AgentPromptBuilder.reviserSystemPrompt();
      assertAll(
        () -> assertNotNull(p),
        () -> assertFalse(p.isBlank()),
        () -> assertTrue(p.contains("spot-fix")),
        () -> assertTrue(p.contains("anti-detect"))
      );
    }
  }

  @Nested
  class UserPromptTests {

    @Test
    void radarUserPrompt_shouldIncludeRankingsData() {
      String p = AgentPromptBuilder.radarUserPrompt("test rankings data");
      assertAll(
        () -> assertTrue(p.contains("test rankings data")),
        () -> assertTrue(p.contains("实时排行榜数据"))
      );
    }

    @Test
    void architectUserPrompt_zh_shouldIncludeProfileDetails() {
      var profile = zhProfile();
      String p = AgentPromptBuilder.architectUserPrompt(profile);
      assertAll(
        () -> assertTrue(p.contains(profile.title())),
        () -> assertTrue(p.contains(profile.genre())),
        () -> assertTrue(p.contains(String.valueOf(profile.targetChapters()))),
        () -> assertTrue(p.contains("小说"))
      );
    }

    @Test
    void architectUserPrompt_en_shouldIncludeEnglishDetails() {
      var profile = enProfile();
      String p = AgentPromptBuilder.architectUserPrompt(profile);
      assertAll(
        () -> assertTrue(p.contains(profile.title())),
        () -> assertTrue(p.contains("Generate the complete foundation")),
        () -> assertTrue(p.contains(String.valueOf(profile.chapterWordCount())))
      );
    }

    @Test
    void writerUserPrompt_zh_shouldIncludeChapterContext() {
      var profile = zhProfile();
      String p = AgentPromptBuilder.writerUserPrompt(
        profile, 5, "主角突破筑基期",
        "当前状态", "世界观设定", "近期章节", "伏笔池", "角色矩阵"
      );
      assertAll(
        () -> assertTrue(p.contains("第5章")),
        () -> assertTrue(p.contains("主角突破筑基期")),
        () -> assertTrue(p.contains("当前状态")),
        () -> assertTrue(p.contains("3000字"))
      );
    }

    @Test
    void writerUserPrompt_en_shouldIncludeEnglishContext() {
      var profile = enProfile();
      String p = AgentPromptBuilder.writerUserPrompt(
        profile, 3, "The hero discovers the secret",
        "current state", "story bible", "recent chapters",
        "pending hooks", "character matrix"
      );
      assertAll(
        () -> assertTrue(p.contains("Write chapter 3")),
        () -> assertTrue(p.contains("The hero discovers the secret")),
        () -> assertTrue(p.contains("2500 words"))
      );
    }

    @Test
    void observerUserPrompt_shouldIncludeChapterInfo() {
      String p = AgentPromptBuilder.observerUserPrompt(7, "测试章", "chapter body");
      assertAll(
        () -> assertTrue(p.contains("第7章")),
        () -> assertTrue(p.contains("测试章")),
        () -> assertTrue(p.contains("chapter body"))
      );
    }

    @Test
    void normalizerUserPrompt_shouldIncludeTargetLength() {
      String p = AgentPromptBuilder.normalizerUserPrompt("content", 5000, "expand");
      assertAll(
        () -> assertTrue(p.contains("5000")),
        () -> assertTrue(p.contains("content")),
        () -> assertTrue(p.contains("扩展"))
      );
    }

    @Test
    void auditorUserPrompt_shouldIncludeContentAndContext() {
      String p = AgentPromptBuilder.auditorUserPrompt(
        10, "chapter text", "world bible", "characters", "hooks"
      );
      assertAll(
        () -> assertTrue(p.contains("第10章")),
        () -> assertTrue(p.contains("chapter text")),
        () -> assertTrue(p.contains("world bible")),
        () -> assertTrue(p.contains("characters")),
        () -> assertTrue(p.contains("hooks"))
      );
    }

    @Test
    void reviserUserPrompt_shouldIncludeIssuesAndMode() {
      var issues = List.of(
        new AgentResult.AuditIssue("critical", "ooc", "Character OOC", "Fix it"),
        new AgentResult.AuditIssue("warning", "timeline", "Time inconsistency", "Fix date")
      );
      String p = AgentPromptBuilder.reviserUserPrompt(12, "chapter content", issues, "spot-fix");
      assertAll(
        () -> assertTrue(p.contains("第12章")),
        () -> assertTrue(p.contains("spot-fix")),
        () -> assertTrue(p.contains("Character OOC")),
        () -> assertTrue(p.contains("Time inconsistency")),
        () -> assertTrue(p.contains("chapter content"))
      );
    }

    @Test
    void reflectorUserPrompt_shouldIncludeAllState() {
      String p = AgentPromptBuilder.reflectorUserPrompt(8, "observations", "state", "ledger", "hooks");
      assertAll(
        () -> assertTrue(p.contains("第8章")),
        () -> assertTrue(p.contains("observations")),
        () -> assertTrue(p.contains("state")),
        () -> assertTrue(p.contains("ledger")),
        () -> assertTrue(p.contains("hooks"))
      );
    }
  }

  @Nested
  class LanguageSuffixTests {

    @Test
    void plannerPrompt_zh_shouldNotHaveLanguageOverride() {
      assertFalse(AgentPromptBuilder.plannerSystemPrompt(zhProfile()).contains("LANGUAGE OVERRIDE"));
    }

    @Test
    void plannerPrompt_en_shouldHaveLanguageOverride() {
      assertTrue(AgentPromptBuilder.plannerSystemPrompt(enProfile()).contains("LANGUAGE OVERRIDE"));
    }

    @Test
    void architectPrompt_zh_shouldNotHaveLanguageOverride() {
      assertFalse(AgentPromptBuilder.architectSystemPrompt(zhProfile()).contains("LANGUAGE OVERRIDE"));
    }

    @Test
    void architectPrompt_en_shouldHaveLanguageOverride() {
      assertTrue(AgentPromptBuilder.architectSystemPrompt(enProfile()).contains("LANGUAGE OVERRIDE"));
    }

    @Test
    void writerPrompt_zh_shouldNotHaveLanguageOverride() {
      assertFalse(AgentPromptBuilder.writerSystemPrompt(zhProfile()).contains("LANGUAGE OVERRIDE"));
    }

    @Test
    void writerPrompt_en_shouldHaveLanguageOverride() {
      assertTrue(AgentPromptBuilder.writerSystemPrompt(enProfile()).contains("LANGUAGE OVERRIDE"));
    }
  }

  @Nested
  class CrossAgentPipelineTests {

    @Test
    void plannerWriterPrompts_shouldFormCoherentChain() {
      var profile = zhProfile();
      String chapterIntent = "主角突破筑基期，获得新能力";

      String planPrompt = AgentPromptBuilder.plannerSystemPrompt(profile);
      String writePrompt = AgentPromptBuilder.writerUserPrompt(
        profile, 5, chapterIntent, "state", "bible", "recent", "hooks", "chars"
      );

      assertTrue(planPrompt.contains("章节意图"));
      assertTrue(writePrompt.contains(chapterIntent));
    }

    @Test
    void writerObserverPrompts_shouldFormCoherentChain() {
      String content = "这是第五章的正文内容...";
      String observerPrompt = AgentPromptBuilder.observerUserPrompt(5, "第五章", content);
      assertTrue(observerPrompt.contains(content));
    }

    @Test
    void auditorReviserPrompts_shouldFormCoherentChain() {
      var issues = List.of(
        new AgentResult.AuditIssue("critical", "ooc", "角色行为不一致", "修改对话")
      );
      String revisePrompt = AgentPromptBuilder.reviserUserPrompt(10, "chapter", issues, "spot-fix");
      assertAll(
        () -> assertTrue(revisePrompt.contains("角色行为不一致")),
        () -> assertTrue(revisePrompt.contains("spot-fix"))
      );
    }

    @Test
    void architectWriterPrompts_shouldShareProfileContext() {
      var profile = zhProfile();
      String archPrompt = AgentPromptBuilder.architectSystemPrompt(profile);
      String writePrompt = AgentPromptBuilder.writerSystemPrompt(profile);

      assertTrue(archPrompt.contains("总架构师"));
      assertTrue(writePrompt.contains("写作助手"));
    }
  }

  @Nested
  class GenreAndLanguageTests {

    @ParameterizedTest
    @CsvSource({
      "仙侠, qidian, zh",
      "fantasy, tomato, en",
      "科幻, qidian, zh",
      "romance, feilu, zh",
      "historical, qidian, zh"
    })
    void architectUserPrompt_shouldIncludeGenre(String genre, String platform, String lang) {
      var profile = new BookProfile("b", 1L, "测试", platform, genre, "active",
        100, 3000, lang, "none", 0, "", "cover.jpg",
        LocalDateTime.now(), LocalDateTime.now(), null);
      assertTrue(AgentPromptBuilder.architectUserPrompt(profile).contains(genre));
    }

    @ParameterizedTest
    @ValueSource(strings = {"zh", "en"})
    void allSystemPrompts_shouldReturnNonNullForBothLanguages(String lang) {
      var profile = lang.equals("zh") ? zhProfile() : enProfile();
      assertAll(
        () -> assertNotNull(AgentPromptBuilder.plannerSystemPrompt(profile)),
        () -> assertNotNull(AgentPromptBuilder.architectSystemPrompt(profile)),
        () -> assertNotNull(AgentPromptBuilder.writerSystemPrompt(profile)),
        () -> assertNotNull(AgentPromptBuilder.observerSystemPrompt(profile)),
        () -> assertNotNull(AgentPromptBuilder.reflectorSystemPrompt(profile)),
        () -> assertNotNull(AgentPromptBuilder.auditorSystemPrompt(profile))
      );
    }
  }
}
