package com.hpcloud.mon.common.model.metric;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * MaaS Metric with definition information flattened alongside value information.
 * 
 * @author Jonathan Halterman
 */
public class Metric {
  public String name;
  public Map<String, String> dimensions;
  public long timestamp;
  public double value;
  public double[][] timeValues;
  private MetricDefinition definition;

  public Metric() {
  }

  public Metric(@NotNull MetricDefinition definition, long timestamp, double value) {
    this.definition = Preconditions.checkNotNull(definition, "definition");
    this.name = definition.name;
    setDimensions(definition.dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.value = Preconditions.checkNotNull(value, "value");
  }

  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp, double value) {
    this.name = Preconditions.checkNotNull(name, "name");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.value = Preconditions.checkNotNull(value, "value");
  }

  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp,
      double[][] timeValues) {
    this.name = Preconditions.checkNotNull(name, "name");
    setDimensions(dimensions);
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.timeValues = Preconditions.checkNotNull(timeValues, "timeValues");
  }

  /**
   * Returns the MetricDefinition.
   */
  public MetricDefinition definition() {
    if (definition == null)
      definition = new MetricDefinition(name, dimensions);
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
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    // Note Deep hash code is used here
    result = prime * result + Arrays.deepHashCode(timeValues);
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @JsonProperty
  public void setDimensions(Map<String, String> dimensions) {
    if (this.dimensions == null)
      this.dimensions = dimensions;
    else
      this.dimensions.putAll(dimensions);
  }

  @Override
  public String toString() {
    return String.format("Metric [name=%s, dimensions=%s, timestamp=%s, value(s)=%s]", name,
        dimensions, timestamp, timeValues == null ? value : Arrays.toString(timeValues));
  }
}