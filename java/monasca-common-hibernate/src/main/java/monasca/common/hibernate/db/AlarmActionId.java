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
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;

import monasca.common.model.alarm.AlarmState;

@Embeddable
public class AlarmActionId
    implements Serializable {
  private static final long serialVersionUID = 919758576421181247L;
  @JoinColumn(name = "alarm_definition_id", nullable = false)
  @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
  private AlarmDefinitionDb alarmDefinition;

  @Column(name = "alarm_state", nullable = false)
  @Enumerated(EnumType.STRING)
  private AlarmState alarmState;

  @Column(name = "action_id", length = 36, nullable = false)
  private String actionId;

  public AlarmActionId() {
    super();
  }

  public AlarmActionId(AlarmDefinitionDb alarmDefinition, AlarmState alarmState, String actionId) {
    super();
    this.alarmDefinition = alarmDefinition;
    this.alarmState = alarmState;
    this.actionId = actionId;
  }

  public AlarmActionId setAlarmDefinition(final AlarmDefinitionDb alarmDefinition) {
    this.alarmDefinition = alarmDefinition;
    return this;
  }

  public AlarmActionId setAlarmState(final AlarmState alarmState) {
    this.alarmState = alarmState;
    return this;
  }

  public AlarmActionId setActionId(final String actionId) {
    this.actionId = actionId;
    return this;
  }

  public AlarmDefinitionDb getAlarmDefinition() {
    return this.alarmDefinition;
  }

  public AlarmState getAlarmState() {
    return this.alarmState;
  }

  public String getActionId() {
    return this.actionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AlarmActionId that = (AlarmActionId) o;

    return Objects.equal(this.alarmDefinition, that.alarmDefinition) &&
        Objects.equal(this.alarmState, that.alarmState) &&
        Objects.equal(this.actionId, that.actionId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(alarmDefinition, alarmState, actionId);
  }
}
