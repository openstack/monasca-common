package com.hpcloud.mon.common.model.alarm;

import java.io.Serializable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hpcloud.mon.common.model.alarm.AlarmExpressionLexer;
import com.hpcloud.mon.common.model.alarm.AlarmExpressionParser;
import com.hpcloud.mon.common.model.metric.MetricDefinition;

/**
 * Alarm sub expression value object.
 * 
 * @author Todd Walk
 * @author Jonathan Halterman
 */
public class AlarmSubExpression implements Serializable {
  private static final long serialVersionUID = -7458129503846747592L;
  public static final int DEFAULT_PERIOD = 60;
  public static final int DEFAULT_PERIODS = 1;

  private AggregateFunction function;
  private MetricDefinition metricDefinition;
  private AlarmOperator operator;
  private double threshold;
  private int period;
  private int periods;

  public AlarmSubExpression(AggregateFunction function, MetricDefinition metricDefinition,
      AlarmOperator operator, double threshold, int period, int periods) {
    this.function = function;
    this.metricDefinition = metricDefinition;
    this.operator = operator;
    this.threshold = threshold;
    this.period = period;
    this.periods = periods;
  }

  AlarmSubExpression() {
  }

  /**
   * Returns an AlarmSubExpression for the {@code expression} string.
   * 
   * @throws IllegalArgumentException if the {@code expression} is invalid
   */
  @JsonCreator
  public static AlarmSubExpression of(String expression) {
    AlarmExpressionParser parser = new AlarmExpressionParser(new CommonTokenStream(
        new AlarmExpressionLexer(new ANTLRInputStream(expression))));
    parser.removeErrorListeners();
    parser.addErrorListener(new AlarmExpressionErrorListener());
    parser.setBuildParseTree(true);
    ParserRuleContext tree = parser.start();
    AlarmSubExpressionListener listener = new AlarmSubExpressionListener(true);
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(listener, tree);
    return (AlarmSubExpression) listener.getElements().get(0);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AlarmSubExpression other = (AlarmSubExpression) obj;
    if (function != other.function)
      return false;
    if (metricDefinition == null) {
      if (other.metricDefinition != null)
        return false;
    } else if (!metricDefinition.equals(other.metricDefinition))
      return false;
    if (operator != other.operator)
      return false;
    if (period != other.period)
      return false;
    if (periods != other.periods)
      return false;
    if (Double.doubleToLongBits(threshold) != Double.doubleToLongBits(other.threshold))
      return false;
    return true;
  }

  /**
   * Evaluates the {@code value} against the threshold and returns the result.
   */
  public boolean evaluate(double value) {
    return operator.evaluate(value, threshold);
  }

  /**
   * Returns the sub-alarm's expression.
   */
  public String getExpression() {
    StringBuilder sb = new StringBuilder();
    sb.append(function).append('(').append(metricDefinition.toExpression());
    if (period != 60)
      sb.append(", ").append(period);
    sb.append(") ").append(operator).append(' ').append(threshold);
    if (periods != 1)
      sb.append(" times ").append(periods);
    return sb.toString();
  }

  public AggregateFunction getFunction() {
    return function;
  }

  public MetricDefinition getMetricDefinition() {
    return metricDefinition;
  }

  public AlarmOperator getOperator() {
    return operator;
  }

  public int getPeriod() {
    return period;
  }

  public int getPeriods() {
    return periods;
  }

  public double getThreshold() {
    return threshold;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((function == null) ? 0 : function.hashCode());
    result = prime * result + ((metricDefinition == null) ? 0 : metricDefinition.hashCode());
    result = prime * result + ((operator == null) ? 0 : operator.hashCode());
    result = prime * result + period;
    result = prime * result + periods;
    long temp;
    temp = Double.doubleToLongBits(threshold);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public void setFunction(AggregateFunction function) {
    this.function = function;
  }

  public void setMetricDefinition(MetricDefinition metricDefinition) {
    this.metricDefinition = metricDefinition;
  }

  public void setOperator(AlarmOperator operator) {
    this.operator = operator;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public void setPeriods(int periods) {
    this.periods = periods;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  @Override
  public String toString() {
    return getExpression();
  }
}
