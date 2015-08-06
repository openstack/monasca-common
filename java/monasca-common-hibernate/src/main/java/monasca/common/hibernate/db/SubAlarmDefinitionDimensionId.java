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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Embeddable
public class SubAlarmDefinitionDimensionId
    implements Serializable {
  private static final long serialVersionUID = -233531731474459939L;

  @JoinColumn(name = "sub_alarm_definition_id", nullable = false)
  @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private SubAlarmDefinitionDb subExpression;

  @Column(name = "dimension_name", length = 255, nullable = false)
  private String dimensionName;

  public SubAlarmDefinitionDimensionId() {
    this(null, "");
  }

  public SubAlarmDefinitionDimensionId(SubAlarmDefinitionDb subExpression,
                                       String dimensionName) {
    super();
    this.subExpression = subExpression;
    this.dimensionName = dimensionName;
  }

  public SubAlarmDefinitionDimensionId setSubExpression(final SubAlarmDefinitionDb subExpression) {
    this.subExpression = subExpression;
    return this;
  }

  public SubAlarmDefinitionDimensionId setDimensionName(final String dimensionName) {
    this.dimensionName = dimensionName;
    return this;
  }

  public SubAlarmDefinitionDb getSubExpression() {
    return this.subExpression;
  }

  public String getDimensionName() {
    return this.dimensionName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SubAlarmDefinitionDimensionId that = (SubAlarmDefinitionDimensionId) o;

    return Objects.equal(this.subExpression, that.subExpression) &&
        Objects.equal(this.dimensionName, that.dimensionName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(subExpression, dimensionName);
  }
}
