export type CoverProviderId = "kkaiapi" | "openai" | "google" | "custom";

export interface CoverProviderPreset {
  readonly service: CoverProviderId;
  readonly label: string;
  readonly baseUrl: string;
  readonly api: "responses" | "images" | "gemini";
  readonly defaultModel: string;
  readonly models: readonly string[];
}

export const CUSTOM_COVER_LABEL = "自定义 (OpenAI 兼容)";
export const CUSTOM_COVER_DEFAULT_MODEL = "gpt-image-2";
export const CUSTOM_COVER_DEFAULT_API = "images";

export function buildCustomCoverPreset(config: {
  baseUrl: string;
  api?: "responses" | "images" | "gemini";
  model?: string;
}): CoverProviderPreset {
  return {
    service: "custom",
    label: config.baseUrl,
    baseUrl: config.baseUrl,
    api: config.api ?? CUSTOM_COVER_DEFAULT_API,
    defaultModel: config.model ?? CUSTOM_COVER_DEFAULT_MODEL,
    models: [config.model ?? CUSTOM_COVER_DEFAULT_MODEL],
  };
}

export const COVER_PROVIDER_PRESETS: readonly CoverProviderPreset[] = [
  {
    service: "kkaiapi",
    label: "kkaiapi",
    baseUrl: "https://api.kkaiapi.com/v1",
    api: "images",
    defaultModel: "gpt-image-2",
    models: ["gpt-image-2"],
  },
  {
    service: "openai",
    label: "OpenAI Images",
    baseUrl: "https://api.openai.com/v1",
    api: "images",
    defaultModel: "gpt-image-2",
    models: ["gpt-image-2"],
  },
  {
    service: "google",
    label: "Google Gemini",
    baseUrl: "https://generativelanguage.googleapis.com/v1beta",
    api: "gemini",
    defaultModel: "gemini-3.1-flash-image-preview",
    models: ["gemini-3.1-flash-image-preview", "gemini-2.5-flash-image"],
  },
];

export function resolveCoverProviderPreset(service: string | undefined): CoverProviderPreset | undefined {
  if (service === "custom") return undefined;
  return COVER_PROVIDER_PRESETS.find((provider) => provider.service === service);
}

export function isCustomCoverProvider(service: string): boolean {
  return service === "custom" || service.startsWith("custom:");
}

export function coverSecretKey(service: string): string {
  return `cover:${service}`;
}
