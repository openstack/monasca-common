package com.hpcloud.mon.common.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

/**
 * Utilities for working with Namespaces.
 * 
 * @author Jonathan Halterman
 * @author Todd Walk
 */
public class Namespaces {
  public static final String COMPUTE_NAMESPACE = "hpcs.compute";
  public static final String VOLUME_NAMESPACE = "hpcs.volume";
  public static final String RESERVED_NAMESPACE_PREFIX = "hpcs.";
  public static final String OBJECT_STORE_NAMESPACE = "hpcs.object-store";
  private static final Pattern UUID_PATTERN = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
  private static final Map<String, List<String>> NAMESPACE_SUPPORTED_DIMENSIONS;
  private static final Map<String, List<String>> NAMESPACE_REQUIRED_DIMENSIONS;
  private static final Map<String, List<String>> NAMESPACE_METRICS;
  private static final Map<String, NamespaceInfo> NAMESPACE_INFO;

  private static final NamespaceInfo COMPUTE_NAMESPACE_INFO = new NamespaceInfo() {
    // US West
    private final List<String> requiredDimsFor11 = Arrays.asList("instance_id", "instance_uuid",
        "az");
    // US East
    private final List<String> requiredDimsFor2 = Arrays.asList("instance_id");

    @Override
    public List<String> getRequiredDimensions(String serviceVersion) {
      if ("2".equals(serviceVersion))
        return requiredDimsFor2;
      else
        return requiredDimsFor11;
    }

    @Override
    public String getResourceIdDimension(String serviceVersion) {
      if ("2".equals(serviceVersion))
        return "instance_id";
      else
        return "instance_uuid";
    }

    @Override
    public String getSecondaryResourceIdDimension(String serviceVersion) {
      if ("1.1".equals(serviceVersion))
        return "instance_id";
      return null;
    }

    @Override
    public boolean isValid(String dimensionName, String dimensionValue) {
      if ("instance_id".equals(dimensionName))
        return dimensionValue.length() != 36 || UUID_PATTERN.matcher(dimensionValue).matches();
      if ("instance_uuid".equals(dimensionName))
        return UUID_PATTERN.matcher(dimensionValue).matches();
      if ("az".equals(dimensionName))
        return Ints.tryParse(dimensionValue) != null;
      return true;
    }
  };

  private static final NamespaceInfo VOLUME_NAMESPACE_INFO = new NamespaceInfo() {
    // US West is 1.1
    private final List<String> requiredDimsFor11 = Arrays.asList("instance_id", "instance_uuid",
        "az");
    // US East is 1.0
    private final List<String> requiredDimsFor10 = Arrays.asList("instance_id");
    // US East & West (nova 13.5+)
    private final List<String> requiredDimsFor2 = Arrays.asList("instance_id", "disk");

    @Override
    public List<String> getRequiredDimensions(String serviceVersion) {
      if ("1.0".equals(serviceVersion))
        return requiredDimsFor10;
      else if ("1.1".equals(serviceVersion))
        return requiredDimsFor11;
      else
        return requiredDimsFor2;
    }

    @Override
    public String getResourceIdDimension(String serviceVersion) {
      if ("1.1".equals(serviceVersion))
        return "instance_uuid";
      else
        return "instance_id";
    }

    @Override
    public String getSecondaryResourceIdDimension(String serviceVersion) {
      if ("1.1".equals(serviceVersion))
        return "instance_id";
      return null;
    }

    @Override
    public boolean isValid(String dimensionName, String dimensionValue) {
      if ("instance_id".equals(dimensionName))
        return dimensionValue.length() != 36 || UUID_PATTERN.matcher(dimensionValue).matches();
      if ("instance_uuid".equals(dimensionName))
        return UUID_PATTERN.matcher(dimensionValue).matches();
      if ("az".equals(dimensionName))
        return Ints.tryParse(dimensionValue) != null;
      return true;
    }
  };

  private static final NamespaceInfo OBJECT_STORE_NAMESPACE_INFO = new NamespaceInfo() {
    // Loosen restrictions for swift (until we re-write all of this code)
    private final List<String> requiredDims = Collections.emptyList();

    @Override
    public List<String> getRequiredDimensions(String serviceVersion) {
      return requiredDims;
    }

    @Override
    public String getResourceIdDimension(String serviceVersion) {
      return "container";
    }

    @Override
    public String getSecondaryResourceIdDimension(String serviceVersion) {
      return null;
    }

    @Override
    public boolean isValid(String dimensionName, String dimensionValue) {
      // Commented out to loosen that restriction for swift (until we re-write all of this code)
      // if ("container".equals(dimensionName))
      // return dimensionValue.length() < 256 || !dimensionValue.contains("/");
      return true;
    }
  };

  static {
    NAMESPACE_SUPPORTED_DIMENSIONS = new HashMap<String, List<String>>();
    NAMESPACE_REQUIRED_DIMENSIONS = new HashMap<String, List<String>>();
    NAMESPACE_METRICS = new HashMap<String, List<String>>();
    NAMESPACE_INFO = new HashMap<String, NamespaceInfo>();

    // Compute
    NAMESPACE_SUPPORTED_DIMENSIONS.put(COMPUTE_NAMESPACE,
        Arrays.asList("instance_uuid", "instance_id", "az"));
    NAMESPACE_REQUIRED_DIMENSIONS.put(COMPUTE_NAMESPACE,
        Arrays.asList("instance_id", "instance_uuid", "az"));
    NAMESPACE_METRICS.put(COMPUTE_NAMESPACE, Arrays.asList("cpu_total_time",
        "cpu_total_utilization", "disk_read_ops", "disk_read_ops_count", "disk_write_ops",
        "disk_write_ops_count", "disk_read_bytes", "disk_read_bytes_count", "disk_write_bytes",
        "disk_write_bytes_count", "net_in_bytes", "net_in_bytes_count", "net_out_bytes",
        "net_out_bytes_count", "net_in_packets", "net_in_packets_count", "net_out_packets",
        "net_out_packets_count", "net_in_dropped", "net_in_dropped_count", "net_out_dropped",
        "net_out_dropped_count", "net_in_errors", "net_in_errors_count", "net_out_errors",
        "net_out_errors_count"));
    NAMESPACE_INFO.put(COMPUTE_NAMESPACE, COMPUTE_NAMESPACE_INFO);

    // Volume
    NAMESPACE_SUPPORTED_DIMENSIONS.put(VOLUME_NAMESPACE,
        Arrays.asList("instance_uuid", "instance_id", "az", "disk"));
    NAMESPACE_REQUIRED_DIMENSIONS.put(VOLUME_NAMESPACE,
        Arrays.asList("instance_id", "instance_uuid", "az"));
    NAMESPACE_METRICS.put(VOLUME_NAMESPACE, Arrays.asList("volume_read_ops", "volume_write_ops",
        "volume_read_bytes", "volume_write_bytes", "volume_read_time", "volume_write_time",
        "volume_idle_time"));
    NAMESPACE_INFO.put(VOLUME_NAMESPACE, VOLUME_NAMESPACE_INFO);

    // Object Store
    // Commented out to loosen that restrictions for swift (until we re-write all of this code)
    // NAMESPACE_SUPPORTED_DIMENSIONS.put(OBJECT_STORE_NAMESPACE, Arrays.asList("container"));
    // NAMESPACE_REQUIRED_DIMENSIONS.put(OBJECT_STORE_NAMESPACE, Arrays.asList("container"));
    NAMESPACE_METRICS.put(OBJECT_STORE_NAMESPACE,
        Arrays.asList("project_write_bytes", "project_read_bytes", "project_put_ops",
            "project_get_ops", "container_write_bytes", "container_read_bytes",
            "container_put_ops", "container_get_ops", "container_write_bytes_proxy",
            "container_read_bytes_proxy", "container_put_ops_proxy", "container_get_ops_proxy",
            "project_bytes_used", "container_bytes_used", "number_of_objects",
            "number_of_containers", "projects_bytes_used_replica", "container_bytes_used_replica",
            "number_of_objects_replica", "number_of_containers_replica"));
    NAMESPACE_INFO.put(OBJECT_STORE_NAMESPACE, OBJECT_STORE_NAMESPACE_INFO);
  }

  /**
   * Provides information for a namespace.
   */
  private interface NamespaceInfo {
    /**
     * Returns the required dimensions for the {@code serviceVersion}.
     */
    List<String> getRequiredDimensions(String serviceVersion);

    /**
     * Returns the dimension name that represents the resource id for the {@code serviceVersion},
     * else {@code null}.
     */
    String getResourceIdDimension(String serviceVersion);

    /**
     * Returns the dimension name that represents the secondary resource id for the
     * {@code serviceVersion}, else {@code null}.
     */
    String getSecondaryResourceIdDimension(String serviceVersion);

    /**
     * Returns whether the {@code dimensionValue} is valid for the {@code dimensionName}.
     */
    boolean isValid(String dimensionName, String dimensionValue);
  }

  /**
   * Returns the required dimensions for the {@code namespace} and {@code serviceVersion}, else
   * empty list.
   */
  public static List<String> getRequiredDimensions(String namespace, String serviceVersion) {
    NamespaceInfo info = NAMESPACE_INFO.get(namespace);
    if (info != null)
      return info.getRequiredDimensions(serviceVersion);
    return Collections.emptyList();
  }

  /**
   * Returns the dimension name that represents the resource id for the {@code namespace}, else
   * {@code null}.
   */
  public static String getResourceIdDimension(String namespace, String serviceVersion) {
    NamespaceInfo info = NAMESPACE_INFO.get(namespace);
    if (info != null)
      return info.getResourceIdDimension(serviceVersion);
    return null;
  }

  /**
   * Returns the dimension name that represents the secondary resource id for the {@code namespace},
   * else {@code null}.
   */
  public static String getSecondaryResourceIdDimension(String namespace, String serviceVersion) {
    NamespaceInfo info = NAMESPACE_INFO.get(namespace);
    if (info != null)
      return info.getSecondaryResourceIdDimension(serviceVersion);
    return null;
  }

  /**
   * Returns whether the {@code namespace} is "core", which should correspond to metrics containing
   * an instance_id dimension.
   */
  public static boolean isCore(String namespace) {
    return NAMESPACE_METRICS.get(namespace) != null;
  }

  /**
   * Returns whether the {@code namespace} is reserved (hpcs).
   */
  public static boolean isReserved(String namespace) {
    return namespace.toLowerCase().startsWith(RESERVED_NAMESPACE_PREFIX);
  }

  /**
   * Returns whether the {@code dimensionName} is supported for the {@code namespace}.
   */
  public static boolean isValidDimensionName(String namespace, String dimensionName) {
    List<String> list = NAMESPACE_SUPPORTED_DIMENSIONS.get(namespace);
    return list == null || list.isEmpty() || list.contains(dimensionName)
        || dimensionName.equals("metric_name") || dimensionName.equals("device");
  }

  /**
   * Returns whether the {@code dimensionValue} is valid for the {@code namespace} and
   * {@code dimensionName}.
   */
  public static boolean isValidDimensionValue(String namespace, String dimensionName,
      String dimensionValue) {
    NamespaceInfo info = NAMESPACE_INFO.get(namespace);
    if (info != null)
      return info.isValid(dimensionName, dimensionValue);
    return true;
  }

  // TODO remove this when 1.0 API is removed
  public static void putDimensionForSubject(String namespace, String subject,
      Map<String, String> dimensions) {
    if (COMPUTE_NAMESPACE.equals(namespace))
      dimensions.put("device", subject);
    else if (VOLUME_NAMESPACE.equals(namespace))
      dimensions.put("disk", subject);
    else
      dimensions.put("subject", subject);
  }

  /**
   * Returns whether the {@code metricName} is supported for the {@code namespace}.
   */
  public static boolean isValidMetricname(String namespace, String metricName) {
    List<String> list = NAMESPACE_METRICS.get(namespace);
    return list == null || list.isEmpty() || list.contains(metricName);
  }
}
