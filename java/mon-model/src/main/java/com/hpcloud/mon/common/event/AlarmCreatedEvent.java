package com.hpcloud.mon.common.event;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.hpcloud.mon.common.model.alarm.AlarmSubExpression;

/**
 * Represents an alarm having been created.
 * 
 * @author Jonathan Halterman
 */
@JsonRootName(value = "alarm-created")
public class AlarmCreatedEvent implements Serializable {
  private static final long serialVersionUID = -2971178340115415059L;

  public String tenantId;
  public String alarmId;
  public String alarmName;
  public String alarmExpression;
  public Map<String, AlarmSubExpression> alarmSubExpressions;

  public AlarmCreatedEvent() {
  }

  public AlarmCreatedEvent(String tenantId, String alarmId, String alarmName,
      String alarmExpression, Map<String, AlarmSubExpression> alarmSubExpressions) {
    this.tenantId = tenantId;
    this.alarmId = alarmId;
    this.alarmName = alarmName;
    this.alarmExpression = alarmExpression;
    this.alarmSubExpressions = alarmSubExpressions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AlarmCreatedEvent other = (AlarmCreatedEvent) obj;
    if (alarmExpression == null) {
      if (other.alarmExpression != null)
        return false;
    } else if (!alarmExpression.equals(other.alarmExpression))
      return false;
    if (alarmId == null) {
      if (other.alarmId != null)
        return false;
    } else if (!alarmId.equals(other.alarmId))
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
    result = prime * result + ((alarmExpression == null) ? 0 : alarmExpression.hashCode());
    result = prime * result + ((alarmId == null) ? 0 : alarmId.hashCode());
    result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
    result = prime * result + ((alarmSubExpressions == null) ? 0 : alarmSubExpressions.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "AlarmCreatedEvent [tenantId=%s, alarmId=%s, alarmName=%s, expression=%s]", tenantId,
        alarmId, alarmName, alarmExpression);
  }
}
