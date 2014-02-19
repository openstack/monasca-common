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

abstract class SleepingRetry implements RetryPolicy {
  private final int n;

  protected SleepingRetry(int n) {
    this.n = n;
  }

  int getN() {
    return n;
  }

  public boolean allowRetry(int retryCount, long elapsedTimeMs) {
    if (retryCount < n) {
      long sleepMillis = getSleepTimeMs(retryCount, elapsedTimeMs);
      if (sleepMillis > 0)
        try {
          Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return false;
        }
      return true;
    }
    return false;
  }

  protected abstract long getSleepTimeMs(int retryCount, long elapsedTimeMs);
}
