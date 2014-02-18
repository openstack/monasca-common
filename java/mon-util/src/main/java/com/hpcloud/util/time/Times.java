package com.hpcloud.util.time;

/**
 * Utilities for working with times.
 * 
 * @author Jonathan Halterman
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
