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
import javax.persistence.Embeddable;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import monasca.common.hibernate.type.BinaryId;
import monasca.common.hibernate.type.BinaryIdType;

@Embeddable
@TypeDef(
    name = "monasca.common.hibernate.type.BinaryId",
    typeClass = BinaryIdType.class
)
public class MetricDimensionDbId
    implements Serializable {
  private static final long serialVersionUID = -594428923583460707L;

  @Type(type = "monasca.common.hibernate.type.BinaryId")
  @Column(name = "dimension_set_id", length = 20, nullable = false)
  private BinaryId dimensionSetId;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  public MetricDimensionDbId() {
  }

  public MetricDimensionDbId(final BinaryId dimensionSetId, final String name) {
    this.dimensionSetId = dimensionSetId;
    this.name = name;
  }

  public MetricDimensionDbId setDimensionSetId(final BinaryId dimensionSetId) {
    this.dimensionSetId = dimensionSetId;
    return this;
  }

  public MetricDimensionDbId setName(final String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public BinaryId getDimensionSetId() {
    return dimensionSetId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MetricDimensionDbId that = (MetricDimensionDbId) o;

    return Objects.equal(this.dimensionSetId, that.dimensionSetId) &&
        Objects.equal(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dimensionSetId, name);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("dimensionSetId", dimensionSetId)
        .add("name", name)
        .toString();
  }
}
