package com.hpcloud.mon.common.event;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.hpcloud.mon.common.model.alarm.AlarmState;

/**
 * Represents an alarm state transition having occurred.
 * 
 * @author Jonathan Halterman
 */
@JsonRootName(value = "alarm-transitioned")
public class AlarmStateTransitionedEvent {
  public String tenantId;
  public String alarmId;
  public String alarmName;
  public String alarmDescription;
  public AlarmState oldState;
  public AlarmState newState;
  public boolean actionsEnabled;
  public String stateChangeReason;
  public long timestamp;

  public AlarmStateTransitionedEvent() {
  }

  public AlarmStateTransitionedEvent(String tenantId, String alarmId, String alarmName,
      String alarmDescription, AlarmState oldState, AlarmState newState, boolean actionsEnabled, String stateChangeReason, long timestamp) {
    this.tenantId = tenantId;
    this.alarmId = alarmId;
    this.alarmName = alarmName;
    this.alarmDescription = alarmDescription;
    this.oldState = oldState;
    this.newState = newState;
    this.actionsEnabled = actionsEnabled;
    this.stateChangeReason = stateChangeReason;
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return String.format(
        "AlarmStateTransitionedEvent [tenantId=%s, alarmId=%s, alarmName=%s, alarmDescription=%s oldState=%s, newState=%s, actionsEnabled=%s, stateChangeReason=%s, timestamp=%s]",
        tenantId, alarmId, alarmName, alarmDescription, oldState, newState, actionsEnabled, stateChangeReason, timestamp);
  }
}
