package monasca.common.util.stats;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import monasca.common.util.stats.Statistics.Average;
import monasca.common.util.stats.Statistics.Count;
import monasca.common.util.stats.Statistics.Max;
import monasca.common.util.stats.Statistics.Min;
import monasca.common.util.stats.Statistics.Sum;

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
