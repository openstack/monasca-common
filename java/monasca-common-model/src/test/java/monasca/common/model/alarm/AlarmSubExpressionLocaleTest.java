/*
 * Copyright 2015 FUJITSU LIMITED
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

import java.util.List;
import java.util.Locale;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import monasca.common.model.metric.MetricDefinition;

/**
 * Checks if conversion from decimal to string is Locale independent.
 *
 * @author lukasz.zajaczkowski@ts.fujitsu.com
 *
 */
@Test
public class AlarmSubExpressionLocaleTest {

  private static final String EXPECTED_EXPRESSION = "min(hpcs.compute{instance_id=5, metric_name=cpu, device=1}) < 1.2";

  public void shouldBeLocaleIndependent() {
    List<Locale> localeList = Lists.newArrayList(
        Locale.GERMAN, Locale.CHINA, Locale.FRANCE, Locale.JAPAN, Locale.CANADA, Locale.KOREA
        );

    for (Locale locale : localeList) {
      Locale.setDefault(locale);
      AlarmSubExpression alarmSubExpression =
          new AlarmSubExpression(AggregateFunction.MIN, new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
              .put("instance_id", "5").put("metric_name", "cpu").put("device", "1").build()), AlarmOperator.LT, 1.2, 60, 1);

      assertEquals(alarmSubExpression.getExpression(), EXPECTED_EXPRESSION, "Not correct expression for locale " + locale.getDisplayName());

    }
  }

  public void shouldWorkWithDefaultLocale() {

    Locale.setDefault(Locale.US);
    AlarmSubExpression alarmSubExpression =
        new AlarmSubExpression(AggregateFunction.MIN, new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
            .put("instance_id", "5").put("metric_name", "cpu").put("device", "1").build()), AlarmOperator.LT, 1.2, 60, 1);

    assertEquals(alarmSubExpression.getExpression(), EXPECTED_EXPRESSION, "Not correct expression for default locale");
  }
}
