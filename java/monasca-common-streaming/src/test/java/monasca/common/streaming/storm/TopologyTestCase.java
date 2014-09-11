package monasca.common.streaming.storm;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;

import com.google.common.base.Preconditions;
import monasca.common.util.Injector;

@Test(groups = "integration")
public class TopologyTestCase {
  public static final String TEST_TOPOLOGY_NAME = "test-maas-alarming";
  protected static volatile LocalCluster cluster;

  @BeforeSuite
  protected void startTopology() throws Exception {
    if (cluster == null) {
      synchronized (TopologyTestCase.class) {
        if (cluster == null) {
          Preconditions.checkArgument(Injector.isBound(Config.class),
              "You must bind a storm config");
          Preconditions.checkArgument(Injector.isBound(StormTopology.class),
              "You must bind a storm topology");

          cluster = new LocalCluster();
          cluster.submitTopology(TEST_TOPOLOGY_NAME, Injector.getInstance(Config.class),
              Injector.getInstance(StormTopology.class));
        }
      }
    }
  }

  @AfterSuite
  protected static void stopTopology() {
    cluster.killTopology(TEST_TOPOLOGY_NAME);
    cluster.shutdown();
  }
}
