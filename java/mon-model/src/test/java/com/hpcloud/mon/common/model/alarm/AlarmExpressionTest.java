package com.hpcloud.mon.common.model.alarm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.hpcloud.mon.common.model.alarm.AggregateFunction;
import com.hpcloud.mon.common.model.alarm.AlarmExpression;
import com.hpcloud.mon.common.model.alarm.AlarmOperator;
import com.hpcloud.mon.common.model.alarm.AlarmSubExpression;
import com.hpcloud.mon.common.model.metric.MetricDefinition;

/**
 * @author Jonathan Halterman
 */
@Test
public class AlarmExpressionTest {
  public void shouldParseExpression() {
    AlarmExpression expr = new AlarmExpression(
        "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
    List<AlarmSubExpression> alarms = expr.getSubExpressions();

    AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.GT, 5, 1, 3);
    AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("flavor_id", "3")
            .put("metric_name", "mem")
            .build()), AlarmOperator.LT, 4, 2, 3);

    assertEquals(alarms.get(0), expected1);
    assertEquals(alarms.get(1), expected2);
  }

  public void shouldParseExpressionWithoutType() {
    AlarmExpression expr = new AlarmExpression(
        "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
    List<AlarmSubExpression> alarms = expr.getSubExpressions();

    AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5")
            .put("metric_name", "cpu")
            .put("device", "1")
            .build()), AlarmOperator.GT, 5, 1, 3);
    AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
        new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("flavor_id", "3")
            .put("metric_name", "mem")
            .build()), AlarmOperator.LT, 4, 2, 3);

    assertEquals(alarms.get(0), expected1);
    assertEquals(alarms.get(1), expected2);
  }

  public void shouldEvaluateExpression() {
    AlarmExpression expr = new AlarmExpression(
        "sum(hpcs.compute{instance_id=5,metric_name=disk}, 1) > 33 or (avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3)");
    List<AlarmSubExpression> alarms = expr.getSubExpressions();

    AlarmSubExpression alarm1 = alarms.get(0);
    AlarmSubExpression alarm2 = alarms.get(1);
    AlarmSubExpression alarm3 = alarms.get(2);

    assertTrue(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
        .put(alarm1, true)
        .put(alarm2, false)
        .put(alarm3, false)
        .build()));

    assertTrue(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
        .put(alarm1, false)
        .put(alarm2, true)
        .put(alarm3, true)
        .build()));

    assertFalse(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
        .put(alarm1, false)
        .put(alarm2, false)
        .put(alarm3, true)
        .build()));

    assertFalse(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
        .put(alarm1, false)
        .put(alarm2, true)
        .put(alarm3, false)
        .build()));
  }

  public void shouldDefaultPeriodAndPeriods() {
    AlarmExpression expr = new AlarmExpression("avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}) > 5");
    AlarmSubExpression alarm = expr.getSubExpressions().get(0);
    assertEquals(alarm.getPeriod(), 60);
    assertEquals(alarm.getPeriods(), 1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldThrowOnEvaluateInvalidSubExpressions() {
    AlarmExpression expr = new AlarmExpression(
        "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=2}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
    expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
        .put(
            new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
                ImmutableMap.<String, String>builder()
                    .put("flavor_id", "3")
                    .put("metric_name", "mem")
                    .build()), AlarmOperator.LT, 4, 2, 3), true)
        .build());
  }

  @Test(enabled = false)
  public void testExpressionEquality() {
    AlarmExpression expr1 = new AlarmExpression(
        "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
    AlarmExpression expr2 = new AlarmExpression(
        "avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) gt 3  times 3 && avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 3");
    assertEquals(expr1, expr2);

    AlarmExpression expr3 = new AlarmExpression(
        "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 444 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
    assertNotEquals(expr1, expr3);
  }
}
