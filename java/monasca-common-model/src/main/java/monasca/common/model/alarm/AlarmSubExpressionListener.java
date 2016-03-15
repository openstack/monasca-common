/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * Copyright 2016 FUJITSU LIMITED
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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import monasca.common.model.metric.MetricDefinition;

/**
 * Complex alarm parser lister for sub expression extraction.
 */
class AlarmSubExpressionListener extends AlarmExpressionBaseListener {

  private final boolean simpleExpression;
  private AggregateFunction function;
  private String namespace;
  private SortedMap<String, String> dimensions = new TreeMap<String, String>();
  private AlarmOperator operator;
  private double threshold;
  private int period = AlarmSubExpression.DEFAULT_PERIOD;
  private int periods = AlarmSubExpression.DEFAULT_PERIODS;
  private List<Object> elements = new ArrayList<Object>();
  private boolean deterministic = AlarmSubExpression.DEFAULT_DETERMINISTIC;

  AlarmSubExpressionListener(boolean simpleExpression) {
    this.simpleExpression = simpleExpression;
  }

  private void saveSubExpression() {
    // not possible to establish if metric is sporadic from expression, so we go with default
    final MetricDefinition metricDefinition = new MetricDefinition(
        namespace,
        dimensions
    );
    final AlarmSubExpression subExpression = new AlarmSubExpression(function,
        metricDefinition,
        operator,
        threshold,
        period,
        periods,
        deterministic
    );
    elements.add(subExpression);

    function = null;
    namespace = null;
    dimensions = new TreeMap<>();
    operator = null;
    threshold = 0;
    period = AlarmSubExpression.DEFAULT_PERIOD;
    periods = AlarmSubExpression.DEFAULT_PERIODS;
    deterministic = AlarmSubExpression.DEFAULT_DETERMINISTIC;
  }

  @Override
  public void exitRelationalExprFwd(AlarmExpressionParser.RelationalExprFwdContext ctx) {
    // This is *right now* basically the same as a min or max function, convert it
    if (operator == AlarmOperator.GT || operator == AlarmOperator.GTE)
      function = AggregateFunction.MAX;
    else
      function = AggregateFunction.MIN;
    saveSubExpression();
  }

  @Override
  public void exitRelationalExprFuncFwd(AlarmExpressionParser.RelationalExprFuncFwdContext ctx) {
    saveSubExpression();
  }

  @Override
  public void enterFunctionType(AlarmExpressionParser.FunctionTypeContext ctx) {
    function = AggregateFunction.valueOf(ctx.getChild(0).getText().toUpperCase());
  }

  @Override
  public void enterNamespace(AlarmExpressionParser.NamespaceContext ctx) {
    namespace = ctx.getChild(0).getText();
  }

  @Override
  public void enterDimension(AlarmExpressionParser.DimensionContext ctx) {
    StringBuilder dimensionName = new StringBuilder();
    dimensionName.append(ctx.getChild(0).getText());
    int i = 1;
    while (!ctx.getChild(i).getText().equals("=")) {
      dimensionName.append(' ');
      dimensionName.append(ctx.getChild(i).getText());
      i++;
    }
    // move past the '=' token
    i++;

    StringBuilder dimensionValue = new StringBuilder();
    dimensionValue.append(ctx.getChild(i).getText());
    i++;
    while (i < ctx.getChildCount()) {
      dimensionValue.append(' ');
      dimensionValue.append(ctx.getChild(i).getText());
      i++;
    }
    if (dimensions.put(dimensionName.toString(), dimensionValue.toString()) != null)
      throw new IllegalArgumentException("More than one value was given for dimension "
          + dimensionName);
  }

  @Override
  public void enterPeriod(AlarmExpressionParser.PeriodContext ctx) {
    period = Integer.valueOf(ctx.getChild(0).getText());
  }

  @Override
  public void enterRepeat(AlarmExpressionParser.RepeatContext ctx) {
    periods = Integer.valueOf(ctx.getChild(0).getText());
  }

  @Override
  public void enterLt(AlarmExpressionParser.LtContext ctx) {
    assertSimpleExpression();
    operator = AlarmOperator.LT;
  }

  @Override
  public void enterLte(AlarmExpressionParser.LteContext ctx) {
    assertSimpleExpression();
    operator = AlarmOperator.LTE;
  }

  @Override
  public void enterGt(AlarmExpressionParser.GtContext ctx) {
    assertSimpleExpression();
    operator = AlarmOperator.GT;
  }

  @Override
  public void enterGte(AlarmExpressionParser.GteContext ctx) {
    assertSimpleExpression();
    operator = AlarmOperator.GTE;
  }

  @Override
  public void exitLiteral(AlarmExpressionParser.LiteralContext ctx) {
    threshold = Double.valueOf(ctx.getChild(0).getText());
  }

  @Override
  public void exitOrExpr(AlarmExpressionParser.OrExprContext ctx) {
    elements.add(BooleanOperator.OR);
  }

  @Override
  public void exitAndExpr(AlarmExpressionParser.AndExprContext ctx) {
    elements.add(BooleanOperator.AND);
  }

  @Override
  public void enterDeterministic(final AlarmExpressionParser.DeterministicContext ctx) {
    this.deterministic = true;
  }

  /**
   * Returns the operator and operand elements of the expression in postfix order. Elements will be
   * of types AlarmSubExpression and BooleanOperator.
   */
  List<Object> getElements() {
    return elements;
  }

  private void assertSimpleExpression() {
    if (simpleExpression && !elements.isEmpty())
      throw new IllegalArgumentException("Expected a simple expression");
  }
}
