package com.hpcloud.util.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpcloud.util.Exceptions;
import com.hpcloud.util.retry.internal.RetryPolicy;

/**
 * <p>
 * If an exception occurs during the operation, the RetryLoop will process it, check with the
 * current retry policy and either attempt to retry or re-throw the exception.
 * </p>
 * 
 * Usage:<br/>
 * <code><pre>
 * RetryLoop loop = ...
 * while (loop.shouldContinue()) {
 *   try {
 *     doSomething();
 *     loop.complete();
 *   } catch (Exception e) {
 *     loop.recordFailure(e);
 *   }
 * }
 * </pre></code>
 */
public class RetryLoop {
  private static final Logger LOG = LoggerFactory.getLogger(RetryLoop.class);
  private final Class<? extends Throwable>[] retryableFailures;
  private final long startTimeMs = System.currentTimeMillis();
  private final RetryPolicy retryPolicy;

  private RuntimeException lastFailure;
  private int attemptCount;
  private boolean completed;

  /**
   * Creates a new retry loop
   * 
   * @param retryPolicy that determines whether a retry is allowed
   * @param retryableFailures failures that can result in a retry
   */
  @SafeVarargs
  RetryLoop(RetryPolicy retryPolicy, Class<? extends Throwable>... retryableFailures) {
    this.retryPolicy = retryPolicy;
    this.retryableFailures = retryableFailures;
  }

  /** Completes the retry loop. */
  public void complete() {
    completed = true;
  }

  /** Returns true if the retry loop is complete, else false. */
  public boolean isComplete() {
    return completed;
  }

  /** Returns true if {@code failure} is retry-able, else false. */
  public boolean isRetryableFailure(Throwable failure) {
    if (retryableFailures == null)
      return true;
    for (Class<? extends Throwable> retryableFailure : retryableFailures)
      if (retryableFailure.isAssignableFrom(failure.getClass()))
        return true;
    return false;
  }

  /**
   * Records the {@code failure}. If {@code failure} is not retry-able, it is re-thrown.
   * 
   * @throws RuntimeException if {@code failure} is not retry-able
   */
  public void recordFailure(Throwable failure) {
    lastFailure = failure instanceof RuntimeException ? (RuntimeException) failure
        : Exceptions.uncheck(failure);
    if (!isRetryableFailure(failure))
      throw lastFailure;
  }

  /**
   * Returns true if the retry loop has not been completed and the retry policy has not been
   * exceeded. Else if a failure was recorded (@see {@link #recordFailure(Throwable)}), it is
   * re-thrown. Else false is returned.
   * 
   * @throws RuntimeException if the retry policy has been exceeded and a failure was recorded.
   */
  public boolean shouldContinue() {
    if (!completed
        && (++attemptCount == 1 || retryPolicy.allowRetry(attemptCount - 2,
            System.currentTimeMillis() - startTimeMs))) {
      if (attemptCount > 1)
        LOG.debug("Retrying operation");
      lastFailure = null;
      return true;
    }

    if (lastFailure != null)
      throw lastFailure;
    return false;
  }
}
