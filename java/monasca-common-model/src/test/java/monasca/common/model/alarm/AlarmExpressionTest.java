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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import monasca.common.model.metric.MetricDefinition;

@Test
public class AlarmExpressionTest {
  private final String restrictedChars = "(){}&|<>=\",";

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

    public void shouldParseString() {


        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1, url=\"https://www.google.com/?startpage=3&happygoing\"}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem, specialchars=\"!@#$%^&*()~<>{}[],.\"}, 2) < 4 times 3");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("url", "\"https://www.google.com/?startpage=3&happygoing\"")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("specialchars", "\"!@#$%^&*()~<>{}[],.\"")
                        .build()), AlarmOperator.LT, 4, 2, 3);

        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
    }

    public void shouldParseComplexWithoutQuotes() {


        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1, url=https%3A%2F%2Fwww.google.com%2F%3Fstartpage%3D3%26happygoing}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem, specialchars=a!@#/\\$%^*~}, 2) < 4 times 3");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();


        AlarmExpression containsDirectories = new AlarmExpression("avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1,global=$_globalVariable,special=__useSparingly,dos=\\system32\\, windows=C:\\system32\\}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem,$globalVariable=global,__useSparingly=special,unix=/opt/vertica/bin/}, 2) < 4 times 3");
        List<AlarmSubExpression> alarmsContainsDirectories = containsDirectories.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("url", "https%3A%2F%2Fwww.google.com%2F%3Fstartpage%3D3%26happygoing")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("specialchars", "a!@#/\\$%^*~")
                        .build()), AlarmOperator.LT, 4, 2, 3);

        AlarmSubExpression expected3 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("device", "1")
                        .put("global", "$_globalVariable")
                        .put("special", "__useSparingly")
                        .put("dos", "\\system32\\")
                        .put("windows", "C:\\system32\\")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected4 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("$globalVariable", "global")
                        .put("__useSparingly", "special")
                        .put("unix", "/opt/vertica/bin/")
                        .build()), AlarmOperator.LT, 4, 2, 3);
        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
        assertEquals(alarmsContainsDirectories.get(0), expected3);
        assertEquals(alarmsContainsDirectories.get(1), expected4);
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
        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}) > 5");
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
                              new AlarmSubExpression(AggregateFunction.AVG,
                                                     new MetricDefinition("hpcs.compute",
                                                                          ImmutableMap
                                                                              .<String, String>builder()
                                                                              .put("flavor_id", "3")
                                                                              .put("metric_name",
                                                                                   "mem")
                                                                              .build()),
                                                     AlarmOperator.LT, 4, 2, 3), true)
                          .build());
    }

    public void shouldGetAlarmExpressionTree() {
        Object expr = AlarmExpression.of(
                "(avg(foo) > 1 and avg(bar) < 2 and avg(baz) > 3) or (avg(foo) > 4 and avg(bar) < 5 and avg(baz) > 6)")
                .getExpressionTree();
        assertEquals(
                expr.toString(),
                "((avg(foo) > 1.0 AND avg(bar) < 2.0 AND avg(baz) > 3.0) OR (avg(foo) > 4.0 AND avg(bar) < 5.0 AND avg(baz) > 6.0))");

        expr = AlarmExpression.of(
                "(avg(foo) > 1 and (avg(bar) < 2 or avg(baz) > 3)) and (avg(foo) > 4 or avg(bar) < 5 or avg(baz) > 6)")
                .getExpressionTree();
        assertEquals(
                expr.toString(),
                "(avg(foo) > 1.0 AND (avg(bar) < 2.0 OR avg(baz) > 3.0) AND (avg(foo) > 4.0 OR avg(bar) < 5.0 OR avg(baz) > 6.0))");
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

    public void shouldParseNamesWithUnicode() {
      AlarmExpression expr1 = new AlarmExpression(
          "公{此=该,metric_name=mem} > 4"
      );
      AlarmSubExpression alarm1 = expr1.getSubExpressions().get(0);
      MetricDefinition expected1 = new MetricDefinition("公",
                                                        ImmutableMap.<String, String>builder()
                                                            .put("此", "该")
                                                            .put("metric_name", "mem")
                                                            .build());
      assertEquals(alarm1.getMetricDefinition(), expected1);
    }

    public void shouldParseDimensionsWithSpaces() {
      AlarmExpression[] expr_list = {
          new AlarmExpression("test_metric{this_is_a_test=this is a test} > 10"),
          new AlarmExpression("test_metric{this is also a test = this_is_also_a_test} > 10")
      };
      MetricDefinition[] expected_list = {
          new MetricDefinition("test_metric", ImmutableMap.<String,String>builder()
              .put("this_is_a_test", "this is a test")
              .build()),
          new MetricDefinition("test_metric", ImmutableMap.<String,String>builder()
              .put("this is also a test", "this_is_also_a_test")
              .build())
      };

      for(int i = 0; i < expr_list.length; i++) {
        AlarmSubExpression expr = expr_list[i].getSubExpressions().get(0);
        assertEquals(expr.getMetricDefinition(), expected_list[i]);
      }
    }

    public void shouldFailWithRestrictedChars() {
      String[] expressions = {"%cmetric{foo=bar,metric_name=mem} > 4",
                              "me%ctric{foo=bar,metric_name=mem} > 4",
                              "metric%c{foo=bar,metric_name=mem} > 4",
                              "metric{%cfoo=bar,metric_name=mem} > 4",
                              "metric{f%coo=bar,metric_name=mem} > 4",
                              "metric{foo%c=bar,metric_name=mem} > 4",
                              "metric{foo=%cbar,metric_name=mem} > 4",
                              "metric{foo=b%car,metric_name=mem} > 4",
                              "metric{foo=bar%c,metric_name=mem} > 4"};
      for (int i = 0; i < expressions.length; i++) {
        for (int j = 0; j < restrictedChars.length(); j++) {
          String
              exprStr =
              String.format(expressions[i], restrictedChars.charAt(j));
          try {
            AlarmExpression expr = new AlarmExpression(exprStr);
            fail(String.format("Successfully parsed invalid expression: %s", exprStr));
          } catch (Exception ex) {
            //System.out.println(ex);
          }
        }
      }
    }

    public void shouldParseSpacings() {
      AlarmExpression expr = new AlarmExpression("avg ( metric { foo = bar , metric_name = mem } ) > 4"
                                                 + " or avg(metric{foo=bar,metric_name=mem})>4"
                                                 + " or avg( metric{ foo= bar, metric_name= mem} )> 4"
                                                 + " or avg (metric {foo =bar ,metric_name =mem }) >4");
      List<AlarmSubExpression> subExpressions = expr.getSubExpressions();
      for(int i = 1; i < subExpressions.size(); i++){
        assertEquals(subExpressions.get(0),subExpressions.get(i));
      }
    }

    public void shouldParseComplexExpression() {
      AlarmExpression expr = new AlarmExpression("max(-_.千幸福的笑脸{घोड़ा=馬,"
                                                 + "dn2=dv2,千幸福的笑脸घ=千幸福的笑脸घ}) gte 100 "
                                                 + "times 3 && "
                                                 + "(min(ເຮືອນ{dn3=dv3,家=дом}) < 10 or sum(biz{dn5=dv5}) >9 and "
                                                 + "count(fizzle) lt 0 or count(baz) > 1)");
    }

  public void shouldParseDeterministicExpression() {
    final Map<String, String> dimensions = Maps.newHashMap();
    final ArrayList<AlarmExpression> expressions = Lists.newArrayList(
        new AlarmExpression("count(log.error{},deterministic,20) > 5")
    );
    final MetricDefinition metricDefinition = new MetricDefinition("log.error", dimensions);

    final AlarmSubExpression logErrorExpr = new AlarmSubExpression(
        AggregateFunction.COUNT,
        metricDefinition,
        AlarmOperator.GT,
        5,
        20,
        1,
        true // each expression is deterministic
    );

    for (final AlarmExpression expr : expressions) {
      final List<AlarmSubExpression> subExpressions = expr.getSubExpressions();

      assertTrue(expr.isDeterministic());  // each expression is deterministic
      assertEquals(1, subExpressions.size());
      assertEquals(subExpressions.get(0), logErrorExpr);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldNotParseInvalidExpressionWrongRightOperand() {
    AlarmExpression.of("count(log.error{},deterministic=foo,20) > 5");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldNotParseInvalidExpressionMalformedDeterministicKeyword() {
    AlarmExpression.of("count(log.error{},determ=true,20) > 5");
  }

  public void shouldBeDeterministicIfAllSubExpressionAreDeterministic() {
    final String expression1 = "count(log.error{hostname=1,component=A},deterministic,20) > 5";
    final String expression2 = "count(log.error{hostname=1,component=B},deterministic,20) > 10";
    final String expression3 = "count(log.error{hostname=1,component=C},deterministic,20) > 15";

    final String expression = String.format("%s OR %s OR %s", expression1, expression2, expression3);

    assertTrue(new AlarmExpression(expression).isDeterministic());
  }

  public void shouldBeNonDeterministicIfAtLeastOneExpressionIsNonDeterministic() {
    final String expression1 = "count(log.error{hostname=1,component=A},deterministic,20) > 5";
    final String expression2 = "count(log.error{hostname=1,component=B},deterministic,20) > 10";
    final String expression3 = "count(log.error{}) > 15";

    final String expression = String.format("%s OR %s OR %s", expression1, expression2, expression3);

    assertFalse(new AlarmExpression(expression).isDeterministic());
  }
}
