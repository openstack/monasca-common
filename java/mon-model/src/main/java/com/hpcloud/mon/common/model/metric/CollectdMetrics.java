package com.hpcloud.mon.common.model.metric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.hpcloud.mon.common.model.Namespaces;
import com.hpcloud.mon.common.model.alarm.AlarmExpression;
import com.hpcloud.mon.common.model.alarm.AlarmSubExpression;
import com.hpcloud.util.Exceptions;

/**
 * Utilities for working with collectd metrics.
 * 
 * @author Jonathan Halterman
 */
public final class CollectdMetrics {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final ObjectReader COLLECTD_METRIC_READER;
  private static final String PUTVAL_ELEMENT_KEY = "putval";
  private static final String HOST_ELEMENT_KEY = "host";
  private static final String PLUGIN_ELEMENT_KEY = "plugin";
  private static final String PLUGIN_INSTANCE_ELEMENT_KEY = "plugin_instance";
  private static final String TYPE_ELEMENT_KEY = "type";
  private static final String TYPE_INSTANCE_ELEMENT_KEY = "type_instance";
  private static final String VALUES_ELEMENT_KEY = "values";
  private static final String TIME_ELEMENT_KEY = "time";
  private static final String DSNAMES_ELEMENT_KEY = "dsnames";
  private static final String LIBVIRT_PLUGIN = "libvirt";
  private static final String BOCK_PLUGIN = "bock";
  public static final String METRIC_NAME_DIM = "metric_name";
  private static final String AZ_DIM = "az";
  public static final String DEVICE_DIM = "device";
  public static final String DISK_DIM = "disk";
  private static final String INSTANCE_ID_DIM = "instance_id";
  private static final Map<String, MetricDefinitionDecoder> METRIC_DEFINITION_DECODERS;
  private static final Map<String, MetricDefinitionEncoder> METRIC_DEFINITION_ENCODERS;
  private static final Pattern AZ_PATTERN = Pattern.compile("(az([1-3]){1})",
      Pattern.CASE_INSENSITIVE);
  private static final Joiner COLON_JOINER = Joiner.on(':');
  private static final Splitter COLON_SPLITTER = Splitter.on(':');

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    COLLECTD_METRIC_READER = OBJECT_MAPPER.reader(CollectdMetric.class);
    METRIC_DEFINITION_DECODERS = new HashMap<String, MetricDefinitionDecoder>();
    METRIC_DEFINITION_ENCODERS = new HashMap<String, MetricDefinitionEncoder>();

    // Decodes libvirt metric definitions
    METRIC_DEFINITION_DECODERS.put(LIBVIRT_PLUGIN, new MetricDefinitionDecoder() {
      {
        metricNames.put("virt_cpu_total:value", "cpu_total_time");
        metricNames.put("disk_ops:read", "disk_read_ops_count");
        metricNames.put("disk_ops:write", "disk_write_ops_count");
        metricNames.put("disk_octets:read", "disk_read_bytes_count");
        metricNames.put("disk_octets:write", "disk_write_bytes_count");
        metricNames.put("if_octets:rx", "net_in_bytes_count");
        metricNames.put("if_octets:tx", "net_out_bytes_count");
        metricNames.put("if_packets:rx", "net_in_packets_count");
        metricNames.put("if_packets:tx", "net_out_packets_count");
        metricNames.put("if_dropped:rx", "net_in_dropped_count");
        metricNames.put("if_dropped:tx", "net_out_dropped_count");
        metricNames.put("if_errors:rx", "net_in_errors_count");
        metricNames.put("if_errors:tx", "net_out_errors_count");
      }

      @Override
      SortedMap<String, String> dimensionsFor(String host, String pluginInstance, String type,
          String typeInstance) {
        SortedMap<String, String> dimensions = super.dimensionsFor(host, pluginInstance, type,
            typeInstance);
        if (!Strings.isNullOrEmpty(type) && !Strings.isNullOrEmpty(typeInstance))
          dimensions.put(DEVICE_DIM, type.startsWith("if") ? "eth0" : typeInstance);
        return dimensions;
      }

      @Override
      String namespace() {
        return Namespaces.COMPUTE_NAMESPACE;
      }
    });

    METRIC_DEFINITION_ENCODERS.put(Namespaces.COMPUTE_NAMESPACE, new MetricDefinitionEncoder() {
      {
        metricNames.put("cpu_total_time", new String[] { "virt_cpu_total", "value" });
        metricNames.put("disk_read_ops_count", new String[] { "disk_ops", "read" });
        metricNames.put("disk_write_ops_count", new String[] { "disk_ops", "write" });
        metricNames.put("disk_read_bytes_count", new String[] { "disk_octets", "read" });
        metricNames.put("disk_write_bytes_count", new String[] { "disk_octets", "write" });
        metricNames.put("net_in_bytes_count", new String[] { "if_octets", "rx" });
        metricNames.put("net_out_bytes_count", new String[] { "if_octets", "tx" });
        metricNames.put("net_in_packets_count", new String[] { "if_packets", "rx" });
        metricNames.put("net_out_packets_count", new String[] { "if_packets", "tx" });
        metricNames.put("net_in_dropped_count", new String[] { "if_dropped", "rx" });
        metricNames.put("net_out_dropped_count", new String[] { "if_dropped", "tx" });
        metricNames.put("net_in_errors_count", new String[] { "if_errors", "rx" });
        metricNames.put("net_out_errors_count", new String[] { "if_errors", "tx" });
      }
    });

    // Decodes bock metric definitions
    METRIC_DEFINITION_DECODERS.put(BOCK_PLUGIN, new MetricDefinitionDecoder() {
      {
        metricNames.put("disk_ops:read", "volume_read_ops");
        metricNames.put("disk_ops:write", "volume_write_ops");
        metricNames.put("disk_octets:read", "volume_read_bytes");
        metricNames.put("disk_octets:write", "volume_write_bytes");
        metricNames.put("disk_time:read", "volume_read_time");
        metricNames.put("disk_time:write", "volume_write_time");
        metricNames.put("counter:value", "volume_idle_time");
      }

      @Override
      SortedMap<String, String> dimensionsFor(String host, String pluginInstance, String type,
          String typeInstance) {
        SortedMap<String, String> dimensions = super.dimensionsFor(host, pluginInstance, type,
            typeInstance);
        if (pluginInstance != null)
          dimensions.put(DISK_DIM, pluginInstance);
        return dimensions;
      }

      @Override
      String namespace() {
        return Namespaces.VOLUME_NAMESPACE;
      }
    });

    METRIC_DEFINITION_ENCODERS.put(Namespaces.VOLUME_NAMESPACE, new MetricDefinitionEncoder() {
      {
        metricNames.put("volume_read_ops", new String[] { "disk_ops", "read" });
        metricNames.put("volume_write_ops", new String[] { "disk_ops", "write" });
        metricNames.put("volume_read_bytes", new String[] { "disk_octets", "read" });
        metricNames.put("volume_write_bytes", new String[] { "disk_octets", "write" });
        metricNames.put("volume_read_time", new String[] { "disk_time", "read" });
        metricNames.put("volume_write_time", new String[] { "disk_time", "write" });
        metricNames.put("volume_idle_time", new String[] { "counter", "value" });
      }
    });
  }

  private CollectdMetrics() {
  }

  /** Decodes metric types. */
  static abstract class MetricDefinitionDecoder {
    protected final Map<String, String> metricNames = new HashMap<String, String>();

    /**
     * Returns a metricType intended to be composed of plugin, pluginInstance, type, typeInstance,
     * dsName - in that order, else {@code null} if no metric type could be decoded for the
     * {@code components}.
     */
    protected String decodeMetricTypeFor(String... components) {
      return metricNames.get(COLON_JOINER.join(components));
    }

    SortedMap<String, String> dimensionsFor(String host, String pluginInstance, String type,
        String typeInstance) {
      SortedMap<String, String> dimensions = new TreeMap<String, String>();

      if (host.contains("instance-")) {
        String az = azForHost(host);
        if (az != null)
          dimensions.put("az", az);
        String instanceId = instanceIdForHost(host);
        if (instanceId != null)
          dimensions.put(INSTANCE_ID_DIM, instanceId);
      } else if (host.length() == 36) {
        try {
          UUID.fromString(host);
          dimensions.put(INSTANCE_ID_DIM, host);
        } catch (IllegalArgumentException e) {
        }
      }

      return dimensions;
    }

    String metricNameFor(String pluginInstance, String type, String typeInstance, String dsName) {
      return decodeMetricTypeFor(type, dsName);
    }

    abstract String namespace();
  }

  /** Encodes metric types. */
  static abstract class MetricDefinitionEncoder {
    protected final Map<String, String[]> metricNames = new HashMap<String, String[]>();
  }

  /**
   * Returns the collectd type and ds_name for the {@code namespace} and {@code metricName}, else
   * returns null.
   */
  public static String[] collectdNamesFor(String namespace, String metricName) {
    MetricDefinitionEncoder encoder = METRIC_DEFINITION_ENCODERS.get(namespace);
    return encoder == null ? null : encoder.metricNames.get(metricName);
  }

  /**
   * Returns a CollectdMetric instance for the {@code collectdMetricJson}.
   * 
   * @throws RuntimeException if an error occurs while parsing the {@code collectdMetricJson}
   */
  public static CollectdMetric fromJson(byte[] collectdMetricJson) {
    try {
      JsonNode rootNode = OBJECT_MAPPER.readTree(collectdMetricJson);
      JsonNode putvalNode = rootNode.get(PUTVAL_ELEMENT_KEY);
      return COLLECTD_METRIC_READER.readValue(putvalNode);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to parse collectd metric json: %s", new String(
          collectdMetricJson));
    }
  }

  /**
   * Returns a host, consisting of the {@code instanceId} left padded with '0' characters to a
   * length of 8. Example: instance-000afabb
   * 
   * <p>
   * Note: The 8 character length hex encoding is intended to match the encoding that nova uses as
   * per: https://noc-aw2az1-server01.uswest.hpcloud.net/tools/nova_lookup.php
   */
  public static String hostForInstanceId(int instanceId) {
    String hex = Integer.toHexString(Integer.valueOf(instanceId));
    String instancePrefix = "instance-";

    StringBuilder sb = new StringBuilder(instancePrefix.length() + 8);
    sb.append(instancePrefix);
    for (int i = hex.length(); i < 8; i++)
      sb.append('0');
    sb.append(hex);
    return sb.toString();
  }

  /**
   * Returns true if the {@code namespace} is supported by collectd, else false.
   */
  public static boolean isCollectdNamespace(String namespace) {
    return (namespace.equalsIgnoreCase(Namespaces.COMPUTE_NAMESPACE) || namespace.equalsIgnoreCase(Namespaces.VOLUME_NAMESPACE));
  }

  /**
   * Returns whether a reserved namespace dimension is supported.
   */
  public static boolean isSupportedDimension(String dimension) {
    return INSTANCE_ID_DIM.equals(dimension) || AZ_DIM.equals(dimension)
        || METRIC_NAME_DIM.equals(dimension) || DEVICE_DIM.equals(dimension)
        || DISK_DIM.equals(dimension);
  }

  /**
   * Returns true if the {@code dimension} for the {@code namespace} is supported by collectd, else
   * false.
   */
  public static boolean isSupportedDimension(String namespace, String dimension) {
    return !Namespaces.isReserved(namespace) || isSupportedDimension(dimension);
  }

  /**
   * Returns the collectd plugin for the namespace, else null.
   */
  public static String pluginForNamespace(String namespace) {
    if (Namespaces.COMPUTE_NAMESPACE.equals(namespace))
      return LIBVIRT_PLUGIN;
    if (Namespaces.VOLUME_NAMESPACE.equals(namespace))
      return BOCK_PLUGIN;
    return null;
  }

  /**
   * Removes dimensions from the {@code expression} that are not supported by collectd.
   */
  public static void removeUnsupportedDimensions(AlarmExpression expression) {
    for (AlarmSubExpression subExpression : expression.getSubExpressions())
      removeUnsupportedDimensions(subExpression.getMetricDefinition());
  }

  /**
   * Removes dimensions from the {@code metricDefinition} that are not supported by collectd.
   */
  public static void removeUnsupportedDimensions(MetricDefinition metricDefinition) {
    if (Namespaces.isReserved(metricDefinition.namespace) && metricDefinition.dimensions != null) {
      for (Iterator<String> it = metricDefinition.dimensions.keySet().iterator(); it.hasNext();) {
        String dim = it.next();
        if (!isSupportedDimension(dim))
          it.remove();
      }

      metricDefinition.setDimensions(metricDefinition.dimensions);
    }
  }

  /**
   * Returns the collectd subject that originated from the plugin_instance or type_instance for the
   * {@code namespace} and {@code dimensions}, else null.
   */
  public static String subjectFor(String namespace, Map<String, String> dimensions) {
    if (Namespaces.COMPUTE_NAMESPACE.equals(namespace))
      return dimensions.get(DEVICE_DIM);
    if (Namespaces.VOLUME_NAMESPACE.equals(namespace))
      return dimensions.get(DISK_DIM);
    return null;
  }

  /**
   * Returns flat metrics converted from the {@code collectdMetricJson} else {@code null} if the
   * metric type is not supported.
   */
  public static List<FlatMetric> toFlatMetrics(byte[] collectdMetricJson) {
    return toMetrics(collectdMetricJson, FlatMetric.class);
  }

  /**
   * Returns flat metrics JSON converted from the {@code collectdMetricJson} else {@code null} if
   * the metric type is not supported.
   */
  public static List<String> toFlatMetricsJson(byte[] collectdMetricJson) {
    List<FlatMetric> flatMetrics = toFlatMetrics(collectdMetricJson);
    if (flatMetrics.isEmpty())
      return null;
    List<String> jsons = new ArrayList<String>(flatMetrics.size());
    for (FlatMetric flatMetric : flatMetrics) {
      String json = FlatMetrics.toJson(flatMetric);
      if (json != null)
        jsons.add(json);
    }

    return jsons;
  }

  /**
   * Returns metrics converted from the {@code collectdMetricJson} else {@code null} if the metric
   * type is not supported.
   */
  public static List<Metric> toMetrics(byte[] collectdMetricJson) {
    return toMetrics(collectdMetricJson, Metric.class);
  }

  /**
   * Returns the AZ for the collectd {@code host} value.
   */
  private static String azForHost(String host) {
    Matcher matcher = AZ_PATTERN.matcher(host);
    return matcher.find() ? matcher.group(2) : null;
  }

  /**
   * Returns the user-facing nova instance id for the collectd {@code host} value.
   */
  private static String instanceIdForHost(String host) {
    String hosts[] = Iterables.toArray(COLON_SPLITTER.split(host), String.class);
    String novaInstanceId = hosts.length > 1 ? hosts[1] : hosts[0];
    novaInstanceId = novaInstanceId.substring(novaInstanceId.indexOf('-') + 1);
    return Integer.valueOf(novaInstanceId, 16).toString();
  }

  /**
   * Returns flat metrics converted from the {@code collectdMetricJson} else {@code null} if the
   * metric type is not supported.
   * 
   * @throws RuntimeException if an error occurs while converting the {@code collectdMetricJson}
   */
  @SuppressWarnings("unchecked")
  private static <T> List<T> toMetrics(byte[] collectdMetricJson, Class<T> metricClass) {
    List<T> metrics = null;

    try {
      JsonNode rootNode = OBJECT_MAPPER.readTree(collectdMetricJson);
      JsonNode putvalNode = rootNode.get(PUTVAL_ELEMENT_KEY);
      String host = putvalNode.get(HOST_ELEMENT_KEY).asText();
      String plugin = putvalNode.get(PLUGIN_ELEMENT_KEY).asText();

      MetricDefinitionDecoder decoder = METRIC_DEFINITION_DECODERS.get(plugin);
      if (decoder == null)
        throw new IllegalArgumentException("No metric decoder could be found for the " + plugin);

      String pluginInstance = putvalNode.get(PLUGIN_INSTANCE_ELEMENT_KEY).asText();
      String type = putvalNode.get(TYPE_ELEMENT_KEY).asText();
      String typeInstance = putvalNode.get(TYPE_INSTANCE_ELEMENT_KEY).asText();
      ArrayNode dsNames = (ArrayNode) putvalNode.get(DSNAMES_ELEMENT_KEY);
      ArrayNode valuesNode = (ArrayNode) putvalNode.get(VALUES_ELEMENT_KEY);
      long timestamp = putvalNode.get(TIME_ELEMENT_KEY).asLong();

      String namespace = decoder.namespace();
      for (int i = 0; i < valuesNode.size(); i++) {
        String dsName = dsNames.size() > 0 ? dsNames.get(i).asText() : null;
        String metricName = decoder.metricNameFor(pluginInstance, type, typeInstance, dsName);
        if (metricName == null)
          continue;

        SortedMap<String, String> dimensions = decoder.dimensionsFor(host, pluginInstance, type,
            typeInstance);
        dimensions.put(METRIC_NAME_DIM, metricName);
        long value = valuesNode.get(i).asLong();

        T metric = null;
        if (FlatMetric.class.equals(metricClass))
          metric = (T) new FlatMetric(namespace, dimensions, timestamp, value);
        else
          metric = (T) new Metric(new MetricDefinition(namespace, dimensions), timestamp, value);

        if (metrics == null)
          metrics = new ArrayList<T>(valuesNode.size());
        metrics.add(metric);
      }
    } catch (IOException e) {
      throw Exceptions.uncheck(e, "Failed to convert collectd metric json to %s: %s",
          metricClass.getSimpleName(), new String(collectdMetricJson));
    }

    return metrics;
  }
}
