import { Command } from 'commander';
import { ApiClient } from '../services/api-client.js';
import { BackendAdapter } from '../services/backend-adapter.js';

export interface BackendOptions {
  backend?: string;
}

export function withBackend(command: Command): Command {
  return command.option('--backend <url>', 'Use Java backend server at the given URL (e.g. http://localhost:8080)');
}

export function getApiClient(options: BackendOptions): ApiClient | null {
  if (options.backend) {
    return new ApiClient(options.backend);
  }
  return null;
}

export function getBackendAdapter(options: BackendOptions): { client: ApiClient; backend: BackendAdapter } | null {
  if (options.backend) {
    const client = new ApiClient(options.backend);
    const backend = new BackendAdapter(client);
    return { client, backend };
  }
  return null;
}
