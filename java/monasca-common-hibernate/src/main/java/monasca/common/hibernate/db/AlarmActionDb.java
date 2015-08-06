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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.google.common.base.Objects;

import monasca.common.model.alarm.AlarmState;

@Entity
@Table(name = "alarm_action")
@NamedQueries({
    @NamedQuery(
        name = AlarmActionDb.Queries.DELETE_BY_ALARMDEFINITION_ID,
        query = "delete AlarmActionDb aa " +
            "where aa.alarmActionId.alarmDefinition.id = :id"
    ),
    @NamedQuery(
        name = AlarmActionDb.Queries.DELETE_BY_ALARMDEFINITION_ID_AND_ALARMSTATE,
        query = "delete AlarmActionDb aa " +
            "where aa.alarmActionId.alarmDefinition.id = :id " +
            "and aa.alarmActionId.alarmState = :alarmState"
    ),
    @NamedQuery(
        name = AlarmActionDb.Queries.FIND_BY_TENANT_ID_AND_ALARMDEFINITION_ID_DISTINCT,
        query = "select distinct aa from AlarmActionDb aa, AlarmDefinitionDb ad " +
            "where ad.id=aa.alarmActionId.alarmDefinition.id " +
            "and ad.deletedAt is null " +
            "and ad.tenantId= :tenantId " +
            "and ad.id= :alarmDefId"
    )
})
public class AlarmActionDb
    implements Serializable {
  private static final long serialVersionUID = -8138171887172601911L;
  @EmbeddedId
  private AlarmActionId alarmActionId;

  public AlarmActionDb() {
    this(null, AlarmState.UNDETERMINED, null);
  }

  public AlarmActionDb(final AlarmDefinitionDb alarmDefinition,
                       final AlarmState alarmState,
                       final String actionId) {
    super();
    this.alarmActionId = new AlarmActionId(alarmDefinition, alarmState, actionId);
  }

  public AlarmActionId getAlarmActionId() {
    return alarmActionId;
  }

  public AlarmActionDb setAlarmActionId(AlarmActionId alarmActionId) {
    this.alarmActionId = alarmActionId;
    return this;
  }

  public boolean isInAlarmState(final AlarmState state) {
    return this.alarmActionId != null && this.alarmActionId.getAlarmState().equals(state);
  }

  public AlarmActionDb setAlarmState(final AlarmState alarmState) {
    this.requireAlarmActionId().setAlarmState(alarmState);
    return this;
  }

  public AlarmActionDb setActionId(final String actionId) {
    this.requireAlarmActionId().setActionId(actionId);
    return this;
  }

  public AlarmActionDb setAlarmDefinition(final AlarmDefinitionDb alarmDefinition) {
    this.requireAlarmActionId().setAlarmDefinition(alarmDefinition);
    return this;
  }

  public AlarmState getAlarmState() {
    return this.requireAlarmActionId().getAlarmState();
  }

  public AlarmDefinitionDb getAlarmDefinition() {
    return this.requireAlarmActionId().getAlarmDefinition();
  }

  public String getActionId() {
    return this.requireAlarmActionId().getActionId();
  }

  private AlarmActionId requireAlarmActionId() {
    if (this.alarmActionId == null) {
      this.alarmActionId = new AlarmActionId();
    }
    return this.alarmActionId;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("alarmActionId", alarmActionId)
        .toString();
  }

  public interface Queries {
    String DELETE_BY_ALARMDEFINITION_ID = "AlarmAction.deleteByAlarmDefinitionId";
    String DELETE_BY_ALARMDEFINITION_ID_AND_ALARMSTATE = "AlarmAction.deleteByAlarmDefinitionIdAndAlarmState";
    String FIND_BY_TENANT_ID_AND_ALARMDEFINITION_ID_DISTINCT = "AlarmAction.findByTenantIdAndAlarmDefinitionId.Distinct";
  }
}
