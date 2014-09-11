/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.util.time;

/**
 * Utilities for working with times.
 */
public final class Times {
  private Times() {
  }

  /**
   * Returns a timestamp in seconds for the given {@code seconds} which is rounded down to the
   * nearest minute.
   */
  public static long roundDownToNearestMinute(long seconds) {
    return seconds / 60 * 60;
  }

  /**
   * Returns a timestamp in milliseconds for the given {@code milliseconds} which is rounded down to
   * the nearest minute.
   */
  public static long roundDownToNearestSecond(long milliseconds) {
    return milliseconds / 1000 * 1000;
  }
}
