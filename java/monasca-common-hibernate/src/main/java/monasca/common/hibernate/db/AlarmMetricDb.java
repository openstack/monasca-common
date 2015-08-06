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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "alarm_metric", indexes = {
    @Index(name = "metric_definition_dimensions_id", columnList = "metric_definition_dimensions_id"),
    @Index(name = "alarm_id", columnList = "alarm_id")
})
public class AlarmMetricDb
    implements Serializable {
  private static final long serialVersionUID = 2852204906043180958L;
  @EmbeddedId
  private AlarmMetricId alarmMetricId;

  public AlarmMetricDb() {
    super();
  }

  public AlarmMetricDb(AlarmMetricId alarmMetricId) {
    this();
    this.alarmMetricId = alarmMetricId;
    this.alarmMetricId.getAlarm().addAlarmMetric(this);
  }

  public AlarmMetricDb(final AlarmDb alarm, MetricDefinitionDimensionsDb mdd) {
    this(new AlarmMetricId(alarm, mdd));
  }

  public AlarmMetricId getAlarmMetricId() {
    return alarmMetricId;
  }

  public AlarmMetricDb setAlarmMetricId(AlarmMetricId alarmMetricId) {
    this.alarmMetricId = alarmMetricId;
    return this;
  }

  public AlarmMetricDb setAlarm(final AlarmDb alarm) {
    if (alarm != null) {
      if (!alarm.hasAlarmMetric(this)) {
        alarm.addAlarmMetric(this);
      }
      this.requireAlarmMetricId().setAlarm(alarm);
    }
    return this;
  }

  public AlarmMetricDb setMetricDefinitionDimensionsId(final MetricDefinitionDimensionsDb mdd) {
    this.requireAlarmMetricId().setMetricDefinitionDimensions(mdd);
    return this;
  }

  private AlarmMetricId requireAlarmMetricId() {
    if (this.alarmMetricId == null) {
      this.alarmMetricId = new AlarmMetricId();
    }
    return this.alarmMetricId;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("alarmMetricId", alarmMetricId)
        .toString();
  }
}
