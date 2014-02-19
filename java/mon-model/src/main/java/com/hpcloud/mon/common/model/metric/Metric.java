package com.hpcloud.mon.common.model.metric;

import java.util.Arrays;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * MaaS Metric with definition information flattened alongside value information.
 * 
 * @author Jonathan Halterman
 */
public class Metric {
  public String namespace;
  public SortedMap<String, String> dimensions;
  public long timestamp;
  public double value;
  public double[][] timeValues;
  private MetricDefinition definition;

  public Metric() {
  }

  public Metric(String namespace, @Nullable SortedMap<String, String> dimensions, long timestamp,
      double value) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.value = Preconditions.checkNotNull(value, "value");
  }

  public Metric(String namespace, @Nullable SortedMap<String, String> dimensions, long timestamp,
      double[][] timeValues) {
    this.namespace = Preconditions.checkNotNull(namespace, "namespace");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.timeValues = Preconditions.checkNotNull(timeValues, "timeValues");
  }

  /**
   * Returns the MetricDefinition.
   */
  public MetricDefinition definition() {
    if (definition == null)
      definition = new MetricDefinition(namespace, dimensions);
    return definition;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Metric other = (Metric) obj;
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

  @JsonProperty
  public void setDimensions(SortedMap<String, String> dimensions) {
    if (this.dimensions == null)
      this.dimensions = dimensions;
    else
      this.dimensions.putAll(dimensions);
  }

  @Override
  public String toString() {
    return String.format("Metric [namespace=%s, dimensions=%s, timestamp=%s, value(s)=%s]",
        namespace, dimensions, timestamp, timeValues == null ? value : Arrays.toString(timeValues));
  }
}