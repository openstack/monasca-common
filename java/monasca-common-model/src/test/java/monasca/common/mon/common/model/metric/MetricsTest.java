package monasca.common.model.metric;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.testng.annotations.Test;

import monasca.common.model.metric.Metric;
import monasca.common.model.metric.Metrics;

@Test
public class MetricsTest {
  public void shouldSerializeValue() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "cpu");
    dimensions.put("instance_id", "123");
    Metric metric = new Metric("hpcs.compute", dimensions, 123345, 5);

    String json = Metrics.toJson(metric);
    assertEquals(
        json,
        "{\"name\":\"hpcs.compute\",\"dimensions\":{\"instance_id\":\"123\",\"metric_name\":\"cpu\"},\"timestamp\":123345,\"value\":5.0}");
  }

  public void shouldSerializeTimeValues() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "cpu");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    Metric metric = new Metric("hpcs.compute", dimensions, 123345, new double[][] { { 123, 5 },
        { 456, 6 } });

    String json = Metrics.toJson(metric);
    assertEquals(
        json,
        "{\"name\":\"hpcs.compute\",\"dimensions\":{\"device\":\"2\",\"instance_id\":\"123\",\"metric_name\":\"cpu\"},\"timestamp\":123345,\"time_values\":[[123,5.0],[456,6.0]]}");
  }

  public void shouldSerializeAndDeserialize() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "cpu");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, new double[][] { { 123, 5 },
        { 456, 6 } });

    Metric metric = Metrics.fromJson(Metrics.toJson(expected).getBytes());
    assertEquals(metric, expected);
  }

  public void shouldSerializeValueUTF() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "foôbár");
    dimensions.put("instance_id", "123");
    Metric metric = new Metric("hpcs.compute", dimensions, 123345, 5);

    String json = Metrics.toJson(metric);
    assertEquals(
        json,
        "{\"name\":\"hpcs.compute\",\"dimensions\":{\"instance_id\":\"123\",\"metric_name\":\"foôbár\"},\"timestamp\":123345,\"value\":5.0}");
  }

  public void shouldSerializeAndDeserializeUTF8() throws UnsupportedEncodingException {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "foôbár");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, new double[][] { { 123, 5 },
        { 456, 6 } });

    Metric metric;
    metric = Metrics.fromJson(Metrics.toJson(expected).getBytes("UTF-8"));
    assertEquals(metric, expected);
  }

  public void shouldSerializeAndDeserializeUTF8_2() throws UnsupportedEncodingException {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "fo\u00f4b\u00e1r");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, new double[][] { { 123, 5 },
        { 456, 6 } });

    Metric metric;
    metric = Metrics.fromJson(Metrics.toJson(expected).getBytes("UTF-8"));
    assertEquals(metric, expected);
  }

  public void shouldSerializeAndDeserializeUTF8_3() throws UnsupportedEncodingException {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "fo\u00f4b\u00e1r");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    SortedMap<String, String> dimensions2 = new TreeMap<String, String>();
    dimensions2.put("metric_name", "foôbár");
    dimensions2.put("device", "2");
    dimensions2.put("instance_id", "123");
    Metric expected_escaped = new Metric("hpcs.compute", dimensions, 123345, new double[][] {
        { 123, 5 }, { 456, 6 } });
    Metric expected_nonescaped = new Metric("hpcs.compute", dimensions2, 123345, new double[][] {
        { 123, 5 }, { 456, 6 } });

    Metric metric;
    metric = Metrics.fromJson(Metrics.toJson(expected_escaped).getBytes("UTF-8"));
    assertEquals(metric, expected_nonescaped);
  }
}
