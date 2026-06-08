package com.inkos.core.llm.endpoint;

import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class CustomEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("custom").label("自定义端点").api("openai-completions")
      .baseUrl("")
      .build();
  }

  private CustomEndpoints() {}
}
