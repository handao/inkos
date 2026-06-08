package com.inkos.core.state;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Filesystem-based StateManager implementation using Java NIO.
 *
 * TS reference: packages/core/src/state/manager.ts
 * Directory layout per book:
 *   {projectRoot}/books/{bookId}/
 *     book.json
 *     story/
 *       author_intent.md
 *       current_focus.md
 *       style_guide.md
 *       runtime/
 *       outline/
 *       roles/主要角色/
 *       roles/次要角色/
 *     chapters/
 *       {chapterNumber}/
 *         content.md
 *         meta.json
 *     .lock
 */
public class FileSystemStateManager implements StateManager {

  private static final ObjectMapper JSON = new ObjectMapper();

  private final Path projectRoot;
  private final Set<String> activeWrites = new HashSet<>();
  private final Map<String, FileLock> activeFileLocks = new ConcurrentHashMap<>();
  private final Map<String, FileChannel> activeLockChannels = new ConcurrentHashMap<>();

  public FileSystemStateManager(Path projectRoot) {
    this.projectRoot = projectRoot;
  }

  public Path getBookDirectory(String bookId) {
    return bookDir(bookId);
  }

  public Path resolveChapterPath(String bookId, int chapterNumber) {
    return bookDir(bookId).resolve("chapters")
      .resolve(String.format("%04d", chapterNumber)).resolve("content.md");
  }

  public String readControlDocument(String bookId) {
    try {
      Path bookJson = bookDir(bookId).resolve("book.json");
      if (Files.exists(bookJson)) {
        return Files.readString(bookJson);
      }
      return "{}";
    } catch (IOException e) {
      throw new RuntimeException("Failed to read control document for book: " + bookId, e);
    }
  }

  public void writeControlDocument(String bookId, String content) {
    try {
      Path bookJson = bookDir(bookId).resolve("book.json");
      Files.createDirectories(bookJson.getParent());
      Files.writeString(bookJson, content);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write control document for book: " + bookId, e);
    }
  }

  public void releaseBookLock(String bookId) {
    activeWrites.remove(bookId);
    FileChannel channel = activeLockChannels.remove(bookId);
    FileLock fileLock = activeFileLocks.remove(bookId);
    try {
      if (fileLock != null) fileLock.release();
      if (channel != null) channel.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to release lock for book: " + bookId, e);
    }
  }

  private Path bookDir(String bookId) {
    return projectRoot.resolve("books").resolve(bookId);
  }

  private Path storyDir(String bookId) {
    return bookDir(bookId).resolve("story");
  }

  private Path chapterDir(String bookId, int chapterNumber) {
    return bookDir(bookId).resolve("chapters").resolve(String.format("%04d", chapterNumber));
  }

  @Override
  public Path getProjectRoot() {
    return projectRoot;
  }

  @Override
  public Path getBookDir(String bookId) {
    return bookDir(bookId);
  }

  @Override
  public CompletableFuture<Void> ensureControlDocuments(String bookId, String authorIntent) {
    return CompletableFuture.runAsync(() -> {
      try {
        Path story = storyDir(bookId);
        Files.createDirectories(story.resolve("runtime"));
        Files.createDirectories(story.resolve("outline"));
        Files.createDirectories(story.resolve("roles").resolve("主要角色"));
        Files.createDirectories(story.resolve("roles").resolve("次要角色"));

        writeIfMissing(story.resolve("author_intent.md"), authorIntent != null && !authorIntent.isBlank()
          ? authorIntent.stripTrailing() + "\n"
          : "# 作者意图\n\n（在这里描述这本书的长期创作方向。）\n");

        writeIfMissing(story.resolve("current_focus.md"),
          "# 当前聚焦\n\n## 当前重点\n\n（描述接下来 1-3 章最需要优先推进的内容。）\n");

        writeIfMissing(story.resolve("style_guide"), "");
      } catch (IOException e) {
        throw new RuntimeException("Failed to ensure control documents for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<ControlDocuments> loadControlDocuments(String bookId) {
    return ensureControlDocuments(bookId, null)
      .thenCompose(v -> CompletableFuture.supplyAsync(() -> {
        try {
          Path story = storyDir(bookId);
          String authorIntent = Files.readString(story.resolve("author_intent.md"));
          String currentFocus = Files.readString(story.resolve("current_focus.md"));
          String runtimeDir = story.resolve("runtime").toString();
          return new ControlDocuments(authorIntent, currentFocus, runtimeDir);
        } catch (IOException e) {
          throw new RuntimeException("Failed to load control documents for book: " + bookId, e);
        }
      }));
  }

  @Override
  public CompletableFuture<AutoCloseable> acquireBookLock(String bookId) {
    return CompletableFuture.supplyAsync(() -> {
      // Same-process lock detection
      if (activeWrites.contains(bookId)) {
        throw new IllegalStateException("Book already locked in this process: " + bookId);
      }
      activeWrites.add(bookId);

      try {
        Path lockFile = bookDir(bookId).resolve(".lock");
        Files.createDirectories(lockFile.getParent());
        FileChannel channel = FileChannel.open(lockFile,
          StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
        FileLock fileLock = channel.tryLock();
        if (fileLock == null) {
          channel.close();
          activeWrites.remove(bookId);
          throw new OverlappingFileLockException();
        }

        activeFileLocks.put(bookId, fileLock);
        activeLockChannels.put(bookId, channel);

        return () -> {
          try {
            fileLock.release();
            channel.close();
          } finally {
            activeWrites.remove(bookId);
            activeFileLocks.remove(bookId);
            activeLockChannels.remove(bookId);
          }
        };
      } catch (IOException e) {
        activeWrites.remove(bookId);
        throw new RuntimeException("Failed to acquire lock for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<Void> updateCurrentFocus(String bookId, String focusContent) {
    return CompletableFuture.runAsync(() -> {
      try {
        Files.writeString(storyDir(bookId).resolve("current_focus.md"), focusContent);
      } catch (IOException e) {
        throw new RuntimeException("Failed to update current focus for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<Void> updateStyleGuide(String bookId, String styleContent) {
    return CompletableFuture.runAsync(() -> {
      try {
        Files.writeString(storyDir(bookId).resolve("style_guide"), styleContent);
      } catch (IOException e) {
        throw new RuntimeException("Failed to update style guide for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<List<String>> listChapters(String bookId) {
    return CompletableFuture.supplyAsync(() -> {
      Path chaptersDir = bookDir(bookId).resolve("chapters");
      if (!Files.isDirectory(chaptersDir)) return List.of();
      try (Stream<Path> entries = Files.list(chaptersDir)) {
        return entries
          .filter(Files::isDirectory)
          .map(p -> p.getFileName().toString())
          .sorted()
          .toList();
      } catch (IOException e) {
        throw new RuntimeException("Failed to list chapters for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<Optional<String>> readChapter(String bookId, int chapterNumber) {
    return CompletableFuture.supplyAsync(() -> {
      Path contentFile = chapterDir(bookId, chapterNumber).resolve("content.md");
      if (!Files.exists(contentFile)) return Optional.empty();
      try {
        return Optional.of(Files.readString(contentFile));
      } catch (IOException e) {
        throw new RuntimeException("Failed to read chapter " + chapterNumber + " for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<Void> writeChapter(String bookId, int chapterNumber, String content) {
    return CompletableFuture.runAsync(() -> {
      try {
        Path dir = chapterDir(bookId, chapterNumber);
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("content.md"), content);
      } catch (IOException e) {
        throw new RuntimeException("Failed to write chapter " + chapterNumber + " for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<List<String>> loadRuntimeFacts(String bookId) {
    return CompletableFuture.supplyAsync(() -> {
      Path factsFile = storyDir(bookId).resolve("runtime").resolve("facts.json");
      if (!Files.exists(factsFile)) return List.of();
      try {
        String json = Files.readString(factsFile);
        return JSON.readValue(json, new TypeReference<List<String>>() {});
      } catch (IOException e) {
        throw new RuntimeException("Failed to load runtime facts for book: " + bookId, e);
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveRuntimeFacts(String bookId, List<String> facts) {
    return CompletableFuture.runAsync(() -> {
      try {
        Path factsFile = storyDir(bookId).resolve("runtime").resolve("facts.json");
        Files.createDirectories(factsFile.getParent());
        String json = JSON.writeValueAsString(facts);
        Files.writeString(factsFile, json);
      } catch (IOException e) {
        throw new RuntimeException("Failed to save runtime facts for book: " + bookId, e);
      }
    });
  }

  private void writeIfMissing(Path path, String content) throws IOException {
    if (!Files.exists(path)) {
      Files.createDirectories(path.getParent());
      Files.writeString(path, content);
    }
  }

  // TS: resolveControlDocumentLanguage() — reads book.json → language field
  private String resolveLanguage(String bookId) {
    try {
      Path bookJson = bookDir(bookId).resolve("book.json");
      if (Files.exists(bookJson)) {
        var root = JSON.readTree(Files.readString(bookJson));
        var lang = root.get("language");
        return lang != null ? lang.asText("zh") : "zh";
      }
    } catch (IOException ignored) {/* defaults to zh */}
    return "zh";
  }
}
