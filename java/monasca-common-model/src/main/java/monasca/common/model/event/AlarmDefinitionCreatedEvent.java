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
 * Represents an alarm definition having been created.
 */
@JsonRootName(value = "alarm-definition-created")
public class AlarmDefinitionCreatedEvent implements Serializable {
  private static final long serialVersionUID = -2971178340115415059L;

  public String tenantId;
  public String alarmDefinitionId;
  public String alarmName;
  public String alarmDescription;
  public String alarmExpression;
  public Map<String, AlarmSubExpression> alarmSubExpressions;
  public List<String> matchBy;

  public AlarmDefinitionCreatedEvent() {}

  public AlarmDefinitionCreatedEvent(String tenantId, String alarmDefinitionId, String alarmName,
      String alarmDescription, String alarmExpression,
      Map<String, AlarmSubExpression> alarmSubExpressions, List<String> matchBy) {
    this.tenantId = tenantId;
    this.alarmDefinitionId = alarmDefinitionId;
    this.alarmName = alarmName;
    this.alarmDescription = alarmDescription;
    this.alarmExpression = alarmExpression;
    this.alarmSubExpressions = alarmSubExpressions;
    this.matchBy = matchBy;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmDefinitionCreatedEvent))
      return false;
    AlarmDefinitionCreatedEvent other = (AlarmDefinitionCreatedEvent) obj;
    if (alarmDefinitionId == null) {
      if (other.alarmDefinitionId != null)
        return false;
    } else if (!alarmDefinitionId.equals(other.alarmDefinitionId))
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
    if (alarmSubExpressions == null) {
      if (other.alarmSubExpressions != null)
        return false;
    } else if (!alarmSubExpressions.equals(other.alarmSubExpressions))
      return false;
    if (matchBy == null) {
      if (other.matchBy != null)
        return false;
    } else if (!matchBy.equals(other.matchBy))
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
    result = prime * result + ((alarmExpression == null) ? 0 : alarmExpression.hashCode());
    result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
    result = prime * result + ((alarmSubExpressions == null) ? 0 : alarmSubExpressions.hashCode());
    result = prime * result + ((matchBy == null) ? 0 : matchBy.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "AlarmDefinitionCreatedEvent [tenantId=" + tenantId + ", alarmDefinitionId="
        + alarmDefinitionId + ", alarmName=" + alarmName + ", alarmExpression=" + alarmExpression
        + ", alarmSubExpressions=" + alarmSubExpressions + ", matchBy=" + matchBy + "]";
  }
}
