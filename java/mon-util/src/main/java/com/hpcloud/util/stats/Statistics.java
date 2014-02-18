package com.hpcloud.util.stats;

/**
 * Statistic implementations.
 * 
 * @author Jonathan Halterman
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
    public void addValue(double value) {
      super.addValue(value);
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
    public void addValue(double value) {
      initialized = true;
      this.value++;
    }
  }

  public static class Max extends AbstractStatistic {
    @Override
    public void addValue(double value) {
      if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value > this.value)
        this.value = value;
    }
  }

  public static class Min extends AbstractStatistic {
    @Override
    public void addValue(double value) {
      if (!initialized) {
        initialized = true;
        this.value = value;
      } else if (value < this.value)
        this.value = value;
    }
  }

  public static class Sum extends AbstractStatistic {
    @Override
    public void addValue(double value) {
      initialized = true;
      this.value += value;
    }
  }

  private Statistics() {
  }
}
