package com.hpcloud.util.stats;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hpcloud.util.stats.Statistics.Average;
import com.hpcloud.util.stats.Statistics.Count;
import com.hpcloud.util.stats.Statistics.Max;
import com.hpcloud.util.stats.Statistics.Min;
import com.hpcloud.util.stats.Statistics.Sum;

/**
 * @author Jonathan Halterman
 */
@Test
public class StatisticsTest {
  @DataProvider(name = "metricTypes")
  public Object[][] createData1() {
    return new Object[][] { { new Average(), 3 }, { new Sum(), 6 }, { new Min(), 2 },
        { new Max(), 4 }, { new Count(), 2 }, };
  }

  @Test(dataProvider = "metricTypes")
  public void testStat(Statistic stat, double expectedValue) {
    stat.addValue(2);
    stat.addValue(4);
    assertEquals(stat.value(), expectedValue, stat.getClass().getName());
  }
}
