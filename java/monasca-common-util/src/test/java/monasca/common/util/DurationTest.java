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
