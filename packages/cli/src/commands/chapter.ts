import { Command } from "commander";
import { open } from "node:fs/promises";
import { log, logError } from "../utils.js";
import { withBackend, getApiClient, type BackendOptions } from "./backend-command.js";

export const chapterCommand = new Command("chapter")
  .description("Manage chapters");

const chapterListCommand = chapterCommand
  .command("list")
  .description("List chapters for a book")
  .argument("<book-id>", "Book ID")
  .option("--json", "Output JSON");

withBackend(chapterListCommand);

chapterListCommand.action(async (bookId: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const chapters = await api.listChapters(bookId);
      if (opts.json) {
        log(JSON.stringify({ chapters }, null, 2));
      } else {
        if (chapters.length === 0) {
          log(`No chapters found for book "${bookId}".`);
          return;
        }
        for (const ch of chapters) {
          log(`  ${ch.chapterNumber}. ${ch.title} [${ch.status}] (${ch.wordCount} words)`);
        }
      }
      return;
    }

    logError("chapter list requires --backend. Use 'inkos status --chapters' for local books.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to list chapters: ${e}`);
    }
    process.exit(1);
  }
});

const chapterShowCommand = chapterCommand
  .command("show")
  .description("Show chapter content")
  .argument("<id>", "Chapter ID (numeric)")
  .option("--json", "Output JSON")
  .option("--output <path>", "Save chapter content to file");

withBackend(chapterShowCommand);

chapterShowCommand.action(async (id: string, opts) => {
  try {
    const api = getApiClient(opts);
    if (api) {
      const chapter = await api.getChapter(parseInt(id, 10));
      if (opts.json) {
        log(JSON.stringify(chapter, null, 2));
      } else {
        log(`Chapter ${chapter.chapterNumber}: ${chapter.title}`);
        log(`Status: ${chapter.status} | Words: ${chapter.wordCount}`);
        log("");
        if (opts.output) {
          const file = await open(opts.output, "w");
          await file.writeFile(chapter.content);
          await file.close();
          log(`Content saved to: ${opts.output}`);
        } else {
          log(chapter.content);
        }
      }
      return;
    }

    logError("chapter show requires --backend. Use 'inkos status --chapters' for local books.");
    process.exit(1);
  } catch (e) {
    if (opts.json) {
      log(JSON.stringify({ error: String(e) }));
    } else {
      logError(`Failed to show chapter: ${e}`);
    }
    process.exit(1);
  }
});
