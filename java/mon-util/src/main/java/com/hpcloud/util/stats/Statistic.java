package com.hpcloud.util.stats;

/**
 * Statistic.
 * 
 * @author Jonathan Halterman
 */
public interface Statistic {
  /** Adds the {@code value} to the statistic. */
  void addValue(double value);

  /** Returns true if the statistic has been initialized with a value, else false. */
  boolean isInitialized();

  /** Resets the value of the statistic. */
  void reset();

  /** Returns the value of the statistic. */
  double value();
}
