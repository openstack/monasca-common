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
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Embeddable
public class AlarmMetricId
    implements Serializable {
  private static final long serialVersionUID = -7672930363327018974L;

  @JoinColumn(name = "metric_definition_dimensions_id", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(cascade = {
      CascadeType.PERSIST,
      CascadeType.REFRESH,
      CascadeType.REMOVE
  })
  private MetricDefinitionDimensionsDb metricDefinitionDimensions;

  @JoinColumn(name = "alarm_id", referencedColumnName = "id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(cascade = {
      CascadeType.PERSIST,
      CascadeType.REFRESH,
      CascadeType.REMOVE
  }, fetch = FetchType.LAZY, optional = false)
  private AlarmDb alarm;

  public AlarmMetricId() {
    this(null, null);
  }

  public AlarmMetricId(final AlarmDb alarm,
                       MetricDefinitionDimensionsDb metricDefinitionDimensionsId) {
    super();
    this.alarm = alarm;
    this.metricDefinitionDimensions = metricDefinitionDimensionsId;
  }

  public AlarmMetricId(final AlarmDb alarm) {
    this(alarm, null);
  }

  public AlarmDb getAlarm() {
    return alarm;
  }

  public AlarmMetricId setAlarm(final AlarmDb alarm) {
    this.alarm = alarm;
    return this;
  }

  public MetricDefinitionDimensionsDb getMetricDefinitionDimensions() {
    return metricDefinitionDimensions;
  }

  public AlarmMetricId setMetricDefinitionDimensions(final MetricDefinitionDimensionsDb metricDefinitionDimensions) {
    this.metricDefinitionDimensions = metricDefinitionDimensions;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AlarmMetricId that = (AlarmMetricId) o;

    return Objects.equal(this.metricDefinitionDimensions, that.metricDefinitionDimensions) &&
        Objects.equal(this.alarm, that.alarm);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(metricDefinitionDimensions, alarm);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("alarm", alarm)
        .toString();
  }
}
