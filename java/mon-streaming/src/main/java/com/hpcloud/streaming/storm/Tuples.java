package com.hpcloud.streaming.storm;

import backtype.storm.Constants;
import backtype.storm.tuple.Tuple;

/**
 * Utilities for working with Tuples.
 * 
 * @author Jonathan Halterman
 */
public final class Tuples {
  private Tuples() {
  }

  public static boolean isTickTuple(Tuple tuple) {
    return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
        && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
  }
}
