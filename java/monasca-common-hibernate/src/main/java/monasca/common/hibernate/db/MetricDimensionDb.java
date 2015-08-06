/*
 * Copyright 2015 FUJITSU LIMITED
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
 *
 */
package monasca.common.hibernate.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;

import monasca.common.hibernate.type.BinaryId;

@Entity
@Table(
    name = "metric_dimension",
    indexes = {
        @Index(name = "dimension_set_id", columnList = "dimension_set_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "metric_dimension_key", columnNames = {
            "dimension_set_id",
            "name"
        })
    }
)
public class MetricDimensionDb
    implements Serializable {
  private static final long serialVersionUID = 4261654453776857159L;

  @EmbeddedId
  private MetricDimensionDbId id;

  @Column(name = "value", length = 255, nullable = false)
  private String value;

  public MetricDimensionDb() {
    super();
  }

  public MetricDimensionDb(final byte[] dimensionSetId, final String name) {
    this(dimensionSetId, name, null);
  }

  public MetricDimensionDb(final byte[] dimensionSetId, final String name, final String value) {
    this(new BinaryId(dimensionSetId), name, value);
  }

  public MetricDimensionDb(final BinaryId dimensionSetId, final String name, final String value) {
    this(new MetricDimensionDbId(dimensionSetId, name), value);
  }

  public MetricDimensionDb(final MetricDimensionDbId id, final String value) {
    this.id = id;
    this.value = value;
  }

  public MetricDimensionDb setId(final MetricDimensionDbId id) {
    this.id = id;
    return this;
  }

  public MetricDimensionDbId getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public MetricDimensionDb setValue(String value) {
    this.value = value;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MetricDimensionDb that = (MetricDimensionDb) o;

    return Objects.equal(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("value", value)
        .toString();
  }
}
