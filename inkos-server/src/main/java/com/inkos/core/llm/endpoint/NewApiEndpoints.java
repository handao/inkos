package com.inkos.core.llm.endpoint;

import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class NewApiEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("newapi").label("New API (中转网关)").group("aggregator").api("openai-completions")
      .baseUrl("")
      .build();
  }

  private NewApiEndpoints() {}
}
