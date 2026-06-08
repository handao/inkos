package com.inkos.core.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

  @Test
  void bookProfile_shouldCreateRecord() {
    var now = LocalDateTime.now();
    var profile = new BookProfile("b1", 1L, "Test Book", "tomato", "fantasy",
      BookProfile.STATUS_ACTIVE, 50, 3000, "zh", "none",
      5, "outline", "cover.jpg", now, now, null);

    assertEquals("b1", profile.id());
    assertEquals(BookProfile.STATUS_ACTIVE, profile.status());
    assertEquals(BookProfile.PLATFORM_TOMATO, profile.platform());
  }

  @Test
  void bookProfile_shouldNormalizePlatform() {
    assertEquals(BookProfile.PLATFORM_TOMATO, BookProfile.normalizePlatform("番茄小说"));
    assertEquals(BookProfile.PLATFORM_TOMATO, BookProfile.normalizePlatform("fanqie"));
    assertEquals(BookProfile.PLATFORM_TOMATO, BookProfile.normalizePlatform("tomato-novel"));
    assertEquals(BookProfile.PLATFORM_QIDIAN, BookProfile.normalizePlatform("起点"));
    assertEquals(BookProfile.PLATFORM_QIDIAN, BookProfile.normalizePlatform("qidian"));
    assertEquals(BookProfile.PLATFORM_FEILU, BookProfile.normalizePlatform("飞卢"));
    assertEquals(BookProfile.PLATFORM_FEILU, BookProfile.normalizePlatform("feilu"));
    assertEquals(BookProfile.PLATFORM_OTHER, BookProfile.normalizePlatform("some-random"));
    assertEquals(BookProfile.PLATFORM_OTHER, BookProfile.normalizePlatform(null));
  }

  @Test
  void chapterContent_shouldCreateRecord() {
    var telemetry = new ChapterContent.LengthTelemetry(3000, 2500, 3500, 2000, 4000, "zh_chars",
      2500, 2500, 2500, 2500, false, false);
    var usage = new ChapterContent.TokenUsage(1000, 2000, 3000);
    var content = new ChapterContent(1, "Chapter 1", ChapterContent.STATUS_DRAFTED, 2500,
      LocalDateTime.now(), LocalDateTime.now(), List.of(), List.of(), null, null, null, null,
      telemetry, usage);

    assertEquals(1, content.number());
    assertEquals(ChapterContent.STATUS_DRAFTED, content.status());
  }

  @Test
  void chapterContent_shouldSumTokenUsage() {
    var a = new ChapterContent.TokenUsage(100, 200, 300);
    var b = new ChapterContent.TokenUsage(300, 400, 700);
    var sum = ChapterContent.TokenUsage.sum(a, b);
    assertEquals(400, sum.promptTokens());
    assertEquals(600, sum.completionTokens());
    assertEquals(1000, sum.totalTokens());
  }

  @Test
  void genreProfile_shouldCreateDefault() {
    var default_ = GenreProfile.defaultProfile();
    assertEquals("unknown", default_.name());
    assertEquals(GenreProfile.LANGUAGE_ZH, default_.language());
    assertTrue(default_.chapterTypes().isEmpty());
  }

  @Test
  void styleProfile_shouldCreateRecord() {
    var range = new StyleProfile.ParagraphLengthRange(1, 10);
    var style = new StyleProfile(25.0, 5.0, 3.0, range, 0.8, List.of("pattern1"),
      List.of("rhetoric1"), "test", "2025-01-01");

    assertEquals(25.0, style.avgSentenceLength());
    assertEquals(5.0, style.sentenceLengthStdDev());
    assertEquals(1, style.paragraphLengthRange().min());
  }

  @Test
  void pipelineState_shouldCreateNestedRecords() {
    var protagonist = new PipelineState.Protagonist("active", "find the artifact", "no killing");
    var enemy = new PipelineState.Enemy("Dark Lord", "archnemesis", "world domination");
    var state = new PipelineState(5, "dungeon", protagonist, List.of(enemy),
      List.of("the ring is a trap"), "escape from dungeon", "anchor-001");

    assertEquals(5, state.chapter());
    assertEquals("dungeon", state.location());
    assertEquals("archnemesis", state.enemies().getFirst().relationship());
  }

  @Test
  void runtimeState_shouldCreateNestedRecords() {
    var fact = new RuntimeState.CurrentStateFact("hero", "location", "forest", 1, 5, 1);
    var currentState = new RuntimeState.CurrentStateState(5, List.of(fact));
    var summary = new RuntimeState.ChapterSummariesState(List.of());
    var hooks = new RuntimeState.HooksState(List.of());
    var manifest = new RuntimeState.StateManifest(2, "zh", 5, 1, List.of());
    var runtime = new RuntimeState(manifest, hooks, summary, currentState);

    assertEquals(2, runtime.manifest().schemaVersion());
    assertEquals(1, runtime.currentState().facts().size());
    assertEquals("hero", runtime.currentState().facts().getFirst().subject());
  }

  @Test
  void runtimeState_shouldCreateHookRecord() {
    var hook = new RuntimeState.HookRecord("h1", 1, "foreshadowing", RuntimeState.HookRecord.STATUS_OPEN,
      1, "reveal betrayal", "near-term", "notes", List.of(), "arc1",
      true, 5, 0, false);

    assertEquals(RuntimeState.HookRecord.STATUS_OPEN, hook.status());
    assertEquals("near-term", hook.payoffTiming());
    assertTrue(hook.coreHook());
  }

  @Test
  void agentMessage_shouldCreateSealedTypes() {
    AgentMessage sys = new AgentMessage.SystemMessage("be helpful");
    AgentMessage user = new AgentMessage.UserMessage("write chapter");
    AgentMessage assistant = new AgentMessage.AssistantMessage("ok", List.of());
    AgentMessage tool = new AgentMessage.ToolResultMessage("call-1", "done");

    assertInstanceOf(AgentMessage.SystemMessage.class, sys);
    assertEquals("be helpful", ((AgentMessage.SystemMessage) sys).content());
    assertInstanceOf(AgentMessage.AssistantMessage.class, assistant);
    assertInstanceOf(AgentMessage.ToolResultMessage.class, tool);
  }

  @Test
  void agentMessage_shouldCreateAssistantMessageWithoutToolCalls() {
    var msg = new AgentMessage.AssistantMessage("hello");
    assertTrue(msg.toolCalls().isEmpty());
  }

  @Test
  void lengthSpec_shouldCheckOutsideHardRange() {
    var spec = new LengthSpec(3000, 2500, 3500, 2000, 4000, "zh_chars", "none");

    assertFalse(spec.isOutsideHardRange(2000));
    assertFalse(spec.isOutsideHardRange(4000));
    assertFalse(spec.isOutsideHardRange(3000));
    assertTrue(spec.isOutsideHardRange(1999));
    assertTrue(spec.isOutsideHardRange(4001));
  }

  @Test
  void lengthSpec_shouldHaveConstants() {
    assertEquals("zh_chars", LengthSpec.COUNT_MODE_ZH);
    assertEquals("en_words", LengthSpec.COUNT_MODE_EN);
    assertEquals("expand", LengthSpec.NORMALIZE_EXPAND);
    assertEquals("compress", LengthSpec.NORMALIZE_COMPRESS);
    assertEquals("none", LengthSpec.NORMALIZE_NONE);
  }
}
