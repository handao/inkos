import { Command } from "commander";
import { log, logError } from "../utils.js";
import { withBackend, getApiClient, type BackendOptions } from "./backend-command.js";

export const sessionCommand = new Command("session")
  .description("Manage AI writing sessions");

const sessionListCommand = sessionCommand
  .command("list")
  .description("List sessions")
  .argument("[book-id]", "Filter by book ID")
  .option("--json", "Output JSON");

withBackend(sessionListCommand);

sessionListCommand.action(async (bookId: string | undefined, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const sessions = await api.listSessions(bookId);
      if (opts.json) {
        log(JSON.stringify({ sessions }, null, 2));
      } else {
        if (sessions.length === 0) {
          log("No sessions found.");
          return;
        }
        for (const s of sessions) {
          log(`  ${s.sessionId} | ${s.title || "(no title)"} | book: ${s.bookId} | mode: ${s.mode}`);
          log(`     Created: ${s.createdAt} | Draft: ${s.isDraft ? "yes" : "no"}`);
          log("");
        }
      }
      return;
    }

    logError("session list requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to list sessions: ${e}`);
    }
    process.exit(1);
  }
});

const sessionCreateCommand = sessionCommand
  .command("create")
  .description("Create a new writing session")
  .argument("<book-id>", "Book ID")
  .option("--title <title>", "Session title")
  .option("--mode <mode>", "Session mode (e.g., write, revise)")
  .option("--json", "Output JSON");

withBackend(sessionCreateCommand);

sessionCreateCommand.action(async (bookId: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const session = await api.createSession(bookId, opts.title, opts.mode);
      if (opts.json) {
        log(JSON.stringify(session, null, 2));
      } else {
        log(`Session created: ${session.sessionId}`);
        log(`  Book: ${session.bookId}`);
        log(`  Title: ${session.title || "(none)"}`);
        log(`  Mode: ${session.mode}`);
      }
      return;
    }

    logError("session create requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to create session: ${e}`);
    }
    process.exit(1);
  }
});

const sessionSendCommand = sessionCommand
  .command("send")
  .description("Send a message to a session")
  .argument("<session-id>", "Session ID")
  .argument("<message>", "Message content")
  .option("--json", "Output JSON");

withBackend(sessionSendCommand);

sessionSendCommand.action(async (sessionId: string, message: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const reply = await api.sendMessage(sessionId, message);
      if (opts.json) {
        log(JSON.stringify(reply, null, 2));
      } else {
        log(`[${reply.role}] ${reply.content}`);
      }
      return;
    }

    logError("session send requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to send message: ${e}`);
    }
    process.exit(1);
  }
});

const sessionShowCommand = sessionCommand
  .command("show")
  .description("Show session details")
  .argument("<session-id>", "Session ID")
  .option("--json", "Output JSON");

withBackend(sessionShowCommand);

sessionShowCommand.action(async (sessionId: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const session = await api.getSession(sessionId);
      if (opts.json) {
        log(JSON.stringify(session, null, 2));
      } else {
        log(`Session: ${session.sessionId}`);
        log(`Book: ${session.bookId}`);
        log(`Title: ${session.title || "(none)"}`);
        log(`Mode: ${session.mode}`);
        log(`Messages: ${session.messages.length}`);
        log("");
        for (const msg of session.messages) {
          const preview = msg.content.length > 200 ? `${msg.content.slice(0, 200)}...` : msg.content;
          log(`[${msg.role}] ${preview}`);
          log("");
        }
      }
      return;
    }

    logError("session show requires --backend.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to show session: ${e}`);
    }
    process.exit(1);
  }
});
