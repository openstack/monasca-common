package com.hpcloud.mon.common.model.alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsualtes a boolean expression consisting of an operator and two or more operands.
 * 
 * @author Jonathan Halterman
 */
public class BooleanExpression {
  public final BooleanOperator operator;
  public final List<Object> operands;

  public BooleanExpression(BooleanOperator operator, Object left, Object right) {
    this.operator = operator;
    operands = new ArrayList<>();
    operands.add(left);
    operands.add(right);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BooleanExpression other = (BooleanExpression) obj;
    if (operands == null) {
      if (other.operands != null)
        return false;
    } else if (!operands.equals(other.operands))
      return false;
    if (operator != other.operator)
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((operands == null) ? 0 : operands.hashCode());
    result = prime * result + ((operator == null) ? 0 : operator.hashCode());
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder().append('(').append(operands.get(0));
    for (int i = 1; i < operands.size(); i++)
      sb.append(' ').append(operator.name()).append(' ').append(operands.get(i));
    return sb.append(')').toString();
  }
}
