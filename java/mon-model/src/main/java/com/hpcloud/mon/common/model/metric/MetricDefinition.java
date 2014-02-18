package com.hpcloud.mon.common.model.metric;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

/**
 * Metric definition.
 * 
 * @author Jonathan Halterman
 */
public class MetricDefinition implements Serializable {
  private static final long serialVersionUID = -3074228641225201445L;

  public String namespace;
  public Map<String, String> dimensions;

  public MetricDefinition() {
  }

  public MetricDefinition(String namespace, @Nullable Map<String, String> dimensions) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(dimensions);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MetricDefinition other = (MetricDefinition) obj;
    if (dimensions == null) {
      if (other.dimensions != null)
        return false;
    } else if (!dimensions.equals(other.dimensions))
      return false;
    if (namespace == null) {
      if (other.namespace != null)
        return false;
    } else if (!namespace.equals(other.namespace))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
    return result;
  }

  public void setDimensions(Map<String, String> dimensions) {
    this.dimensions = dimensions == null || dimensions.isEmpty() ? null : dimensions;
  }

  /**
   * Returns an expression representation of the metric definition.
   */
  public String toExpression() {
    StringBuilder b = new StringBuilder();
    b.append(namespace);
    if (dimensions != null)
      b.append(dimensions);
    return b.toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("MetricDefinition [").append(namespace);
    if (dimensions != null && !dimensions.isEmpty())
      sb.append(dimensions);
    return sb.append(']').toString();
  }
}