package com.inkos.core.agent;

import com.inkos.core.pipeline.AgentContext;
import java.util.concurrent.CompletableFuture;

public interface Agent {
  String getName();
  String getRole();
  CompletableFuture<AgentResult> execute(AgentContext context, AgentInput input);
}
