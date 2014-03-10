package com.hpcloud.streaming.storm;

import backtype.storm.task.TopologyContext;


/**
 * Storm related logging utilities.
 * 
 * @author Jonathan Halterman
 */
public final class Logging {
  private Logging() {
  }

  public static String categoryFor(Class<?> type, TopologyContext ctx) {
    return String.format("%s-%s", type.getName(), ctx.getThisTaskId());
  }
}
