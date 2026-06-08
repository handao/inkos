package com.inkos.core.state;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Filesystem-based state management interface for book project data.
 *
 * TS source: packages/core/src/state/manager.ts (560 lines)
 * Manages control documents (author_intent.md, current_focus.md, style_guide.md),
 * chapter metadata, book lock acquisition, and runtime state directories.
 */
public interface StateManager {

  /**
   * Ensure control document directories and default files exist for a book.
   * Creates: story/author_intent.md, story/current_focus.md, story/style_guide.md,
   * story/runtime/, story/outline/, story/roles/主要角色/, story/roles/次要角色/
   *
   * TS: ensureControlDocuments()
   */
  CompletableFuture<Void> ensureControlDocuments(String bookId, String authorIntent);

  /**
   * Load control documents for a book.
   * Returns author intent markdown, current focus markdown, and the runtime directory path.
   *
   * TS: loadControlDocuments()
   */
  CompletableFuture<ControlDocuments> loadControlDocuments(String bookId);

  /**
   * Acquire an exclusive file lock for a book to prevent concurrent writes.
   * Returns a release function to unlock.
   *
   * TS: acquireBookLock() → releases lock on process exit or explicit unlock
   */
  CompletableFuture<AutoCloseable> acquireBookLock(String bookId);

  /**
   * Update the current focus document.
   *
   * TS: writes current_focus.md after chapter completion
   */
  CompletableFuture<Void> updateCurrentFocus(String bookId, String focusContent);

  /**
   * Update the style guide.
   *
   * TS: appends writing methodology if missing
   */
  CompletableFuture<Void> updateStyleGuide(String bookId, String styleContent);

  /**
   * List all chapter directories under a book.
   *
   * TS: reads book dir chapters/ subdirectories
   */
  CompletableFuture<List<String>> listChapters(String bookId);

  /**
   * Read a chapter's content from the filesystem.
   */
  CompletableFuture<Optional<String>> readChapter(String bookId, int chapterNumber);

  /**
   * Write a chapter's content to the filesystem.
   */
  CompletableFuture<Void> writeChapter(String bookId, int chapterNumber, String content);

  /**
   * Load runtime state facts for a book.
   *
   * TS: loadSnapshotCurrentStateFacts() from runtime-state-store.ts
   */
  CompletableFuture<List<String>> loadRuntimeFacts(String bookId);

  /**
   * Save runtime state facts.
   *
   * TS: persists to runtime state store
   */
  CompletableFuture<Void> saveRuntimeFacts(String bookId, List<String> facts);

  /**
   * Get the project root path.
   */
  Path getProjectRoot();

  /**
   * Get the directory path for a specific book.
   */
  Path getBookDir(String bookId);

  record ControlDocuments(
    String authorIntent,
    String currentFocus,
    String runtimeDir
  ) {}
}
