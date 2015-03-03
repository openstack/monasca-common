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
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "foobar");
    valueMeta.put("deklan", "123");
    Metric metric = new Metric("hpcs.compute", dimensions, 123345, 5, valueMeta);
    String json = Metrics.toJson(metric);
    assertEquals(
        json,
        "{\"name\":\"hpcs.compute\",\"dimensions\":{\"instance_id\":\"123\",\"metric_name\":\"cpu\"}," +
        "\"timestamp\":123345,\"value\":5.0,\"value_meta\":{\"deklan\":\"123\",\"result\":\"foobar\"}}");
  }

  public void shouldSerializeAndDeserialize() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "cpu");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "foobar");
    valueMeta.put("deklan", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, 55.0, valueMeta);

    Metric metric = Metrics.fromJson(Metrics.toJson(expected).getBytes());
    assertEquals(metric, expected);
  }

  public void shouldSerializeValueUTF() {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "foôbár");
    dimensions.put("instance_id", "123");
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "boôbár");
    valueMeta.put("deklan", "123");
    Metric metric = new Metric("hpcs.compute", dimensions, 123345, 5, valueMeta);
    String json = Metrics.toJson(metric);
    assertEquals(
        json,
        "{\"name\":\"hpcs.compute\",\"dimensions\":{\"instance_id\":\"123\",\"metric_name\":\"foôbár\"}," +
        "\"timestamp\":123345,\"value\":5.0,\"value_meta\":{\"deklan\":\"123\",\"result\":\"boôbár\"}}");
  }

  public void shouldSerializeAndDeserializeUTF8() throws UnsupportedEncodingException {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "foôbár");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "foôbár");
    valueMeta.put("deklan", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, 42.0, valueMeta);

    Metric metric;
    metric = Metrics.fromJson(Metrics.toJson(expected).getBytes("UTF-8"));
    assertEquals(metric, expected);
  }

  public void shouldSerializeAndDeserializeUTF8_2() throws UnsupportedEncodingException {
    SortedMap<String, String> dimensions = new TreeMap<String, String>();
    dimensions.put("metric_name", "fo\u00f4b\u00e1r");
    dimensions.put("device", "2");
    dimensions.put("instance_id", "123");
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "fo\u00f4b\u00e1r");
    valueMeta.put("deklan", "123");
    Metric expected = new Metric("hpcs.compute", dimensions, 123345, 84.0, valueMeta);

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
    SortedMap<String, String> valueMeta = new TreeMap<String, String>();
    valueMeta.put("result", "fo\u00f4b\u00e1r");
    valueMeta.put("deklan", "123");
    SortedMap<String, String> valueMeta2 = new TreeMap<String, String>();
    valueMeta2.put("result", "foôbár");
    valueMeta2.put("deklan", "123");
    Metric expected_escaped = new Metric("hpcs.compute", dimensions, 123345, 42.0, valueMeta);
    Metric expected_nonescaped = new Metric("hpcs.compute", dimensions2, 123345, 42.0, valueMeta2);

    Metric metric;
    metric = Metrics.fromJson(Metrics.toJson(expected_escaped).getBytes("UTF-8"));
    assertEquals(metric, expected_nonescaped);
  }
}
