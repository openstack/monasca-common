package com.hpcloud.mon.common.model.alarm;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Alarm operator.
 * 
 * @author Jonathan Halterman
 */
public enum AlarmOperator {
  LT("<"), LTE("<="), GT(">"), GTE(">=");

  private final String operatorSymbols;

  private AlarmOperator(String operatorSymbols) {
    this.operatorSymbols = operatorSymbols;
  }

  @JsonCreator
  public static AlarmOperator fromJson(String text) {
    return valueOf(text.toUpperCase());
  }

  public static AlarmOperator reverseOperator(AlarmOperator op) {
    if (op == LT)
      return GT;
    if (op == GT)
      return LT;
    if (op == LTE)
      return GTE;
    return LTE;
  }

  public boolean evaluate(double lhs, double rhs) {
    switch (this) {
      case LT:
        return lhs < rhs;
      case LTE:
        return lhs <= rhs;
      case GT:
        return lhs > rhs;
      case GTE:
        return lhs >= rhs;
      default:
        return false;
    }
  }

  @Override
  public String toString() {
    return operatorSymbols;
  }
}