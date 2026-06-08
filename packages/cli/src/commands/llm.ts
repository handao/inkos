import { Command } from "commander";
import { log, logError } from "../utils.js";
import { withBackend, getApiClient, type BackendOptions } from "./backend-command.js";

export const llmCommand = new Command("llm")
  .description("Manage LLM service configurations");

const llmListCommand = llmCommand
  .command("list")
  .description("List configured LLM services")
  .option("--json", "Output JSON");

withBackend(llmListCommand);

llmListCommand.action(async (opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const services = await api.listServices();
      if (opts.json) {
        log(JSON.stringify({ services }, null, 2));
      } else {
        if (services.length === 0) {
          log("No LLM services configured.");
          return;
        }
        for (const s of services) {
          log(`  ${s.id}. ${s.label || s.serviceType} [${s.serviceType}]`);
          log(`     URL: ${s.baseUrl}`);
          log(`     Model: ${s.defaultModel || s.models}`);
          log(`     Default: ${s.isDefault ? "yes" : "no"}`);
          log("");
        }
      }
      return;
    }

    logError("llm list requires --backend. Use 'inkos config show-models' for local config.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to list LLM services: ${e}`);
    }
    process.exit(1);
  }
});

const llmAddCommand = llmCommand
  .command("add")
  .description("Add a new LLM service")
  .argument("<name>", "Service name/type")
  .option("--label <label>", "Human-readable label")
  .option("--base-url <url>", "API base URL")
  .option("--api-type <type>", "API type (openai / anthropic / custom)")
  .option("--models <models>", "Comma-separated model list")
  .option("--default-model <model>", "Default model")
  .option("--json", "Output JSON");

withBackend(llmAddCommand);

llmAddCommand.action(async (name: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const result = await api.saveService({
        serviceType: name,
        label: opts.label,
        baseUrl: opts.baseUrl,
        apiType: opts.apiType,
        models: opts.models ? opts.models.split(",").map((m: string) => m.trim()) : undefined,
        defaultModel: opts.defaultModel,
      });
      if (opts.json) {
        log(JSON.stringify(result, null, 2));
      } else {
        log(`LLM service "${name}" added (id: ${result.id}).`);
      }
      return;
    }

    logError("llm add requires --backend. Use 'inkos config set-model' for local config.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to add LLM service: ${e}`);
    }
    process.exit(1);
  }
});

const llmRemoveCommand = llmCommand
  .command("remove")
  .description("Remove an LLM service")
  .argument("<id>", "Service ID (numeric)")
  .option("--json", "Output JSON");

withBackend(llmRemoveCommand);

llmRemoveCommand.action(async (id: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      await api.deleteService(parseInt(id, 10));
      if (opts.json) {
        log(JSON.stringify({ deleted: parseInt(id, 10) }));
      } else {
        log(`LLM service ${id} deleted.`);
      }
      return;
    }

    logError("llm remove requires --backend. Use 'inkos config remove-model' for local config.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to remove LLM service: ${e}`);
    }
    process.exit(1);
  }
});
