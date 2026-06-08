package com.inkos.core.llm;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EndpointConfigTest {

  @Test
  void builder_shouldBuildMinimalEndpoint() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .build();

    assertEquals("test", ep.id());
    assertEquals("openai-completions", ep.api());
  }

  @Test
  void builder_shouldBuildWithAllFields() {
    var compat = new EndpointConfig.ProviderCompat(true, true, false, false);
    var transport = new EndpointConfig.TransportDefaults("chat", true);
    var ep = EndpointConfig.builder()
      .id("test").label("Test").group("china").api("openai-completions")
      .baseUrl("https://test.com").modelsBaseUrl("https://test.com/v1")
      .checkModel("test-model").temperatureRange(0, 2)
      .defaultTemperature(1.0).writingTemperature(1.5)
      .temperatureHint("hint").compat(compat).transportDefaults(transport)
      .build();

    assertEquals("test", ep.id());
    assertEquals("china", ep.group());
    assertArrayEquals(new double[]{0, 2}, ep.temperatureRange());
    assertEquals(1.0, ep.defaultTemperature());
  }

  @Test
  void modelCard_shouldBuildWithRequiredFields() {
    var model = EndpointConfig.ModelCard.builder()
      .id("test-model").maxOutput(4096).contextWindowTokens(8192)
      .build();

    assertEquals("test-model", model.id());
    assertEquals(4096, model.maxOutput());
    assertEquals(8192, model.contextWindowTokens());
    assertTrue(model.enabled());
  }

  @Test
  void modelCard_shouldBuildWithAllFields() {
    var caps = EndpointConfig.ModelCard.Capabilities.builder()
      .text(true).imageInput(false).tools(true).reasoning(false)
      .build();
    var model = EndpointConfig.ModelCard.builder()
      .id("full-model").maxOutput(16384).contextWindowTokens(128000)
      .enabled(false).deploymentName("dep1").releasedAt("2025-01-01")
      .temperature(0.7).status("deprecated").replacement("new-model")
      .capabilities(caps)
      .build();

    assertEquals("full-model", model.id());
    assertFalse(model.enabled());
    assertTrue(model.capabilities().text());
    assertEquals("new-model", model.replacement());
  }

  @Test
  void capabilities_shouldBuild() {
    var caps = EndpointConfig.ModelCard.Capabilities.builder()
      .text(true).imageInput(true).imageOutput(false).tools(true).reasoning(true)
      .build();

    assertTrue(caps.text());
    assertTrue(caps.imageInput());
    assertFalse(caps.imageOutput());
  }

  @Test
  void findModel_shouldFindExactMatch() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .addModel(EndpointConfig.ModelCard.builder().id("my-model").maxOutput(4096).contextWindowTokens(8192).build())
      .build();

    var found = ep.findModel("my-model");
    assertTrue(found.isPresent());
    assertEquals("my-model", found.get().id());
  }

  @Test
  void findModel_shouldBeCaseInsensitive() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .addModel(EndpointConfig.ModelCard.builder().id("My-Model").maxOutput(4096).contextWindowTokens(8192).build())
      .build();

    assertTrue(ep.findModel("my-model").isPresent());
    assertTrue(ep.findModel("MY-MODEL").isPresent());
    assertTrue(ep.findModel("My-Model").isPresent());
  }

  @Test
  void findModel_shouldReturnEmptyForNull() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .addModel(EndpointConfig.ModelCard.builder().id("my-model").maxOutput(4096).contextWindowTokens(8192).build())
      .build();

    assertTrue(ep.findModel(null).isEmpty());
  }

  @Test
  void findModel_shouldReturnEmptyForUnknown() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .build();

    assertTrue(ep.findModel("nonexistent").isEmpty());
  }

  @Test
  void getEnabledModels_shouldFilterDisabled() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .addModel(EndpointConfig.ModelCard.builder().id("enabled-model").maxOutput(4096).contextWindowTokens(8192).build())
      .addModel(EndpointConfig.ModelCard.builder().id("disabled-model").maxOutput(4096).contextWindowTokens(8192).enabled(false).build())
      .build();

    var enabled = ep.getEnabledModels();
    assertEquals(1, enabled.size());
    assertEquals("enabled-model", enabled.getFirst().id());
  }

  @Test
  void addModels_shouldAddMultiple() {
    var ep = EndpointConfig.builder()
      .id("test").label("Test").baseUrl("https://test.com")
      .addModels(
        EndpointConfig.ModelCard.builder().id("model-1").maxOutput(4096).contextWindowTokens(8192).build(),
        EndpointConfig.ModelCard.builder().id("model-2").maxOutput(4096).contextWindowTokens(8192).build()
      )
      .build();

    assertEquals(2, ep.models().size());
  }
}
