package com.hpcloud.mon.common.model.alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hpcloud.mon.common.model.alarm.AlarmExpressionLexer;
import com.hpcloud.mon.common.model.alarm.AlarmExpressionParser;
import com.hpcloud.util.Stack;

/**
 * Alarm expression value object.
 * 
 * @author Todd Walk
 * @author Jonathan Halterman
 */
public class AlarmExpression {
  private final String expression;
  /** Postfix list of expression elements. */
  private final List<Object> elements;
  private volatile List<AlarmSubExpression> subExpressions;

  /**
   * Creates an AlarmExpression for the {@code expression} string.
   * 
   * @throws IllegalArgumentException if the {@code expression} is invalid
   */
  public AlarmExpression(String expression) {
    this.expression = expression;
    AlarmExpressionParser parser = new AlarmExpressionParser(new CommonTokenStream(
        new AlarmExpressionLexer(new ANTLRInputStream(expression))));
    parser.removeErrorListeners();
    parser.addErrorListener(new AlarmExpressionErrorListener());
    parser.setBuildParseTree(true);
    ParserRuleContext tree = parser.start();
    AlarmSubExpressionListener listener = new AlarmSubExpressionListener(false);
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(listener, tree);
    elements = listener.getElements();
  }

  /**
   * Creates an AlarmExpression for the {@code expression} string.
   * 
   * @throws IllegalArgumentException if the {@code expression} is invalid
   */
  @JsonCreator
  public static AlarmExpression of(String expression) {
    return new AlarmExpression(expression);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AlarmExpression other = (AlarmExpression) obj;
    if (elements == null) {
      if (other.elements != null)
        return false;
    } else if (!elements.equals(other.elements))
      return false;
    return true;
  }

  /**
   * Evaluates the {@code subExpressionValues} against the expression, returning true if the values
   * evaluate to true for the expression, else false.
   * 
   * @throws IllegalArgumentException if any of the expected sub-expressions cannot be found in
   *           {@code subExpressionValues}
   */
  public boolean evaluate(Map<AlarmSubExpression, Boolean> subExpressionValues) {
    Stack<Object> stack = new Stack<Object>();

    for (Object element : elements) {
      if (element instanceof AlarmSubExpression) {
        Boolean value = subExpressionValues.get(element);
        if (value == null)
          throw new IllegalArgumentException("Expected sub-expression was not found for " + element);
        stack.push(value);
      } else {
        BooleanOperator operator = (BooleanOperator) element;
        Boolean operandA = (Boolean) stack.pop();
        Boolean operandB = (Boolean) stack.pop();
        stack.push(operator.evaluate(operandA, operandB));
      }
    }

    return (Boolean) stack.pop();
  }

  /**
   * Returns the alarm's expression.
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Returns the sub expressions for the expression in the order that they appear.
   */
  public List<AlarmSubExpression> getSubExpressions() {
    if (subExpressions != null)
      return subExpressions;
    List<AlarmSubExpression> subExpressions = new ArrayList<AlarmSubExpression>();
    for (Object element : elements)
      if (element instanceof AlarmSubExpression)
        subExpressions.add((AlarmSubExpression) element);
    this.subExpressions = subExpressions;
    return subExpressions;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elements == null) ? 0 : elements.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format("AlarmExpression [elements=%s]", elements);
  }
}
