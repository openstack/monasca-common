/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.model.metric;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import monasca.common.model.domain.common.AbstractEntity;

/**
 * Metric definition.
 */
public class MetricDefinition extends AbstractEntity implements Serializable  {
  private static final long serialVersionUID = -3074228641225201445L;

  public String name;
  public Map<String, String> dimensions;

  public MetricDefinition() {
  }

  public MetricDefinition(String name, @Nullable Map<String, String> dimensions) {
    this.name = Preconditions.checkNotNull(name, "name");
    this.dimensions = dimensions;
  }

  @Override
  public String toString() {
    return "MetricDefinition{" +
           "name='" + name + '\'' +
           ", dimensions=" + dimensions +
           '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof MetricDefinition))
      return false;
    MetricDefinition other = (MetricDefinition) obj;
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
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }


  public void setId(String id) {
    this.id = id;
  }

  public void setDimensions(Map<String, String> dimensions) {
    this.dimensions = dimensions;
  }

  /**
   * Returns an expression representation of the metric definition.
   */
  public String toExpression() {
    StringBuilder b = new StringBuilder();
    b.append(name);
    if (dimensions != null && !dimensions.isEmpty())
      b.append(dimensions);
    return b.toString();
  }
}
