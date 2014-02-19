/*
 *
 *  Copyright 2011 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.hpcloud.util.retry.internal;

import com.hpcloud.util.Duration;

/**
 * A retry policy that retries until a given amount of time elapses
 */
public class RetryUntilElapsed extends SleepingRetry {
  private final long maxElapsedTimeMs;
  private final long sleepMsBetweenRetries;

  public RetryUntilElapsed(Duration maxRetryDuration, Duration retryInterval) {
    super(Integer.MAX_VALUE);
    this.maxElapsedTimeMs = maxRetryDuration.timeUnit.toMillis(maxRetryDuration.length);
    this.sleepMsBetweenRetries = retryInterval.timeUnit.toMillis(retryInterval.length);
  }

  @Override
  public boolean allowRetry(int retryCount, long elapsedTimeMs) {
    return super.allowRetry(retryCount, elapsedTimeMs) && (elapsedTimeMs < maxElapsedTimeMs);
  }

  @Override
  protected long getSleepTimeMs(int retryCount, long elapsedTimeMs) {
    return sleepMsBetweenRetries;
  }
}
