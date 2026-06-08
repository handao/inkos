package com.inkos.core.llm.endpoint;

import com.inkos.core.llm.EndpointConfig;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AllEndpointsTest {

  @Test
  void getAll_shouldReturnAll43Providers() {
    var all = AllEndpoints.getAll();
    assertEquals(43, all.size(), "Should have exactly 43 endpoint providers");
  }

  @Test
  void getAll_shouldHaveNoDuplicateIds() {
    var all = AllEndpoints.getAll();
    var ids = new HashSet<String>();
    for (var ep : all) {
      assertTrue(ids.add(ep.id()), "Duplicate endpoint id: " + ep.id());
    }
  }

  @Test
  void getAll_shouldHaveRequiredFields() {
    var all = AllEndpoints.getAll();
    for (var ep : all) {
      assertNotNull(ep.id(), "Endpoint id must not be null");
      assertNotNull(ep.label(), "Endpoint label must not be null: " + ep.id());
      assertNotNull(ep.baseUrl(), "Endpoint baseUrl must not be null: " + ep.id());
    }
  }

  @Test
  void getAll_shouldReturnUnmodifiableList() {
    var all = AllEndpoints.getAll();
    assertThrows(UnsupportedOperationException.class, () -> all.add(null));
  }

  @Test
  void allModels_shouldHaveRequiredFields() {
    var all = AllEndpoints.getAll();
    for (var ep : all) {
      for (var model : ep.models()) {
        assertNotNull(model.id(), "Model id must not be null in " + ep.id());
        assertTrue(model.contextWindowTokens() > 0,
          "Model " + model.id() + " in " + ep.id() + " should have positive context window");
      }
    }
  }

  @Test
  void openai_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("openai");
    assertEquals(53, ep.models().size());
  }

  @Test
  void anthropic_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("anthropic");
    assertEquals(9, ep.models().size());
  }

  @Test
  void deepseek_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("deepseek");
    assertEquals(4, ep.models().size());
  }

  @Test
  void google_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("google");
    assertEquals(26, ep.models().size());
  }

  @Test
  void moonshot_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("moonshot");
    assertEquals(14, ep.models().size());
  }

  @Test
  void ollama_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("ollama");
    assertEquals(52, ep.models().size());
  }

  @Test
  void openrouter_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("openrouter");
    assertEquals(60, ep.models().size());
  }

  @Test
  void siliconcloud_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("siliconcloud");
    assertEquals(98, ep.models().size());
  }

  @Test
  void zhipu_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("zhipu");
    assertEquals(37, ep.models().size());
  }

  @Test
  void wenxin_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("wenxin");
    assertEquals(84, ep.models().size());
  }

  @Test
  void kkaiapi_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("kkaiapi");
    assertEquals(28, ep.models().size());
  }

  @Test
  void githubcopilot_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("githubCopilot");
    assertEquals(21, ep.models().size());
  }

  @Test
  void bailian_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("bailian");
    assertEquals(24, ep.models().size());
  }

  @Test
  void volcengine_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("volcengine");
    assertEquals(30, ep.models().size());
  }

  @Test
  void hunyuan_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("hunyuan");
    assertEquals(20, ep.models().size());
  }

  @Test
  void minimax_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("minimax");
    assertEquals(11, ep.models().size());
  }

  @Test
  void sensenova_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("sensenova");
    assertEquals(23, ep.models().size());
  }

  @Test
  void ai360_shouldHaveCorrectModelCount() {
    var ep = findEndpoint("ai360");
    assertEquals(20, ep.models().size());
  }

  @Test
  void customAndNewapi_shouldHaveZeroModels() {
    var custom = findEndpoint("custom");
    var newapi = findEndpoint("newapi");
    assertEquals(0, custom.models().size());
    assertEquals(0, newapi.models().size());
  }

  private static EndpointConfig findEndpoint(String id) {
    return AllEndpoints.getAll().stream()
      .filter(ep -> ep.id().equals(id))
      .findFirst()
      .orElseThrow(() -> new AssertionError("Endpoint not found: " + id));
  }
}
