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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;
import monasca.common.model.metric.MetricDefinition;

/**
 * Represents an alarm definition having been deleted.
 */
@JsonRootName(value = "alarm-definition-deleted")
public class AlarmDefinitionDeletedEvent implements Serializable {
  private static final long serialVersionUID = -845914476456541787L;

  public String alarmDefinitionId;
  public Map<String, MetricDefinition> subAlarmMetricDefinitions;

  public AlarmDefinitionDeletedEvent() {}

  public AlarmDefinitionDeletedEvent(String alarmDefinition,
      Map<String, MetricDefinition> subAlarmMetricDefinitions) {
    this.alarmDefinitionId = alarmDefinition;
    this.subAlarmMetricDefinitions = subAlarmMetricDefinitions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmDefinitionDeletedEvent))
      return false;
    AlarmDefinitionDeletedEvent other = (AlarmDefinitionDeletedEvent) obj;
    if (alarmDefinitionId == null) {
      if (other.alarmDefinitionId != null)
        return false;
    } else if (!alarmDefinitionId.equals(other.alarmDefinitionId))
      return false;
    if (subAlarmMetricDefinitions == null) {
      if (other.subAlarmMetricDefinitions != null)
        return false;
    } else if (!subAlarmMetricDefinitions.equals(other.subAlarmMetricDefinitions))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((alarmDefinitionId == null) ? 0 : alarmDefinitionId.hashCode());
    result =
        prime * result
            + ((subAlarmMetricDefinitions == null) ? 0 : subAlarmMetricDefinitions.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format("AlarmDefinitionDeletedEvent [alarmDefinitionId=%s, subAlarmIds=%s]",
        alarmDefinitionId, subAlarmMetricDefinitions);
  }
}
