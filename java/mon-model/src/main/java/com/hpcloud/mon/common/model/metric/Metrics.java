package com.hpcloud.mon.common.model.metric;

import java.util.Map;

/**
 * Utilities for working with Metrics.
 * 
 * @author Jonathan Halterman
 */
public final class Metrics {
  private Metrics() {
  }

  /**
   * Returns a metric for the {@code flatMetric} and {@code dimensions}.
   */
  public static Metric of(FlatMetric flatMetric, Map<String, String> dimensions) {
    return flatMetric.timeValues == null ? new Metric(new MetricDefinition(flatMetric.namespace,
        dimensions), flatMetric.timestamp, flatMetric.value) : new Metric(new MetricDefinition(
        flatMetric.namespace, dimensions), flatMetric.timestamp, flatMetric.timeValues);
  }
}
