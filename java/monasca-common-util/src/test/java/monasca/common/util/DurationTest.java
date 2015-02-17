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
package monasca.common.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

@Test
public class DurationTest {
  public void testPattern() {
    assertTrue(Duration.PATTERN.matcher("1s").matches());
    assertTrue(Duration.PATTERN.matcher("1 s").matches());
    assertTrue(Duration.PATTERN.matcher("1 second").matches());
  }

  public void testOf() {
    assertEquals(Duration.seconds(1), Duration.of("1s"));
    assertEquals(Duration.seconds(1), Duration.of("1 s"));
    assertEquals(Duration.seconds(1), Duration.of("1 second"));
    assertEquals(Duration.seconds(10), Duration.of("10 seconds"));
  }

  public void shouldSerializeAndDeserializeFromJson() throws Exception {
    Duration d = Duration.of("10m");
    ObjectMapper om = new ObjectMapper();
    String ser = om.writeValueAsString(d);
    Duration dd = om.readValue(ser, Duration.class);
    assertEquals(dd, d);
  }

  public void shouldSerializeAndDeserializeInfiniteDurationFromJson() throws Exception {
    Duration d = Duration.INFINITE;
    ObjectMapper om = new ObjectMapper();
    String ser = om.writeValueAsString(d);
    Duration dd = om.readValue(ser, Duration.class);
    assertEquals(dd, d);
  }
}
