package com.hpcloud.mon.common.model.metric;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.testng.annotations.Test;

import com.hpcloud.mon.common.model.metric.CollectdMetric;
import com.hpcloud.mon.common.model.metric.CollectdMetrics;
import com.hpcloud.mon.common.model.metric.FlatMetric;

/**
 * @author Jonathan Halterman
 */
@Test
public class CollectdMetricsTest {
  private String interfaceMetric = "{\"putval\":{\"values\":[287122343,345751104],\"dstypes\":[\"derive\",\"derive\"],\"dsnames\":[\"rx\",\"tx\"],\"time\":1364251708.702,\"interval\":60.000,\"host\":\"dx-aw1rdb1-manage0001.rndb.az2.hpcloud.net:instance-0003472\",\"plugin\":\"libvirt\",\"plugin_instance\":\"\",\"type\":\"if_octets\",\"type_instance\":\"vnet0\"}}";
  private String cpuMetric = "{\"putval\":{\"values\":[50308210000000],\"dstypes\":[\"derive\"],\"dsnames\":[\"value\"],\"time\":1364251768.654,\"interval\":60.000,\"host\":\"dx-aw1rdb1-manage0001.rndb.az2.hpcloud.net:instance-0003472\",\"plugin\":\"libvirt\",\"plugin_instance\":\"\",\"type\":\"virt_cpu_total\",\"type_instance\":\"\"}}";
  private String diskMetric = "{\"putval\":{\"values\":[36184,51182963],\"dstypes\":[\"derive\",\"derive\"],\"dsnames\":[\"read\",\"write\"],\"time\":1365802618.809,\"interval\":60.000,\"host\":\"instance-000d65f3\",\"plugin\":\"libvirt\",\"plugin_instance\":\"\",\"type\":\"disk_ops\",\"type_instance\":\"vda\"}}";
  private String unsupportedMetricType = "{\"putval\":{\"values\":[36184,51182963],\"dstypes\":[\"derive\",\"derive\"],\"dsnames\":[\"read\",\"write\"],\"time\":1365802618.809,\"interval\":60.000,\"host\":\"instance-000d65f3\",\"plugin\":\"libvirt\",\"plugin_instance\":\"\",\"type\":\"virt_vcpu\",\"type_instance\":\"1\"}}";
  private String bockMetric = "{\"putval\":{\"values\":[287122343,345751104],\"dstypes\":[\"derive\",\"derive\"],\"dsnames\":[\"read\",\"write\"],\"time\":1364251708.702,\"interval\":60.000,\"host\":\"dx-aw1rdb1-manage0001.rndb.az2.hpcloud.net:instance-0003472\",\"plugin\":\"bock\",\"plugin_instance\":\"vda\",\"type\":\"disk_octets\",\"type_instance\":\"\"}}";
  private String bockMetric2 = "{\"putval\":{\"values\":[72120],\"dstypes\":[\"counter\"],\"dsnames\":[\"value\"],\"time\":1375300095.416,\"interval\":60.000,\"host\":\"nv-aw2az3-compute0254:instance-000f50e7\",\"plugin\":\"bock\",\"plugin_instance\":\"vdf\",\"type\":\"counter\",\"type_instance\":\"\"}}";
  private String uuidHost = "{\"putval\":{\"values\":[287122343,345751104],\"dstypes\":[\"derive\",\"derive\"],\"dsnames\":[\"rx\",\"tx\"],\"time\":1364251708.702,\"interval\":60.000,\"host\":\"125ddf5e-79bb-4ebc-ab3e-a8539be799ff\",\"plugin\":\"libvirt\",\"plugin_instance\":\"\",\"type\":\"if_octets\",\"type_instance\":\"vnet0\"}}";

  public void testHostForInstanceId() {
    assertEquals(CollectdMetrics.hostForInstanceId(67361), "instance-00010721");
  }

  @SuppressWarnings("serial")
  public void testNovaMetricsToFlatMetrics() {
    SortedMap<String, String> expectedDimensions1 = new TreeMap<String, String>() {
      {
        put("metric_name", "net_in_bytes_count");
        put("device", "eth0");
        put("az", "2");
        put("instance_id", "13426");
      }
    };
    SortedMap<String, String> expectedDimensions2 = new TreeMap<String, String>() {
      {
        put("metric_name", "net_out_bytes_count");
        put("device", "eth0");
        put("az", "2");
        put("instance_id", "13426");
      }
    };
    List<FlatMetric> expected = Arrays.asList(new FlatMetric("hpcs.compute", expectedDimensions1,
        1364251708, 287122343), new FlatMetric("hpcs.compute", expectedDimensions2, 1364251708,
        345751104));
    List<FlatMetric> metrics = CollectdMetrics.toFlatMetrics(interfaceMetric.getBytes());
    assertEquals(metrics, expected);

    expectedDimensions1 = new TreeMap<String, String>() {
      {
        put("metric_name", "cpu_total_time");
        put("az", "2");
        put("instance_id", "13426");
      }
    };
    expected = Arrays.asList(new FlatMetric("hpcs.compute", expectedDimensions1, 1364251768,
        50308210000000L));
    metrics = CollectdMetrics.toFlatMetrics(cpuMetric.getBytes());
    assertEquals(metrics, expected);

    expectedDimensions1 = new TreeMap<String, String>() {
      {
        put("metric_name", "disk_read_ops_count");
        put("device", "vda");
        put("instance_id", "878067");
      }
    };
    expectedDimensions2 = new TreeMap<String, String>() {
      {
        put("metric_name", "disk_write_ops_count");
        put("device", "vda");
        put("instance_id", "878067");
      }
    };
    expected = Arrays.asList(
        new FlatMetric("hpcs.compute", expectedDimensions1, 1365802618, 36184), new FlatMetric(
            "hpcs.compute", expectedDimensions2, 1365802618, 51182963));
    metrics = CollectdMetrics.toFlatMetrics(diskMetric.getBytes());
    assertEquals(metrics, expected);
  }

  @SuppressWarnings("serial")
  public void shouldConvertUUIDHostMetrics() {
    SortedMap<String, String> expectedDimensions1 = new TreeMap<String, String>() {
      {
        put("metric_name", "net_in_bytes_count");
        put("device", "eth0");
        put("instance_id", "125ddf5e-79bb-4ebc-ab3e-a8539be799ff");
      }
    };
    SortedMap<String, String> expectedDimensions2 = new TreeMap<String, String>() {
      {
        put("metric_name", "net_out_bytes_count");
        put("device", "eth0");
        put("instance_id", "125ddf5e-79bb-4ebc-ab3e-a8539be799ff");
      }
    };
    List<FlatMetric> expected = Arrays.asList(new FlatMetric("hpcs.compute", expectedDimensions1,
        1364251708, 287122343), new FlatMetric("hpcs.compute", expectedDimensions2, 1364251708,
        345751104));
    List<FlatMetric> metrics = CollectdMetrics.toFlatMetrics(uuidHost.getBytes());
    assertEquals(metrics, expected);
  }

  public void toFlatMetricsShouldReturnNullForUnsupportedMetricTypes() {
    assertNull(CollectdMetrics.toFlatMetrics(unsupportedMetricType.getBytes()));
  }

  public void testFromJson() {
    assertEquals(CollectdMetrics.fromJson(interfaceMetric.getBytes()), new CollectdMetric(
        "dx-aw1rdb1-manage0001.rndb.az2.hpcloud.net:instance-0003472", "libvirt", "", "if_octets",
        "vnet0", 1364251708, 60, new String[] { "rx", "tx" }, new String[] { "derive", "derive" },
        new double[] { 287122343, 345751104 }));
  }

  @SuppressWarnings("serial")
  public void testBockMetricsToFlatMetrics() {
    SortedMap<String, String> expectedDimensions1 = new TreeMap<String, String>() {
      {
        put("metric_name", "volume_read_bytes");
        put("az", "2");
        put("disk", "vda");
        put("instance_id", "13426");
      }
    };
    SortedMap<String, String> expectedDimensions2 = new TreeMap<String, String>() {
      {
        put("metric_name", "volume_write_bytes");
        put("az", "2");
        put("disk", "vda");
        put("instance_id", "13426");
      }
    };

    List<FlatMetric> expected = Arrays.asList(new FlatMetric("hpcs.volume", expectedDimensions1,
        1364251708, 287122343), new FlatMetric("hpcs.volume", expectedDimensions2, 1364251708,
        345751104));
    List<FlatMetric> metrics = CollectdMetrics.toFlatMetrics(bockMetric.getBytes());
    assertEquals(metrics, expected);
  }

  @SuppressWarnings("serial")
  public void testBockMetricToFlatMetrics2() {
    SortedMap<String, String> expectedDimensions = new TreeMap<String, String>() {
      {
        put("metric_name", "volume_idle_time");
        put("az", "3");
        put("disk", "vdf");
        put("instance_id", "1003751");
      }
    };
    List<FlatMetric> expected = Arrays.asList(new FlatMetric("hpcs.volume", expectedDimensions,
        1375300095, 72120));
    List<FlatMetric> metrics = CollectdMetrics.toFlatMetrics(bockMetric2.getBytes());
    assertEquals(metrics, expected);
  }
}
