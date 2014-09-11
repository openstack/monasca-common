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

/**
 * Represents an alarm definition having been updated.
 */
@JsonRootName(value = "alarm-definition-updated")
public class AlarmDefinitionUpdatedEvent implements Serializable {
  private static final long serialVersionUID = -8687756708588262533L;

  public String tenantId;
  public String alarmDefinitionId;
  public String alarmName;
  public String alarmDescription;
  public String alarmExpression;
  public String severity;
  public List<String> matchBy;
  public boolean alarmActionsEnabled;
  /** Sub expressions which have been removed from the updated alarm expression. */
  public Map<String, AlarmSubExpression> oldAlarmSubExpressions;
  /** Sub expressions which have had their operator or threshold changed. */
  public Map<String, AlarmSubExpression> changedSubExpressions;
  /** Sub expressions which have not changed. */
  public Map<String, AlarmSubExpression> unchangedSubExpressions;
  /** Sub expressions which have been added to the updated alarm expression. */
  public Map<String, AlarmSubExpression> newAlarmSubExpressions;

  public AlarmDefinitionUpdatedEvent() {}

  public AlarmDefinitionUpdatedEvent(String tenantId, String alarmDefinitionId, String alarmName,
      String alarmDescription, String alarmExpression, List<String> matchBy,
      boolean alarmActionsEnabled, String severity,
      Map<String, AlarmSubExpression> oldAlarmSubExpressions,
      Map<String, AlarmSubExpression> changedSubExpressions,
      Map<String, AlarmSubExpression> unchangedSubExpressions,
      Map<String, AlarmSubExpression> newAlarmSubExpressions) {
    this.tenantId = tenantId;
    this.alarmDefinitionId = alarmDefinitionId;
    this.alarmName = alarmName;
    this.alarmDescription = alarmDescription;
    this.severity = severity;
    this.matchBy = matchBy;
    this.alarmExpression = alarmExpression;
    this.alarmActionsEnabled = alarmActionsEnabled;
    this.oldAlarmSubExpressions = oldAlarmSubExpressions;
    this.changedSubExpressions = changedSubExpressions;
    this.unchangedSubExpressions = unchangedSubExpressions;
    this.newAlarmSubExpressions = newAlarmSubExpressions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmDefinitionUpdatedEvent))
      return false;
    AlarmDefinitionUpdatedEvent other = (AlarmDefinitionUpdatedEvent) obj;
    if (alarmActionsEnabled != other.alarmActionsEnabled)
      return false;
    if (alarmDefinitionId == null) {
      if (other.alarmDefinitionId != null)
        return false;
    } else if (!alarmDefinitionId.equals(other.alarmDefinitionId))
      return false;
    if (alarmDescription == null) {
      if (other.alarmDescription != null)
        return false;
    } else if (!alarmDescription.equals(other.alarmDescription))
      return false;
    if (severity == null) {
      if (other.severity != null)
        return false;
    } else if (!severity.equals(other.severity))
      return false;
    if (alarmExpression == null) {
      if (other.alarmExpression != null)
        return false;
    } else if (!alarmExpression.equals(other.alarmExpression))
      return false;
    if (alarmName == null) {
      if (other.alarmName != null)
        return false;
    } else if (!alarmName.equals(other.alarmName))
      return false;
    if (changedSubExpressions == null) {
      if (other.changedSubExpressions != null)
        return false;
    } else if (!changedSubExpressions.equals(other.changedSubExpressions))
      return false;
    if (matchBy == null) {
      if (other.matchBy != null)
        return false;
    } else if (!matchBy.equals(other.matchBy))
      return false;
    if (newAlarmSubExpressions == null) {
      if (other.newAlarmSubExpressions != null)
        return false;
    } else if (!newAlarmSubExpressions.equals(other.newAlarmSubExpressions))
      return false;
    if (oldAlarmSubExpressions == null) {
      if (other.oldAlarmSubExpressions != null)
        return false;
    } else if (!oldAlarmSubExpressions.equals(other.oldAlarmSubExpressions))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    if (unchangedSubExpressions == null) {
      if (other.unchangedSubExpressions != null)
        return false;
    } else if (!unchangedSubExpressions.equals(other.unchangedSubExpressions))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (alarmActionsEnabled ? 1231 : 1237);
    result = prime * result + ((alarmDefinitionId == null) ? 0 : alarmDefinitionId.hashCode());
    result = prime * result + ((alarmDescription == null) ? 0 : alarmDescription.hashCode());
    result = prime * result + ((severity == null) ? 0 : severity.hashCode());
    result = prime * result + ((alarmExpression == null) ? 0 : alarmExpression.hashCode());
    result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
    result =
        prime * result + ((changedSubExpressions == null) ? 0 : changedSubExpressions.hashCode());
    result = prime * result + ((matchBy == null) ? 0 : matchBy.hashCode());
    result =
        prime * result + ((newAlarmSubExpressions == null) ? 0 : newAlarmSubExpressions.hashCode());
    result =
        prime * result + ((oldAlarmSubExpressions == null) ? 0 : oldAlarmSubExpressions.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    result =
        prime * result
            + ((unchangedSubExpressions == null) ? 0 : unchangedSubExpressions.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "AlarmDefinitionUpdatedEvent [tenantId=" + tenantId + ", alarmDefinitionId="
        + alarmDefinitionId + ", alarmName=" + alarmName + ", alarmDescription=" + alarmDescription
        + ", alarmExpression=" + alarmExpression + ", alarmActionsEnabled=" + alarmActionsEnabled
        + ", severity=" + severity
        + ", oldAlarmSubExpressions=" + oldAlarmSubExpressions + ", changedSubExpressions="
        + changedSubExpressions + ", unchangedSubExpressions=" + unchangedSubExpressions
        + ", newAlarmSubExpressions=" + newAlarmSubExpressions + ", matchBy=" + matchBy + "]";
  }
}
