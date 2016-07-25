/*
 * (C) Copyright 2014, 2016 Hewlett Packard Enterprise Development LP
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
package monasca.common.util.stats;

/**
 * Statistic.
 */
public interface Statistic {
  /** Adds the {@code value} to the statistic. */
  void addValue(double value, double timestamp);

  /** Returns true if the statistic has been initialized with a value, else false. */
  boolean isInitialized();

  /** Resets the value of the statistic. */
  void reset();

  /** Returns the value of the statistic. */
  double value();
}
