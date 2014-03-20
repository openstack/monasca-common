package com.hpcloud.mon.common.model;

import java.util.List;
import java.util.Set;

/**
 * Provides information for a service.
 */
interface ServiceInfo {
  /**
   * Returns the dimension name that represents the resource id, else {@code null}.
   */
  String getResourceIdDimension();

  /**
   * Returns the supported dimensions.
   */
  List<String> getSupportedDimensions();

  /**
   * Returns the supported metric names.
   */
  Set<String> getSupportedMetricNames();
}