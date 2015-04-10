/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.model.metric;

import java.io.Serializable;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Map;

/**
 * Metric with definition information flattened alongside value information.
 */
public class Metric implements Serializable {
  private static final long serialVersionUID = 3455749495426525634L;

  public String name;
  public Map<String, String> dimensions;
  public long timestamp;
  public double value;
  public Map<String, String> valueMeta;
  private MetricDefinition definition;

  public Metric() {}

  public Metric(@NotNull MetricDefinition definition, long timestamp, double value,
      @Nullable Map<String, String> valueMeta) {
    this.definition = Preconditions.checkNotNull(definition, "definition");
    this.name = definition.name;
    setDimensions(definition.dimensions);
    this.timestamp = timestamp;
    this.value = value;
    this.valueMeta = valueMeta;
  }

  public Metric(String name, @Nullable Map<String, String> dimensions, long timestamp,
      double value, @Nullable Map<String, String> valueMeta) {
    this.name = Preconditions.checkNotNull(name, "name");
    setDimensions(dimensions);
    this.timestamp = timestamp;
    this.value = value;
    this.valueMeta = valueMeta;
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
  public String toString() {
    return "Metric{" +
           "name='" + name + '\'' +
           ", dimensions=" + dimensions +
           ", timestamp=" + timestamp +
           ", value=" + value +
           ", valueMeta=" + valueMeta +
           ", definition=" + definition +
           '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Metric))
      return false;
    Metric other = (Metric) obj;
    if (definition == null) {
      if (other.definition != null)
        return false;
    } else if (!definition.equals(other.definition))
      return false;
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
    if (valueMeta == null) {
      if (other.valueMeta != null)
        return false;
    } else if (!valueMeta.equals(other.valueMeta))
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
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((valueMeta == null) ? 0 : valueMeta.hashCode());
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getDimensions() {
    return dimensions;
  }

  public void setDimensions(Map<String, String> dimensions) {
    this.dimensions = dimensions;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public Map<String, String> getValueMeta() {
    return valueMeta;
  }

  public void setValueMeta(Map<String, String> valueMeta) {
    this.valueMeta = valueMeta;
  }
}
