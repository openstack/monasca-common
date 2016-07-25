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
 * Statistic implementations.
 */
public final class Statistics {
  public static abstract class AbstractStatistic implements Statistic {
    protected boolean initialized;
    protected double value;

    @Override
    public boolean isInitialized() {
      return initialized;
    }

    @Override
    public void reset() {
      initialized = false;
      value = 0;
    }

    @Override
    public String toString() {
      return Double.valueOf(value()).toString();
    }

    @Override
    public double value() {
      return !initialized ? Double.NaN : value;
    }
  }

  public static class Average extends Sum {
    protected int count;

    @Override
    public void addValue(double value, double timestamp) {
      super.addValue(value, timestamp);
      this.count++;
    }

    @Override
    public void reset() {
      super.reset();
      count = 0;
    }

    @Override
    public double value() {
      return !initialized ? Double.NaN : count == 0 ? 0 : value / count;
    }
  }

  public static class Count extends AbstractStatistic {
    @Override
    public void addValue(double value, double timestamp) {
      initialized = true;
      this.value++;
    }
  }

  public static class Max extends AbstractStatistic {
    @Override
    public void addValue(double value, double timestamp) {
      if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value > this.value)
        this.value = value;
    }
  }

  public static class Min extends AbstractStatistic {
    @Override
    public void addValue(double value, double timestamp) {
      if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value < this.value)
        this.value = value;
    }
  }

  public static class Sum extends AbstractStatistic {
    @Override
    public void addValue(double value, double timestamp) {
      initialized = true;
      this.value += value;
    }
  }

  public static class Last extends AbstractStatistic {
    protected double lastTimestamp;

    @Override
    public void addValue(double value, double timestamp) {
      initialized = true;
      // Ensure older measurements don't change value
      if (timestamp > this.lastTimestamp) {
        this.value = value;
        this.lastTimestamp = timestamp;
      }
    }
  }

  private Statistics() {
  }
}
