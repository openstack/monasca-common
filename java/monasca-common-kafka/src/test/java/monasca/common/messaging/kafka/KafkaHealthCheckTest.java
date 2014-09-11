package monasca.common.messaging.kafka;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.codahale.metrics.health.HealthCheck.Result;

@Test(groups = "integration")
public class KafkaHealthCheckTest {
  KafkaConfiguration config;

  @BeforeTest
  protected void beforeTest() {
    config = new KafkaConfiguration();
    config.zookeeperUris = new String[] { "192.168.10.10:2181" };
    config.brokerUris = new String[] { "192.168.10.10:9092" };
  }

  public void shouldCheckHealth() throws Exception {
    Result result = new KafkaHealthCheck(config).check();
    if (!result.isHealthy() && result.getClass() != null)
      fail(result.getMessage(), result.getError());
    assertTrue(result.isHealthy(), result.getMessage());
  }
}
