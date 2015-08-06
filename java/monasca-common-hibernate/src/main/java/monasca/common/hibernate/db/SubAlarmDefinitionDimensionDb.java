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
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "sub_alarm_definition_dimension")
public class SubAlarmDefinitionDimensionDb
    implements Serializable {
  private static final long serialVersionUID = -692669756593028956L;
  @EmbeddedId
  private SubAlarmDefinitionDimensionId subAlarmDefinitionDimensionId;
  @Column(name = "value", length = 255, nullable = true)
  private String value;

  public SubAlarmDefinitionDimensionDb() {
    this(null, "", null);
  }

  public SubAlarmDefinitionDimensionDb(SubAlarmDefinitionDimensionId subAlarmDefinitionDimensionId) {
    super();
    this.subAlarmDefinitionDimensionId = subAlarmDefinitionDimensionId;
  }

  public SubAlarmDefinitionDimensionDb(SubAlarmDefinitionDb subExpression,
                                       String dimension_name,
                                       String value) {
    super();
    this.subAlarmDefinitionDimensionId = new SubAlarmDefinitionDimensionId(subExpression, dimension_name);
    this.value = value;
  }

  public SubAlarmDefinitionDimensionDb(SubAlarmDefinitionDimensionId subAlarmDefinitionDimensionId, String value) {
    super();
    this.subAlarmDefinitionDimensionId = subAlarmDefinitionDimensionId;
    this.value = value;
  }

  public SubAlarmDefinitionDimensionId getSubAlarmDefinitionDimensionId() {
    return subAlarmDefinitionDimensionId;
  }

  public SubAlarmDefinitionDimensionDb setSubAlarmDefinitionDimensionId(SubAlarmDefinitionDimensionId subAlarmDefinitionDimensionId) {
    this.subAlarmDefinitionDimensionId = subAlarmDefinitionDimensionId;
    return this;
  }

  public String getDimensionName() {
    return this.requireId().getDimensionName();
  }

  public SubAlarmDefinitionDb getSubExpression() {
    return this.requireId().getSubExpression();
  }

  public SubAlarmDefinitionDimensionDb setDimensionName(final String dimensionName) {
    this.requireId().setDimensionName(dimensionName);
    return this;
  }

  public SubAlarmDefinitionDimensionDb setSubExpression(final SubAlarmDefinitionDb subExpression) {
    this.requireId().setSubExpression(subExpression);
    return this;
  }

  public String getValue() {
    return value;
  }

  public SubAlarmDefinitionDimensionDb setValue(String value) {
    this.value = value;
    return this;
  }

  private SubAlarmDefinitionDimensionId requireId() {
    if (this.subAlarmDefinitionDimensionId == null) {
      this.subAlarmDefinitionDimensionId = new SubAlarmDefinitionDimensionId();
    }
    return this.subAlarmDefinitionDimensionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SubAlarmDefinitionDimensionDb that = (SubAlarmDefinitionDimensionDb) o;

    return Objects.equal(this.subAlarmDefinitionDimensionId, that.subAlarmDefinitionDimensionId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(subAlarmDefinitionDimensionId);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("subAlarmDefinitionDimensionId", subAlarmDefinitionDimensionId)
        .add("value", value)
        .toString();
  }
}
