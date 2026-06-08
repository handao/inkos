import { Command } from "commander";
import { log, logError } from "../utils.js";
import { withBackend, getApiClient, type BackendOptions } from "./backend-command.js";

export const adminCommand = new Command("admin")
  .description("Admin operations (requires Java backend)");

const adminUsersCommand = adminCommand
  .command("users")
  .description("List users")
  .option("--page <n>", "Page number", "0")
  .option("--size <n>", "Page size", "20")
  .option("--json", "Output JSON");

withBackend(adminUsersCommand);

adminUsersCommand.action(async (opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const { content, total } = await api.listUsers(parseInt(opts.page, 10), parseInt(opts.size, 10));
      if (opts.json) {
        log(JSON.stringify({ users: content, total }, null, 2));
      } else {
        if (content.length === 0) {
          log("No users found.");
          return;
        }
        log(`Users (total: ${total}):`);
        for (const u of content) {
          log(`  ${u.id}. ${u.nickname} (${u.email}) [${u.role}] status: ${u.status}`);
        }
      }
      return;
    }

    logError("admin users requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to list users: ${e}`);
    }
    process.exit(1);
  }
});

const adminUserStatusCommand = adminCommand
  .command("user-status")
  .description("Update user status")
  .argument("<user-id>", "User ID (numeric)")
  .argument("<status>", "New status (active / banned)")
  .option("--json", "Output JSON");

withBackend(adminUserStatusCommand);

adminUserStatusCommand.action(async (userId: string, status: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      await api.updateUserStatus(parseInt(userId, 10), status);
      if (opts.json) {
        log(JSON.stringify({ updated: parseInt(userId, 10), status }));
      } else {
        log(`User ${userId} status set to "${status}".`);
      }
      return;
    }

    logError("admin user-status requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to update user status: ${e}`);
    }
    process.exit(1);
  }
});

const adminWhitelistCommand = adminCommand
  .command("whitelist")
  .description("List whitelist registration codes")
  .option("--json", "Output JSON");

withBackend(adminWhitelistCommand);

adminWhitelistCommand.action(async (opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      if (opts.json) {
        log(JSON.stringify({ message: "Whitelist endpoint not available. Use admin users to manage users." }));
      } else {
        log("Whitelist operations not directly available via API. Use 'admin users' to manage users.");
      }
      return;
    }

    logError("admin whitelist requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to list whitelist: ${e}`);
    }
    process.exit(1);
  }
});
