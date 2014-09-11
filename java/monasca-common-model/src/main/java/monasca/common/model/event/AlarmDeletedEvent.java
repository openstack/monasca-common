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
package monasca.common.model.event;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;

import monasca.common.model.alarm.AlarmSubExpression;
import monasca.common.model.metric.MetricDefinition;

/**
 * Represents an alarm having been deleted.
 */
@JsonRootName(value = "alarm-deleted")
public class AlarmDeletedEvent implements Serializable {
  private static final long serialVersionUID = -988406683520841426L;

  public String tenantId;
  public String alarmId;
  public List<MetricDefinition> alarmMetrics;
  public String alarmDefinitionId;
  public Map<String, AlarmSubExpression> subAlarms;

  public AlarmDeletedEvent() {}

  public AlarmDeletedEvent(String tenantId, String alarmId, List<MetricDefinition> alarmMetrics,
      String alarmDefinitionId, Map<String, AlarmSubExpression> subAlarms) {
    this.tenantId=tenantId;
    this.alarmId = alarmId;
    this.alarmMetrics = alarmMetrics;
    this.alarmDefinitionId = alarmDefinitionId;
    this.subAlarms = subAlarms;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmDeletedEvent))
      return false;
    AlarmDeletedEvent other = (AlarmDeletedEvent) obj;
    if (alarmDefinitionId == null) {
      if (other.alarmDefinitionId != null)
        return false;
    } else if (!alarmDefinitionId.equals(other.alarmDefinitionId))
      return false;
    if (alarmId == null) {
      if (other.alarmId != null)
        return false;
    } else if (!alarmId.equals(other.alarmId))
      return false;
    if (alarmMetrics == null) {
      if (other.alarmMetrics != null)
        return false;
    } else if (!alarmMetrics.equals(other.alarmMetrics))
      return false;
    if (subAlarms == null) {
      if (other.subAlarms != null)
        return false;
    } else if (!subAlarms.equals(other.subAlarms))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alarmDefinitionId == null) ? 0 : alarmDefinitionId.hashCode());
    result = prime * result + ((alarmId == null) ? 0 : alarmId.hashCode());
    result = prime * result + ((alarmMetrics == null) ? 0 : alarmMetrics.hashCode());
    result =
        prime * result
            + ((subAlarms == null) ? 0 : subAlarms.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "AlarmDeletedEvent [tenantId=" + tenantId + ", alarmId=" + alarmId + ", alarmMetrics="
        + alarmMetrics + ", alarmDefinitionId=" + alarmDefinitionId + ", subAlarms=" + subAlarms
        + "]";
  }
}
