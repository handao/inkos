import { Command } from "commander";
import { PipelineRunner } from "@actalk/inkos-core";
import { loadConfig, buildPipelineConfig, findProjectRoot, resolveBookId, log, logError } from "../utils.js";
import { withBackend, getBackendAdapter, type BackendOptions } from "./backend-command.js";

export const auditCommand = new Command("audit")
  .description("Audit a chapter for continuity issues");

withBackend(auditCommand);

auditCommand
  .argument("[book-id]", "Book ID (auto-detected if only one book)")
  .argument("[chapter]", "Chapter number (defaults to latest)")
  .option("--json", "Output JSON")
  .action(async (bookIdArg: string | undefined, chapterStr: string | undefined, opts) => {
    try {
      const be = getBackendAdapter(opts);
      if (be) {
        const root = findProjectRoot();
        let bookId: string;
        let chapterNumber: number | undefined;
        if (bookIdArg && /^\d+$/.test(bookIdArg)) {
          bookId = await resolveBookId(undefined, root);
          chapterNumber = parseInt(bookIdArg, 10);
        } else {
          bookId = await resolveBookId(bookIdArg, root);
          chapterNumber = chapterStr ? parseInt(chapterStr, 10) : undefined;
        }
        if (!opts.json) log(`Auditing "${bookId}" chapter ${chapterNumber ?? "(latest)"} via backend...`);
        const result = await be.backend.auditChapter(bookId, chapterNumber ?? 0);
        if (opts.json) {
          log(JSON.stringify(result, null, 2));
        } else {
          log(`  Audit: ${result.passed ? "PASSED" : "FAILED"}`);
          if (result.issues.length > 0) {
            log("  Issues:");
            for (const issue of result.issues) {
              log(`    - ${issue}`);
            }
          }
        }
        return;
      }

      const config = await loadConfig();
      const root = findProjectRoot();

      let bookId: string;
      let chapterNumber: number | undefined;
      if (bookIdArg && /^\d+$/.test(bookIdArg)) {
        bookId = await resolveBookId(undefined, root);
        chapterNumber = parseInt(bookIdArg, 10);
      } else {
        bookId = await resolveBookId(bookIdArg, root);
        chapterNumber = chapterStr ? parseInt(chapterStr, 10) : undefined;
      }

      const pipeline = new PipelineRunner(buildPipelineConfig(config, root));

      if (!opts.json) log(`Auditing "${bookId}"${chapterNumber ? ` chapter ${chapterNumber}` : " (latest)"}...`);

      const result = await pipeline.auditDraft(bookId, chapterNumber);

      if (opts.json) {
        log(JSON.stringify(result, null, 2));
      } else {
        log(`  Chapter ${result.chapterNumber}: ${result.passed ? "PASSED" : "FAILED"}`);
        log(`  Summary: ${result.summary}`);
        if (result.issues.length > 0) {
          log("  Issues:");
          for (const issue of result.issues) {
            log(`    [${issue.severity}] ${issue.category}: ${issue.description}`);
          }
        }
      }
    } catch (e) {
      if (opts.json) {
        log(JSON.stringify({ error: String(e) }));
      } else {
        logError(`Audit failed: ${e}`);
      }
      process.exit(1);
    }
  });
