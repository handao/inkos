package com.inkos.core.llm;

import com.inkos.core.llm.endpoint.AllEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EndpointRegistryTest {

  private EndpointRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new EndpointRegistry();
  }

  @Test
  void constructor_shouldLoadAllEndpoints() {
    assertEquals(AllEndpoints.getAll().size(), registry.size());
  }

  @Test
  void register_shouldAddNewEndpoint() {
    var ep = EndpointConfig.builder()
      .id("test-provider").label("Test").baseUrl("https://test.com")
      .addModel(EndpointConfig.ModelCard.builder().id("test-model").maxOutput(4096).contextWindowTokens(8192).build())
      .build();
    registry.register(ep);
    assertNotNull(registry.getEndpoint("test-provider"));
  }

  @Test
  void getEndpoint_shouldReturnNullForUnknown() {
    assertNull(registry.getEndpoint("nonexistent"));
  }

  @Test
  void getEndpoint_shouldReturnRegisteredEndpoint() {
    var ep = registry.getEndpoint("openai");
    assertNotNull(ep);
    assertEquals("openai", ep.id());
  }

  @Test
  void getAllEndpoints_shouldReturnAll() {
    assertEquals(registry.size(), registry.getAllEndpoints().size());
  }

  @Test
  void getAllEndpoints_shouldBeUnmodifiable() {
    var all = registry.getAllEndpoints();
    assertThrows(UnsupportedOperationException.class, () -> all.add(null));
  }

  @Test
  void getEndpointsByGroup_shouldFilterByGroup() {
    var overseas = registry.getEndpointsByGroup("overseas");
    assertFalse(overseas.isEmpty());
    assertTrue(overseas.stream().allMatch(e -> "overseas".equals(e.group())));
  }

  @Test
  void getEndpointsByGroup_shouldReturnEmptyForUnknownGroup() {
    assertTrue(registry.getEndpointsByGroup("nonexistent").isEmpty());
  }

  @Test
  void lookupModel_shouldFindExactMatch() {
    var model = registry.lookupModel("openai", "gpt-4o-mini");
    assertNotNull(model);
    assertEquals("gpt-4o-mini", model.id());
  }

  @Test
  void lookupModel_shouldUseCrossProviderFallback() {
    var model = registry.lookupModel("unknown", "gpt-4o-mini");
    assertNotNull(model);
  }

  @Test
  void lookupModel_shouldReturnNullForNonexistentModel() {
    var model = registry.lookupModel("openai", "this-model-definitely-does-not-exist-xyz");
    assertNull(model);
  }
}
