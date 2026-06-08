package com.inkos.core.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenericLlmProvider implements LlmProvider {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final EndpointConfig endpoint;
    private final LlmProviderType type;
    private final String apiKey;
    private final HttpClient httpClient;

    public GenericLlmProvider(EndpointConfig endpoint, LlmProviderType type) {
        this(endpoint, type, null);
    }

    public GenericLlmProvider(EndpointConfig endpoint, LlmProviderType type, String apiKey) {
        this.endpoint = endpoint;
        this.type = type;
        this.apiKey = apiKey != null ? apiKey : resolveApiKeyFromEnv(endpoint.id());
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Override
    public String getProviderName() {
        return endpoint.id();
    }

    @Override
    public LlmProviderType getType() {
        return type;
    }

    @Override
    public CompletableFuture<LlmResponse> chat(LlmRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String apiProtocol = endpoint.api();
            if ("anthropic-messages".equals(apiProtocol)) {
                return chatAnthropic(request);
            } else if ("google-generative-ai".equals(apiProtocol)) {
                return chatGoogle(request);
            }
            return chatOpenAI(request);
        });
    }

    @Override
    public CompletableFuture<LlmResponse> chatStream(LlmRequest request, Consumer<String> onChunk) {
        return CompletableFuture.supplyAsync(() -> {
            String apiProtocol = endpoint.api();
            if ("anthropic-messages".equals(apiProtocol) || "google-generative-ai".equals(apiProtocol)) {
                return chat(request).join();
            }
            return chatOpenAIStream(request, onChunk);
        });
    }

    @Override
    public boolean validateConfig(LlmProviderConfig config) {
        return config.baseUrl() != null && !config.baseUrl().isBlank();
    }

    private LlmResponse chatOpenAI(LlmRequest request) {
        String url = endpoint.baseUrl() + "/chat/completions";
        String body = buildChatRequestBody(request);
        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + resolveApiKey())
            .timeout(Duration.ofMinutes(5))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseChatResponse(response.body());
        } catch (Exception e) {
            throw new RuntimeException("LLM chat request failed: " + e.getMessage(), e);
        }
    }

    private LlmResponse chatAnthropic(LlmRequest request) {
        String url = endpoint.baseUrl() + "/v1/messages";
        String body = buildAnthropicRequestBody(request);
        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("x-api-key", resolveApiKey())
            .header("anthropic-version", "2023-06-01")
            .timeout(Duration.ofMinutes(5))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseAnthropicResponse(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Anthropic chat request failed: " + e.getMessage(), e);
        }
    }

    private LlmResponse chatGoogle(LlmRequest request) {
        String model = request.model();
        String url = endpoint.baseUrl() + "/models/" + model + ":generateContent?key=" + resolveApiKey();
        String body = buildGoogleRequestBody(request);
        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofMinutes(5))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseGoogleResponse(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Google chat request failed: " + e.getMessage(), e);
        }
    }

    private LlmResponse chatOpenAIStream(LlmRequest request, Consumer<String> onChunk) {
        String url = endpoint.baseUrl() + "/chat/completions";
        String body = buildChatRequestBody(request);
        var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + resolveApiKey())
            .header("Accept", "text/event-stream")
            .timeout(Duration.ofMinutes(10))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseStreamResponse(response.body(), onChunk);
        } catch (Exception e) {
            throw new RuntimeException("LLM stream request failed: " + e.getMessage(), e);
        }
    }

    private String buildChatRequestBody(LlmRequest request) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"").append(escapeJson(request.model())).append("\",");
        json.append("\"messages\":[");
        for (int i = 0; i < request.messages().size(); i++) {
            if (i > 0) json.append(",");
            var msg = request.messages().get(i);
            json.append("{\"role\":\"").append(msg.role()).append("\",\"content\":\"")
                .append(escapeJson(msg.content())).append("\"}");
        }
        json.append("],");
        json.append("\"temperature\":").append(request.temperature()).append(",");
        json.append("\"max_tokens\":").append(request.maxTokens()).append(",");
        json.append("\"stream\":").append(request.stream());
        json.append("}");
        return json.toString();
    }

    private String buildAnthropicRequestBody(LlmRequest request) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"").append(escapeJson(request.model())).append("\",");
        json.append("\"max_tokens\":").append(request.maxTokens()).append(",");
        if (request.stream()) {
            json.append("\"stream\":true,");
        }

        // Extract system prompt from messages
        String systemPrompt = null;
        var userMessages = new ArrayList<String>();
        for (var msg : request.messages()) {
            if ("system".equals(msg.role())) {
                systemPrompt = msg.content();
            } else {
                userMessages.add("{\"role\":\"" + msg.role() + "\",\"content\":\""
                    + escapeJson(msg.content()) + "\"}");
            }
        }

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            json.append("\"system\":\"").append(escapeJson(systemPrompt)).append("\",");
        }

        json.append("\"messages\":[");
        for (int i = 0; i < userMessages.size(); i++) {
            if (i > 0) json.append(",");
            json.append(userMessages.get(i));
        }
        json.append("],");
        json.append("\"temperature\":").append(request.temperature());
        json.append("}");
        return json.toString();
    }

    private String buildGoogleRequestBody(LlmRequest request) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // System instruction
        String systemPrompt = null;
        var userMessages = new ArrayList<String>();
        for (var msg : request.messages()) {
            if ("system".equals(msg.role())) {
                systemPrompt = msg.content();
            } else {
                userMessages.add("{\"role\":\"" + msg.role() + "\",\"parts\":[{\"text\":\""
                    + escapeJson(msg.content()) + "\"}]}");
            }
        }

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            json.append("\"systemInstruction\":{\"parts\":[{\"text\":\"")
                .append(escapeJson(systemPrompt)).append("\"}]},");
        }

        json.append("\"contents\":[");
        for (int i = 0; i < userMessages.size(); i++) {
            if (i > 0) json.append(",");
            json.append(userMessages.get(i));
        }
        json.append("],");
        json.append("\"generationConfig\":{");
        json.append("\"temperature\":").append(request.temperature()).append(",");
        json.append("\"maxOutputTokens\":").append(request.maxTokens());
        json.append("}");
        json.append("}");
        return json.toString();
    }

    LlmResponse parseAnthropicResponse(String json) {
        try {
            JsonNode root = JSON.readTree(json);
            JsonNode content = root.get("content");
            String text = "";
            if (content != null && content.isArray() && !content.isEmpty()) {
                text = content.get(0).has("text") ? content.get(0).get("text").asText() : "";
            }

            int promptTokens = 0;
            int completionTokens = 0;
            JsonNode usage = root.get("usage");
            if (usage != null) {
                promptTokens = usage.has("input_tokens") ? usage.get("input_tokens").asInt(0) : 0;
                completionTokens = usage.has("output_tokens") ? usage.get("output_tokens").asInt(0) : 0;
            }

            return new LlmResponse(text,
                new LlmResponse.Usage(promptTokens, completionTokens, promptTokens + completionTokens),
                java.util.List.of());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Anthropic response: " + e.getMessage(), e);
        }
    }

    LlmResponse parseGoogleResponse(String json) {
        try {
            JsonNode root = JSON.readTree(json);
            JsonNode candidates = root.get("candidates");
            String text = "";
            if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    text = parts.get(0).has("text") ? parts.get(0).get("text").asText() : "";
                }
            }

            int promptTokens = 0;
            int completionTokens = 0;
            JsonNode usage = root.get("usageMetadata");
            if (usage != null) {
                promptTokens = usage.has("promptTokenCount") ? usage.get("promptTokenCount").asInt(0) : 0;
                completionTokens = usage.has("candidatesTokenCount") ? usage.get("candidatesTokenCount").asInt(0) : 0;
            }

            return new LlmResponse(text,
                new LlmResponse.Usage(promptTokens, completionTokens, promptTokens + completionTokens),
                java.util.List.of());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Google response: " + e.getMessage(), e);
        }
    }

    private LlmResponse parseChatResponse(String body) {
        try {
            JsonNode root = JSON.readTree(body);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).get("message");
                String content = message != null && message.has("content") && !message.get("content").isNull()
                    ? message.get("content").asText()
                    : "";

                int promptTokens = 0;
                int completionTokens = 0;
                int totalTokens = 0;
                JsonNode usage = root.get("usage");
                if (usage != null) {
                    promptTokens = usage.has("prompt_tokens") ? usage.get("prompt_tokens").asInt(0) : 0;
                    completionTokens = usage.has("completion_tokens") ? usage.get("completion_tokens").asInt(0) : 0;
                    totalTokens = usage.has("total_tokens") ? usage.get("total_tokens").asInt(0) : 0;
                }

                var toolCalls = new ArrayList<LlmResponse.ToolCall>();
                if (message != null && message.has("tool_calls")) {
                    for (JsonNode tc : message.get("tool_calls")) {
                        toolCalls.add(new LlmResponse.ToolCall(
                            tc.has("id") ? tc.get("id").asText("") : "",
                            tc.has("function") ? tc.get("function").get("name").asText("") : "",
                            tc.has("function") ? tc.get("function").get("arguments").asText("") : ""
                        ));
                    }
                }

                return new LlmResponse(content,
                    new LlmResponse.Usage(promptTokens, completionTokens, totalTokens),
                    toolCalls);
            }

            JsonNode error = root.get("error");
            if (error != null) {
                String msg = error.has("message") ? error.get("message").asText() : "Unknown API error";
                throw new RuntimeException("LLM API error: " + msg);
            }

            return new LlmResponse(body, new LlmResponse.Usage(0, 0, 0), java.util.List.of());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM response: " + e.getMessage(), e);
        }
    }

    private LlmResponse parseStreamResponse(String body, Consumer<String> onChunk) {
        StringBuilder content = new StringBuilder();
        int promptTokens = 0;
        int completionTokens = 0;
        int totalTokens = 0;

        for (String line : body.split("\n")) {
            if (!line.startsWith("data: ")) continue;
            String data = line.substring(6).trim();
            if ("[DONE]".equals(data)) continue;

            try {
                JsonNode chunk = JSON.readTree(data);
                JsonNode choices = chunk.get("choices");
                if (choices == null || !choices.isArray() || choices.isEmpty()) continue;

                JsonNode delta = choices.get(0).get("delta");
                if (delta != null && delta.has("content") && !delta.get("content").isNull()) {
                    String text = delta.get("content").asText();
                    content.append(text);
                    onChunk.accept(text);
                }

                JsonNode usage = chunk.get("usage");
                if (usage != null) {
                    promptTokens = usage.has("prompt_tokens") ? usage.get("prompt_tokens").asInt(0) : promptTokens;
                    completionTokens = usage.has("completion_tokens") ? usage.get("completion_tokens").asInt(0) : completionTokens;
                    totalTokens = usage.has("total_tokens") ? usage.get("total_tokens").asInt(0) : totalTokens;
                }
            } catch (Exception ignored) {
                // skip malformed SSE lines; they're not critical
                content.append(data);
                onChunk.accept(data);
            }
        }

        return new LlmResponse(content.toString(),
            new LlmResponse.Usage(promptTokens, completionTokens, totalTokens),
            java.util.List.of());
    }

    private String resolveApiKey() {
        return apiKey;
    }

    private static String resolveApiKeyFromEnv(String endpointId) {
        if (endpointId == null) return "";
        String envName = endpointId.toUpperCase().replace("-", "_").replace(".", "_") + "_API_KEY";
        String direct = System.getenv(envName);
        if (direct != null && !direct.isBlank()) return direct;

        return switch (endpointId.toLowerCase()) {
            case "openai" -> getEnv("OPENAI_API_KEY", "");
            case "anthropic" -> getEnv("ANTHROPIC_API_KEY", "");
            case "deepseek" -> getEnv("DEEPSEEK_API_KEY", "");
            case "google" -> getEnv("GOOGLE_API_KEY", "");
            case "moonshot" -> getEnv("MOONSHOT_API_KEY", "");
            case "zhipu" -> getEnv("ZHIPU_API_KEY", "");
            case "siliconcloud" -> getEnv("SILICONCLOUD_API_KEY", "");
            case "ollama" -> ""; // Ollama doesn't need an API key
            default -> getEnv(envName, "");
        };
    }

    private static String getEnv(String name, String fallback) {
        String val = System.getenv(name);
        return val != null && !val.isBlank() ? val : fallback;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
