package com.hpcloud.mon.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with services.
 * 
 * @author Jonathan Halterman
 */
public class Services {
  // HPCS namespace constants
  public static final String COMPUTE_SERVICE = "hpcs.compute";
  public static final String VOLUME_SERVICE = "hpcs.volume";
  public static final String OBJECT_STORE_SERVICE = "hpcs.object-store";

  // Other constants
  public static final String SERVICE_DIMENSION = "service";
  public static final Map<String, ServiceInfo> INFO;
  public static final String RESERVED_SERVICE_PREFIX = "hpcs.";

  static {
    INFO = new HashMap<>();
    INFO.put(COMPUTE_SERVICE, new ComputeServiceInfo());
    INFO.put(VOLUME_SERVICE, new VolumeServiceInfo());
    INFO.put(OBJECT_STORE_SERVICE, new ObjectStoreServiceInfo());
  }

  /**
   * Returns the dimension name that represents the resource id for the {@code service}, else
   * {@code null}.
   */
  public static String getResourceIdDimension(String service) {
    ServiceInfo serviceInfo = INFO.get(service);
    return serviceInfo == null ? null : serviceInfo.getResourceIdDimension();
  }

  /**
   * Returns whether the {@code service} is reserved (hpcs).
   */
  public static boolean isReserved(String service) {
    return service.toLowerCase().startsWith(RESERVED_SERVICE_PREFIX);
  }

  /**
   * Returns whether the {@code dimensionName} is supported for the {@code service}.
   */
  public static boolean isValidDimensionName(String service, String dimensionName) {
    ServiceInfo serviceInfo = INFO.get(service);
    return serviceInfo == null || serviceInfo.getSupportedDimensions().contains(dimensionName);
  }

  /**
   * Returns whether the {@code metricName} is supported for the {@code service}.
   */
  public static boolean isValidMetricName(String service, String metricName) {
    ServiceInfo serviceInfo = INFO.get(service);
    return serviceInfo == null || serviceInfo.getSupportedMetricNames().contains(metricName);
  }
}
