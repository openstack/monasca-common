package com.hpcloud.mon.common.model.metric;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * MaaS Metric with definition information flattened alongside value information.
 * 
 * @author Jonathan Halterman
 */
public class FlatMetric {
  public String namespace;
  public SortedMap<String, String> dimensions;
  public long timestamp;
  public double value;
  public double[][] timeValues;

  public FlatMetric() {
  }

  public FlatMetric(String namespace, @Nullable SortedMap<String, String> dimensions,
      long timestamp, double value) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.value = Preconditions.checkNotNull(value, "value");
  }

  public FlatMetric(String namespace, @Nullable SortedMap<String, String> dimensions,
      long timestamp, double[][] timeValues) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.timeValues = Preconditions.checkNotNull(timeValues, "timeValues");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FlatMetric other = (FlatMetric) obj;
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
    // Note - Deep Equals is used here
    if (!Arrays.deepEquals(timeValues, other.timeValues))
      return false;
    if (timestamp != other.timestamp)
      return false;
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
    // Note Deep hash code is used here
    result = prime * result + Arrays.deepHashCode(timeValues);
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /**
   * Create a string that is unique for the namespace + dimensions combo. Convert that string to a
   * UTF-8 character encoded language neutral UUID. This encoding must be reproduceable in other
   * languages. Using 128 bit MD5.
   * 
   * @return MD5 128 bit hash of namespace + sorted dimensions
   */
  public HashCode definitionHashCode() {
    StringBuilder sb = new StringBuilder(namespace);
    sb.append('=');
    if (dimensions != null) {
      for (Map.Entry<String, String> dimension : dimensions.entrySet()) {
        sb.append(dimension.getKey()).append(':').append(dimension.getValue()).append(':');
      }
    }
    HashFunction hf = Hashing.md5();
    HashCode hc = hf.newHasher().putString(sb.toString(), Charsets.UTF_8).hash();
    return hc;
  }

  @JsonProperty
  public void setDimensions(SortedMap<String, String> dimensions) {
    if (this.dimensions == null)
      this.dimensions = dimensions;
    else
      this.dimensions.putAll(dimensions);
  }

  /** Returns a Metric for the FlatMetric. */
  public Metric toMetric() {
    MetricDefinition metricDef = new MetricDefinition(namespace, dimensions);
    return timeValues == null ? new Metric(metricDef, timestamp, value) : new Metric(metricDef,
        timestamp, timeValues);
  }

  @Override
  public String toString() {
    return String.format("FlatMetric [namespace=%s, dimensions=%s, timestamp=%s, value=%s]",
        namespace, dimensions, timestamp, timeValues == null ? value : Arrays.toString(timeValues));
  }
}