/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * Copyright 2016 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.model.alarm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import monasca.common.model.metric.MetricDefinition;

@Test
public class AlarmSubExpressionTest {
  public void shouldParseExpression() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1, instance_id=5}, 1) > 5 times 3");

    AlarmSubExpression expected = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.GT, 5, 1, 3);

    assertEquals(expr, expected);
  }

  public void shouldParseExpressionNoType() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3");

    AlarmSubExpression expected = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.GT, 5, 1, 3);

    assertEquals(expr, expected);
  }

  public void shouldParseExpressionWithoutFunctionGT() {
    AlarmSubExpression expr = AlarmSubExpression.of("hpcs.compute{metric_name=cpu, device=1, instance_id=5} > 5");

    AlarmSubExpression expected = new AlarmSubExpression(AggregateFunction.MAX,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.GT, 5, 60, 1);

    assertEquals(expr, expected);
  }

  public void shouldParseExpressionWithoutFunctionLT() {
    AlarmSubExpression expr = AlarmSubExpression.of("hpcs.compute{metric_name=cpu, device=1, instance_id=5} < 5");

    AlarmSubExpression expected = new AlarmSubExpression(AggregateFunction.MIN,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.LT, 5, 60, 1);

    assertEquals(expr, expected);
  }

  public void shouldEvaluateExpression() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1, instance_id=5}, 1) > 5 times 3");

    assertTrue(expr.evaluate(6));
    assertFalse(expr.evaluate(4));
  }

  public void shouldParseExpressionWithoutSubject() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, instance_id=5}, 1) > 5 times 3");
    assertEquals(expr,
        new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
            ImmutableMap.<String, String>builder()
                .put("instance_id", "5")
                .put("metric_name", "cpu")
                .build()), AlarmOperator.GT, 5, 1, 3));
  }

  public void shouldParseExpressionCaseInsensitiveFunc() {
    AlarmSubExpression expr = AlarmSubExpression.of("AvG(hpcs.compute{metric_name=cpu, instance_id=5}, 1) > 5 times 3");
    assertEquals(expr,
        new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
            ImmutableMap.<String, String>builder()
                .put("instance_id", "5")
                .put("metric_name", "cpu")
                .build()), AlarmOperator.GT, 5, 1, 3));
  }

  public void shouldParseExpressionCaseInsensitiveOp() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, instance_id=5}, 1) Gt 5 times 3");
    assertEquals(expr,
        new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
            ImmutableMap.<String, String>builder()
                .put("instance_id", "5")
                .put("metric_name", "cpu")
                .build()), AlarmOperator.GT, 5, 1, 3));
  }

  public void shouldParseExpressionKeywordNamespace() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(count{metric_name=cpu, instance_id=5}, 1) > 5 times 3");
    assertEquals(expr, new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("count",
        ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .build()), AlarmOperator.GT, 5, 1, 3));
  }

  public void shouldParseExpressionKeywordMetricType() {
    AlarmSubExpression expr = AlarmSubExpression.of("avg(hpcs.compute{metric_name=count, instance_id=5}, 1) > 5 times 3");
    assertEquals(expr,
        new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
            ImmutableMap.<String, String>builder()
                .put("instance_id", "5")
                .put("metric_name", "count")
                .build()), AlarmOperator.GT, 5, 1, 3));
  }

  public void shouldDefaultPeriodAndPeriods() {
    AlarmExpression expr = new AlarmExpression(
        "avg(hpcs.compute{metric_name=cpu, device=1, instance_id=2}) > 5");
    AlarmSubExpression alarm = expr.getSubExpressions().get(0);
    assertEquals(alarm.getPeriod(), 60);
    assertEquals(alarm.getPeriods(), 1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldThrowOnDuplicateDimensions() {
    AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1, instance_id=5, instance_uuid=4, instance_id=4}) > 5");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldThrowOnCompoundExpressions() {
    AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1, instance_id=5}) > 5 or avg(hpcs.compute{metric_name=mem, instance_id=5}) > 5");
  }

  public void shouldGetExpression() {
    assertEquals(AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1}) > 5")
        .getExpression(), "avg(hpcs.compute{device=1, metric_name=cpu}) > 5.0");
    assertEquals(
        AlarmSubExpression.of("avg(hpcs.compute{metric_name=cpu, device=1}, 45) > 5 times 4")
            .getExpression(), "avg(hpcs.compute{device=1, metric_name=cpu}, 45) > 5.0 times 4");
  }

  public void toStringAndBack() {
    AlarmExpression expr = new AlarmExpression("elasticsearch.store.size>21474836480");
    AlarmSubExpression subExpr1 = expr.getSubExpressions().get(0);
    AlarmSubExpression subExpr2 = AlarmSubExpression.of(subExpr1.getExpression());
    assertEquals(subExpr2, subExpr1);
  }

  public void shouldAllowDecimalThresholds() {
    assertEquals(AlarmSubExpression.of("avg(hpcs.compute) > 2.375").getThreshold(), 2.375);
  }

  public void shouldBeNonDeterministicByDefault() {
    assertFalse(AlarmSubExpression.of("count(log.error{}) > 1.0").isDeterministic());
  }

  public void shouldBeDeterministicIfSet() {
    assertTrue(AlarmSubExpression.of("count(log.error{}, deterministic) > 1.0").isDeterministic());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldFailWithMalformedDeterministicKeyword() {
    AlarmSubExpression.of("count(log.error{}, deterministici) > 1.0");
  }

}
