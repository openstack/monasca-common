package com.hpcloud.mon.common.model.metric;

import java.io.Serializable;
import java.util.Arrays;

import com.google.common.base.Preconditions;

/**
 * MaaS Metric.
 * 
 * @author Jonathan Halterman
 */
public class Metric implements Serializable {
  private static final long serialVersionUID = 5977725053565324274L;

  public MetricDefinition definition;
  public long timestamp;
  public double value;
  public double[][] timeValues;

  public Metric() {
  }

  public Metric(MetricDefinition definition, long timestamp, double value) {
    this.definition = Preconditions.checkNotNull(definition, "definition");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    this.value = Preconditions.checkNotNull(value, "value");
  }

  public Metric(MetricDefinition definition, long timestamp, double[][] timeValues) {
    this.definition = Preconditions.checkNotNull(definition, "definition");
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
    Metric other = (Metric) obj;
    if (definition == null) {
      if (other.definition != null)
        return false;
    } else if (!definition.equals(other.definition))
      return false;
    // Note - deep equals is used here
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
    result = prime * result + ((definition == null) ? 0 : definition.hashCode());
    // Note - deep hash code is used here
    result = prime * result + Arrays.deepHashCode(timeValues);
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return String.format("Metric [definition=%s, timestamp=%s, value=%s]", definition, timestamp,
        timeValues == null ? value : Arrays.toString(timeValues));
  }
}