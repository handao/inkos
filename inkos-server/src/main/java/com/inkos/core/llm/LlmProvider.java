package com.inkos.core.llm;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface LlmProvider {

    String getProviderName();

    LlmProviderType getType();

    CompletableFuture<LlmResponse> chat(LlmRequest request);

    CompletableFuture<LlmResponse> chatStream(LlmRequest request, Consumer<String> onChunk);

    boolean validateConfig(LlmProviderConfig config);
}
