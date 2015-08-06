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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import monasca.common.hibernate.type.BinaryId;

@Entity
@Table(
    name = "metric_definition_dimensions",
    indexes = {
        @Index(name = "metric_definition_id", columnList = "metric_definition_id"),
        @Index(name = "metric_dimension_set_id", columnList = "metric_dimension_set_id")
    }
)
public class MetricDefinitionDimensionsDb
    extends AbstractUUIDPersistable {
  private static final long serialVersionUID = -4902748436802939703L;

  @JoinColumn(name = "metric_definition_id", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(cascade = {
      CascadeType.REMOVE,
      CascadeType.PERSIST,
      CascadeType.REFRESH
  }, fetch = FetchType.LAZY, optional = false)
  private MetricDefinitionDb metricDefinition;

  @Type(type = "monasca.common.hibernate.type.BinaryId")
  @Column(name = "metric_dimension_set_id", length = 20, nullable = false)
  private BinaryId metricDimensionSetId;

  public MetricDefinitionDimensionsDb() {
    super();
  }

  public MetricDefinitionDimensionsDb(final BinaryId id,
                                      final MetricDefinitionDb metricDefinition,
                                      final BinaryId metricDimensionSetId) {
    super(id);
    this.metricDefinition = metricDefinition;
    this.metricDimensionSetId = metricDimensionSetId;
  }

  public MetricDefinitionDimensionsDb(final byte[] id,
                                      final MetricDefinitionDb metricDefinition,
                                      final BinaryId metricDimensionSetId) {
    this(new BinaryId(id), metricDefinition, metricDimensionSetId);
  }

  public MetricDefinitionDimensionsDb(final byte[] id,
                                      final MetricDefinitionDb metricDefinition,
                                      final byte[] metricDimensionSetId) {
    this(new BinaryId(id), metricDefinition, new BinaryId(metricDimensionSetId));
  }

  public MetricDefinitionDb getMetricDefinition() {
    return metricDefinition;
  }

  public MetricDefinitionDimensionsDb setMetricDefinition(MetricDefinitionDb metricDefinition) {
    this.metricDefinition = metricDefinition;
    return this;
  }

  public BinaryId getMetricDimensionSetId() {
    return metricDimensionSetId;
  }

  public MetricDefinitionDimensionsDb setMetricDimensionSetId(final BinaryId metricDimensionSetId) {
    this.metricDimensionSetId = metricDimensionSetId;
    return this;
  }
}
