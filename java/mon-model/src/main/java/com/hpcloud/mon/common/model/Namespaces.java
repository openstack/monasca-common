package com.hpcloud.mon.common.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for working with Namespaces.
 * 
 * @author Jonathan Halterman
 */
public class Namespaces {
  // HPCS namespace constants
  public static final String COMPUTE_NAMESPACE = "hpcs.compute";
  public static final String VOLUME_NAMESPACE = "hpcs.volume";
  public static final String OBJECT_STORE_NAMESPACE = "hpcs.object-store";

  // Other constants
  public static final Map<String, NamespaceInfo> INFO;
  public static final String RESERVED_NAMESPACE_PREFIX = "hpcs.";

  static {
    INFO = new HashMap<>();
    INFO.put(COMPUTE_NAMESPACE, new ComputeNamespaceInfo());
    INFO.put(VOLUME_NAMESPACE, new VolumeNamespaceInfo());
    INFO.put(OBJECT_STORE_NAMESPACE, new ObjectStoreNamespaceInfo());
  }

  public static List<String> getRequiredDimensions(String namespace) {
    NamespaceInfo namespaceInfo = INFO.get(namespace);
    return namespaceInfo == null ? Collections.<String>emptyList()
        : namespaceInfo.getRequiredDimensions();
  }

  /**
   * Returns the dimension name that represents the resource id for the {@code namespace}, else
   * {@code null}.
   */
  public static String getResourceIdDimension(String namespace) {
    NamespaceInfo namespaceInfo = INFO.get(namespace);
    return namespaceInfo == null ? null : namespaceInfo.getResourceIdDimension();
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
    NamespaceInfo namespaceInfo = INFO.get(namespace);
    return namespaceInfo == null || namespaceInfo.getSupportedDimensions().contains(dimensionName);
  }

  /**
   * Returns whether the {@code metricName} is supported for the {@code namespace}.
   */
  public static boolean isValidMetricName(String namespace, String metricName) {
    NamespaceInfo namespaceInfo = INFO.get(namespace);
    return namespaceInfo == null || namespaceInfo.getSupportedMetrics().contains(metricName);
  }
}
