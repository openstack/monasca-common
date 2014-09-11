/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
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
package monasca.common.model.alarm;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Alarm operator.
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
