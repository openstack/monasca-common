/*
 * Copyright (c) 2014,2016 Hewlett Packard Enterprise Development Company, L.P.
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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import monasca.common.model.alarm.AlarmState;
import monasca.common.model.alarm.AlarmTransitionSubAlarm;
import monasca.common.model.metric.MetricDefinition;

/**
 * Represents an alarm state transition having occurred.
 */
@JsonRootName(value = "alarm-transitioned")
public class AlarmStateTransitionedEvent {
  public String tenantId;
  public String alarmId;
  public String alarmDefinitionId;
  public List<MetricDefinition> metrics;
  public String alarmName;
  public String alarmDescription;
  public AlarmState oldState;
  public AlarmState newState;
  public boolean actionsEnabled;
  public String stateChangeReason;
  public String severity;
  public String link;
  public String lifecycleState;
  public List<AlarmTransitionSubAlarm> subAlarms;
  /** POSIX timestamp */
  public long timestamp;

  public AlarmStateTransitionedEvent() {}

  public AlarmStateTransitionedEvent(String tenantId, String alarmId, String alarmDefinitionId,
      List<MetricDefinition> metrics, String alarmName, String alarmDescription,
      AlarmState oldState, AlarmState newState, String severity, String link, String lifecycleState,
      boolean actionsEnabled, String stateChangeReason, List<AlarmTransitionSubAlarm> subAlarms,
      long timestamp) {
    this.tenantId = tenantId;
    this.alarmId = alarmId;
    this.alarmDefinitionId = alarmDefinitionId;
    this.metrics = metrics;
    this.alarmName = alarmName;
    this.alarmDescription = alarmDescription;
    this.oldState = oldState;
    this.newState = newState;
    this.severity = severity;
    this.link = link;
    this.lifecycleState = lifecycleState;
    this.actionsEnabled = actionsEnabled;
    this.stateChangeReason = stateChangeReason;
    this.subAlarms = subAlarms;
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmStateTransitionedEvent))
      return false;
    AlarmStateTransitionedEvent other = (AlarmStateTransitionedEvent) obj;
    if (actionsEnabled != other.actionsEnabled)
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
    if (severity == null) {
      if (other.severity != null)
        return false;
    } else if (!severity.equals(other.severity))
      return false;
    if (link == null) {
      if (other.link != null)
        return false;
    } else if (!link.equals(other.link))
      return false;
    if (lifecycleState == null) {
      if (other.lifecycleState != null)
        return false;
    } else if (!lifecycleState.equals(other.lifecycleState))
      return false;
    if (subAlarms == null) {
      if (other.subAlarms != null)
        return false;
    } else if (!subAlarms.equals(other.subAlarms))
      return false;
    if (metrics == null) {
      if (other.metrics != null)
        return false;
    } else if (!metrics.equals(other.metrics))
      return false;
    if (newState != other.newState)
      return false;
    if (oldState != other.oldState)
      return false;
    if (stateChangeReason == null) {
      if (other.stateChangeReason != null)
        return false;
    } else if (!stateChangeReason.equals(other.stateChangeReason))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    if (timestamp != other.timestamp)
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (actionsEnabled ? 1231 : 1237);
    result = prime * result + ((alarmDefinitionId == null) ? 0 : alarmDefinitionId.hashCode());
    result = prime * result + ((alarmDescription == null) ? 0 : alarmDescription.hashCode());
    result = prime * result + ((severity == null) ? 0 : severity.hashCode());
    result = prime * result + ((link == null) ? 0 : link.hashCode());
    result = prime * result + ((lifecycleState == null) ? 0 : lifecycleState.hashCode());
    result = prime * result + ((alarmId == null) ? 0 : alarmId.hashCode());
    result = prime * result + ((alarmName == null) ? 0 : alarmName.hashCode());
    result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
    result = prime * result + ((newState == null) ? 0 : newState.hashCode());
    result = prime * result + ((oldState == null) ? 0 : oldState.hashCode());
    result = prime * result + ((stateChangeReason == null) ? 0 : stateChangeReason.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    result = prime * result + ((subAlarms == null) ? 0 : subAlarms.hashCode());
    result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "AlarmStateTransitionedEvent [tenantId=" + tenantId + ", alarmId=" + alarmId
        + ", alarmDefinitionId=" + alarmDefinitionId + ", metrics=" + metrics + ", alarmName="
        + alarmName + ", alarmDescription=" + alarmDescription + ", oldState=" + oldState
        + ", newState=" + newState + ", severity=" + severity + ", link=" + link
        + ", lifecycleState=" + lifecycleState + ", actionsEnabled=" + actionsEnabled + ", stateChangeReason="
        + stateChangeReason + ", subAlarms=" + subAlarms +  ", timestamp=" + timestamp + "]";
  }
}
