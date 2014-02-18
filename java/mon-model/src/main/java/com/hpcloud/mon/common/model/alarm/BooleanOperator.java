package com.hpcloud.mon.common.model.alarm;

/**
 * Boolean operator.
 * 
 * @author Todd Walk
 * @author Jonathan Halterman
 */
public enum BooleanOperator {
  AND, OR;

  public boolean evaluate(boolean lhs, boolean rhs) {
    if (AND.equals(this))
      return lhs && rhs;
    return lhs || rhs;
  }
}
