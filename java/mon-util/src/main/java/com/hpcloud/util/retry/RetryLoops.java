package com.hpcloud.util.retry;

import com.hpcloud.util.Duration;
import com.hpcloud.util.retry.internal.ExponentialBackoffRetry;
import com.hpcloud.util.retry.internal.RetryNTimes;
import com.hpcloud.util.retry.internal.RetryUntilElapsed;

/**
 * Factory methods for creating retry policies.
 * 
 * @author Jonathan Halterman
 */
public final class RetryLoops {
  private RetryLoops() {
  }

  /**
   * @param initialRetryInterval initial interval between retries
   * @param maxRetries max number of times to retry
   * @param exceptions that result in a retry
   */
  public static RetryLoop exponentialBackoff(Duration initialRetryInterval, int maxRetries,
      Class<? extends Throwable>[] exceptions) {
    return new RetryLoop(new ExponentialBackoffRetry(initialRetryInterval, maxRetries), exceptions);
  }

  /**
   * @param n number of retries
   * @param retryInterval interval between retries
   * @param exceptions that result in a retry
   */
  public static RetryLoop nRetries(int n, Duration retryInterval,
      Class<? extends Throwable>[] exceptions) {
    return new RetryLoop(new RetryNTimes(n, retryInterval), exceptions);
  }

  /**
   * @param n number of retries
   * @param retryInterval interval between retries
   */
  public static RetryLoop nRetries(int n) {
    return new RetryLoop(new RetryNTimes(n, Duration.millis(0)),
        (Class<? extends Throwable>[]) null);
  }

  /**
   * @param n number of retries
   * @param retryInterval interval between retries
   */
  public static RetryLoop nRetries(int n, Duration retryInterval) {
    return new RetryLoop(new RetryNTimes(n, retryInterval), (Class<? extends Throwable>[]) null);
  }

  /**
   * @param maxRetryDuration max amount of time allowed for all retries
   * @param retryInterval interval between retries
   * @param exceptions that result in a retry
   */
  public static RetryLoop untilElapsed(Duration maxRetryDuration, Duration retryInterval,
      Class<? extends Throwable>[] exceptions) {
    return new RetryLoop(new RetryUntilElapsed(maxRetryDuration, retryInterval), exceptions);
  }

  /**
   * @param maxRetryDuration max amount of time allowed for all retries
   * @param retryInterval interval between retries
   */
  public static RetryLoop untilElapsed(Duration maxRetryDuration, Duration retryInterval) {
    return new RetryLoop(new RetryUntilElapsed(maxRetryDuration, retryInterval),
        (Class<? extends Throwable>[]) null);
  }
}
