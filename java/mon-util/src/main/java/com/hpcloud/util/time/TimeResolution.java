package com.hpcloud.util.time;

/**
 * Time resolution.
 * 
 * @author Jonathan Halterman
 */
public enum TimeResolution {
  ABSOLUTE {
    @Override
    public long adjust(long timestamp) {
      return timestamp;
    }
  },
  SECONDS {
    @Override
    public long adjust(long timestamp) {
      return Times.roundDownToNearestSecond(timestamp);
    }
  },
  MINUTES {
    @Override
    public long adjust(long timestamp) {
      return Times.roundDownToNearestMinute(timestamp);
    }
  };

  /**
   * Returns the {@code timestamp} adjusted for the resolution.
   */
  public abstract long adjust(long timestamp);
}