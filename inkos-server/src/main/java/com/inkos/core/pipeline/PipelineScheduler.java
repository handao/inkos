package com.inkos.core.pipeline;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Cron-driven scheduler for automated chapter writing and review.
 *
 * TS reference: packages/core/src/pipeline/scheduler.ts
 * Manages periodic execution of writeNextChapter with configurable intervals,
 * book-specific schedules, and concurrent execution guards.
 */
public class PipelineScheduler {

  private final PipelineRunner runner;
  private final ScheduledExecutorService scheduler;
  private final Map<String, ScheduledFuture<?>> activeJobs = new ConcurrentHashMap<>();

  public PipelineScheduler(PipelineRunner runner, ScheduledExecutorService scheduler) {
    this.runner = runner;
    this.scheduler = scheduler;
  }

  /**
   * Schedule a book for automatic chapter writing at a fixed rate.
   * TS: scheduler.ts → scheduleBook() with interval, optional start delay
   */
  public void scheduleBook(String bookId, long intervalMinutes) {
    ScheduledFuture<?> existing = activeJobs.get(bookId);
    if (existing != null && !existing.isCancelled() && !existing.isDone()) {
      existing.cancel(false);
    }

    ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
      () -> {
        try {
          runner.writeNextChapter(bookId).join();
        } catch (Exception e) {
          System.err.println("Scheduled chapter writing failed for book " + bookId + ": " + e.getMessage());
        }
      },
      0, intervalMinutes, TimeUnit.MINUTES
    );

    activeJobs.put(bookId, future);
  }

  /**
   * Cancel scheduling for a specific book.
   * TS: scheduler.ts → unscheduleBook()
   */
  public void unscheduleBook(String bookId) {
    ScheduledFuture<?> future = activeJobs.remove(bookId);
    if (future != null) {
      future.cancel(false);
    }
  }

  /**
   * Check if a book has an active scheduled job.
   */
  public boolean isScheduled(String bookId) {
    ScheduledFuture<?> future = activeJobs.get(bookId);
    return future != null && !future.isCancelled() && !future.isDone();
  }

  /**
   * Cancel all active schedules and shut down the executor gracefully.
   */
  public void shutdown() {
    for (String bookId : Set.copyOf(activeJobs.keySet())) {
      unscheduleBook(bookId);
    }
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
