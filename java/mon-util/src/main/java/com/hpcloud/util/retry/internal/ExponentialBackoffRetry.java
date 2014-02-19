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

import java.util.Random;

import com.hpcloud.util.Duration;

/**
 * Retry policy that retries a set number of times with increasing sleep time between retries
 */
public class ExponentialBackoffRetry extends SleepingRetry {
  private final Random random = new Random();
  private final long baseSleepTimeMs;

  public ExponentialBackoffRetry(Duration initialRetryInterval, int maxRetries) {
    super(maxRetries);
    this.baseSleepTimeMs = initialRetryInterval.timeUnit.toMillis(initialRetryInterval.length);
  }

  long getBaseSleepTimeMs() {
    return baseSleepTimeMs;
  }

  @Override
  protected long getSleepTimeMs(int retryCount, long elapsedTimeMs) {
    // Copied from Hadoop's RetryPolicies.java
    return baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)));
  }
}
