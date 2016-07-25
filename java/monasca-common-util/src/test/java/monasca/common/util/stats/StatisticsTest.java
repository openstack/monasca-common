/*
 * (C) Copyright 2014, 2016 Hewlett Packard Enterprise Development LP
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
package monasca.common.util.stats;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import monasca.common.util.stats.Statistics.Average;
import monasca.common.util.stats.Statistics.Count;
import monasca.common.util.stats.Statistics.Last;
import monasca.common.util.stats.Statistics.Max;
import monasca.common.util.stats.Statistics.Min;
import monasca.common.util.stats.Statistics.Sum;

@Test
public class StatisticsTest {
  @DataProvider(name = "metricTypes")
  public Object[][] createData1() {
    return new Object[][] { { new Average(), 3 }, { new Sum(), 6 }, { new Min(), 2 },
        { new Max(), 4 }, { new Count(), 2 }, { new Last(), 4 }};
  }

  @Test(dataProvider = "metricTypes")
  public void testStat(Statistic stat, double expectedValue) {
    stat.addValue(2, 1);
    stat.addValue(4, 2);
    assertEquals(stat.value(), expectedValue, stat.getClass().getName());
  }
}
