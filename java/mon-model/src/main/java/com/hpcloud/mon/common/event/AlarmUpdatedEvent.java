package com.hpcloud.mon.common.event;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.hpcloud.mon.common.model.alarm.AlarmState;
import com.hpcloud.mon.common.model.alarm.AlarmSubExpression;

/**
 * Represents an alarm having been updated.
 * 
 * @author Jonathan Halterman
 */
@JsonRootName(value = "alarm-updated")
public class AlarmUpdatedEvent implements Serializable {
  private static final long serialVersionUID = -8687756708588262533L;

  public String tenantId;
  public String alarmId;
  public String alarmName;
  public String alarmExpression;
  public AlarmState alarmState;
  public boolean alarmEnabled;
  public Map<String, AlarmSubExpression> oldAlarmSubExpressions;
  public Map<String, AlarmSubExpression> changedSubExpressions;
  public Map<String, AlarmSubExpression> newAlarmSubExpressions;

  public AlarmUpdatedEvent() {
  }

  public AlarmUpdatedEvent(String tenantId, String alarmId, String alarmName,
      String alarmExpression, AlarmState alarmState, boolean alarmEnabled,
      Map<String, AlarmSubExpression> oldAlarmSubExpressions,
      Map<String, AlarmSubExpression> changedSubExpressions,
      Map<String, AlarmSubExpression> newAlarmSubExpressions) {
    this.tenantId = tenantId;
    this.alarmId = alarmId;
    this.alarmName = alarmName;
    this.alarmExpression = alarmExpression;
    this.alarmState = alarmState;
    this.alarmEnabled = alarmEnabled;
    this.oldAlarmSubExpressions = oldAlarmSubExpressions;
    this.changedSubExpressions = changedSubExpressions;
    this.newAlarmSubExpressions = newAlarmSubExpressions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AlarmUpdatedEvent other = (AlarmUpdatedEvent) obj;
    if (alarmEnabled != other.alarmEnabled)
      return false;
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
    if (alarmState != other.alarmState)
      return false;
    if (changedSubExpressions == null) {
      if (other.changedSubExpressions != null)
        return false;
    } else if (!changedSubExpressions.equals(other.changedSubExpressions))
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
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (alarmEnabled ? 1231 : 1237);
    result = prime * result + ((alarmExpression == null) ? 0 : alarmExpression.hashCode());
    result = prime * result + ((alarmId == null) ? 0 : alarmId.hashCode());
    result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
    result = prime * result + ((alarmState == null) ? 0 : alarmState.hashCode());
    result = prime * result
        + ((changedSubExpressions == null) ? 0 : changedSubExpressions.hashCode());
    result = prime * result
        + ((newAlarmSubExpressions == null) ? 0 : newAlarmSubExpressions.hashCode());
    result = prime * result
        + ((oldAlarmSubExpressions == null) ? 0 : oldAlarmSubExpressions.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "AlarmUpdatedEvent [tenantId=%s, alarmId=%s, alarmName=%s, alarmExpression=%s, alarmState=%s, alarmEnabled=%s]",
        tenantId, alarmId, alarmName, alarmExpression, alarmState, alarmEnabled);
  }
}
