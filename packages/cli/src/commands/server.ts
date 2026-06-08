import { Command } from "commander";
import { findProjectRoot, log, logError } from "../utils.js";
import { startServer, stopServer, isRunning, waitForReady } from "../services/server-manager.js";

export const serverCommand = new Command("server")
  .description("Manage the Java backend server");

serverCommand
  .command("start")
  .description("Start the Java backend server")
  .option("--wait", "Wait for the server to be ready before returning")
  .option("--timeout <ms>", "Timeout in milliseconds for --wait", "120000")
  .action(async (opts) => {
    try {
      const root = findProjectRoot();
      const running = await isRunning(root);
      if (running) {
        log("Server is already running.");
        return;
      }

      log("Starting InkOS Java backend server...");
      await startServer(root);

      if (opts.wait) {
        const timeout = parseInt(opts.timeout, 10);
        log("Waiting for server to be ready...");
        const ready = await waitForReady(timeout);
        if (ready) {
          log("Server is ready at http://localhost:8080");
        } else {
          logError("Server started but not ready within timeout.");
          process.exit(1);
        }
      } else {
        log("Server starting in background. Use 'inkos server status' to check.");
      }
    } catch (e) {
      logError(`Failed to start server: ${e}`);
      process.exit(1);
    }
  });

serverCommand
  .command("status")
  .description("Check if the backend server is running")
  .option("--json", "Output JSON")
  .action(async (opts) => {
    try {
      const root = findProjectRoot();
      const running = await isRunning(root);

      if (opts.json) {
        log(JSON.stringify({ running }, null, 2));
        return;
      }

      if (running) {
        log("Server is running.");
        const ready = await waitForReady(5000);
        if (ready) {
          log("Server is ready and accepting requests.");
        } else {
          log("Server process is running but not yet ready.");
        }
      } else {
        log("Server is not running.");
      }
    } catch (e) {
      logError(`Failed to check server status: ${e}`);
      process.exit(1);
    }
  });

serverCommand
  .command("stop")
  .description("Stop the Java backend server")
  .action(async () => {
    try {
      const root = findProjectRoot();
      const running = await isRunning(root);
      if (!running) {
        log("Server is not running.");
        return;
      }

      log("Stopping server...");
      await stopServer(root);
      log("Server stopped.");
    } catch (e) {
      logError(`Failed to stop server: ${e}`);
      process.exit(1);
    }
  });
