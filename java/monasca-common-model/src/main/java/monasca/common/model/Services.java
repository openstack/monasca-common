/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with services.
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
