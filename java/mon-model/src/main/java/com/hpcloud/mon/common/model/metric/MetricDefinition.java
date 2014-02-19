package com.hpcloud.mon.common.model.metric;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Metric definition.
 * 
 * @author Jonathan Halterman
 */
public class MetricDefinition implements Serializable {
  private static final long serialVersionUID = -3074228641225201445L;

  public String namespace;
  public SortedMap<String, String> dimensions;

  public MetricDefinition() {
  }

  public MetricDefinition(String namespace, @Nullable Map<String, String> dimensions) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(new TreeMap<>(dimensions));
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

  public void setDimensions(SortedMap<String, String> dimensions) {
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

  /**
   * Returns a string represents a hash of the namespace + dimensions. Convert that string to a
   * UTF-8 character encoded language neutral UUID. This encoding must be reproduceable in other
   * languages. Uses 128 bit MD5.
   * 
   * @return MD5 128 bit hash of namespace + ordered dimensions
   */
  public HashCode toHashCode() {
    StringBuilder sb = new StringBuilder(namespace);
    sb.append('=');
    if (dimensions != null) {
      boolean first = true;
      for (Map.Entry<String, String> dimension : dimensions.entrySet()) {
        if (!first) {
          sb.append(':');
          first = false;
        }
        sb.append(dimension.getKey()).append(':').append(dimension.getValue());
      }
    }
    HashFunction hf = Hashing.md5();
    HashCode hc = hf.newHasher().putString(sb.toString(), Charsets.UTF_8).hash();
    return hc;
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