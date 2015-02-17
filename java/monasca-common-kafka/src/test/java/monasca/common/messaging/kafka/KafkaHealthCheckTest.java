/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
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
