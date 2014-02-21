package com.hpcloud.mon.common.model;

import java.util.List;
import java.util.Set;

/**
 * Provides information for a namespace.
 */
interface NamespaceInfo {
  /**
   * Returns the required dimensions.
   */
  List<String> getRequiredDimensions();

  /**
   * Returns the dimension name that represents the resource id, else {@code null}.
   */
  String getResourceIdDimension();

  /**
   * Returns the supported dimensions.
   */
  List<String> getSupportedDimensions();

  /**
   * Returns the supported metrics.
   */
  Set<String> getSupportedMetrics();
}