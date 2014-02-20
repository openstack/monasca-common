package com.hpcloud.mon.common.model.metric;

import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * A metric envelope.
 * 
 * @author Jonathan Halterman
 */
public class MetricEnvelope {
  public Metric metric;
  public Map<String, Object> meta;

  protected MetricEnvelope() {
  }

  public MetricEnvelope(Metric metric) {
    Preconditions.checkNotNull(metric, "metric");
    this.metric = metric;
  }

  public MetricEnvelope(Metric metric, Map<String, Object> meta) {
    Preconditions.checkNotNull(metric, "metric");
    Preconditions.checkNotNull(meta, "meta");
    this.metric = metric;
    this.meta = meta;
  }
}