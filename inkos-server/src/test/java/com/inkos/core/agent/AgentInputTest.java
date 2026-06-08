package com.inkos.core.agent;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AgentInputTest {

  @Test
  void builder_shouldBuildWithAllFields() {
    Map<String, Object> extra = Map.<String, Object>of("key", "value");
    var input = AgentInput.builder()
      .prompt("write chapter 5")
      .systemPrompt("you are a writer")
      .model("gpt-4")
      .temperature(0.5)
      .maxTokens(4096)
      .extra(extra)
      .build();

    assertAll(
      () -> assertEquals("write chapter 5", input.prompt()),
      () -> assertEquals("you are a writer", input.systemPrompt()),
      () -> assertEquals("gpt-4", input.model()),
      () -> assertEquals(0.5, input.temperature()),
      () -> assertEquals(4096, input.maxTokens()),
      () -> assertEquals(extra, input.extra()),
      () -> assertNull(input.streamCallback())
    );
  }

  @Test
  void builder_shouldDefaultOptionalFields() {
    var input = AgentInput.builder()
      .prompt("prompt")
      .systemPrompt("system")
      .build();

    assertAll(
      () -> assertEquals("prompt", input.prompt()),
      () -> assertEquals("system", input.systemPrompt()),
      () -> assertNull(input.model()),
      () -> assertNull(input.temperature()),
      () -> assertNull(input.maxTokens()),
      () -> assertTrue(input.extra().isEmpty()),
      () -> assertNull(input.streamCallback())
    );
  }

  @Test
  void of_shouldCreateBasicInput() {
    var input = AgentInput.of("user prompt", "system prompt");

    assertAll(
      () -> assertEquals("user prompt", input.prompt()),
      () -> assertEquals("system prompt", input.systemPrompt()),
      () -> assertNull(input.model()),
      () -> assertNull(input.temperature()),
      () -> assertNull(input.maxTokens()),
      () -> assertTrue(input.extra().isEmpty()),
      () -> assertNull(input.streamCallback())
    );
  }

  @Test
  void extra_shouldHoldAgentSpecificData() {
    var extraMap = new HashMap<String, Object>();
    extraMap.put("chapterNumber", 5);
    extraMap.put("profile", "fantasy");
    extraMap.put("targetLength", 3000);

    var input = AgentInput.builder()
      .prompt("write")
      .systemPrompt("system")
      .extra(extraMap)
      .build();

    assertAll(
      () -> assertEquals(5, input.extra().get("chapterNumber")),
      () -> assertEquals("fantasy", input.extra().get("profile")),
      () -> assertEquals(3000, input.extra().get("targetLength"))
    );
  }

  @Test
  void builder_shouldBeChainable() {
    var input = AgentInput.builder()
      .prompt("a").systemPrompt("b").model("c")
      .temperature(0.1).maxTokens(100)
      .build();

    assertAll(
      () -> assertEquals("a", input.prompt()),
      () -> assertEquals("b", input.systemPrompt()),
      () -> assertEquals("c", input.model()),
      () -> assertEquals(0.1, input.temperature()),
      () -> assertEquals(100, input.maxTokens())
    );
  }

  @Test
  void builder_shouldAllowNullPrompt() {
    var input = AgentInput.builder()
      .prompt(null)
      .systemPrompt("system")
      .build();

    assertNull(input.prompt());
  }
}
