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

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.joda.time.DateTime;

import monasca.common.model.alarm.AlarmState;

@Entity
@Table(name = "alarm")
@NamedQueries({
    @NamedQuery(
        name = AlarmDb.Queries.DELETE_BY_ALARMDEFINITION_ID,
        query = "delete from AlarmDb a where a.alarmDefinition.id = :alarmDefinitionId"
    ),
    @NamedQuery(
        name = AlarmDb.Queries.DELETE_BY_ID,
        query = "delete from AlarmDb a where a.id = :id"
    ),
    @NamedQuery(
        name = AlarmDb.Queries.FIND_BY_ID,
        query = "from AlarmDb a where a.id = :id"
    )
})
public class AlarmDb
    extends AbstractAuditablePersistable<String> {
  private static final long serialVersionUID = -9084263584287898881L;

  @JoinColumn(name = "alarm_definition_id", nullable = false)
  @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
  private AlarmDefinitionDb alarmDefinition;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private AlarmState state;

  @Column(name = "lifecycle_state", length = 50)
  private String lifecycleState;

  @Column(name = "link", length = 512)
  private String link;

  @Column(name = "state_updated_at")
  private Date stateUpdatedAt;

  @OneToMany(mappedBy = "alarmMetricId.alarm", fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.REFRESH,
      CascadeType.REMOVE
  })
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Collection<AlarmMetricDb> alarmMetrics;

  @OneToMany(mappedBy = "alarm", fetch = FetchType.LAZY, cascade = {
      CascadeType.PERSIST,
      CascadeType.REFRESH,
      CascadeType.REMOVE
  })
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Collection<SubAlarmDb> subAlarms;

  public AlarmDb() {
    super();
  }

  public AlarmDb(
      String id,
      AlarmDefinitionDb alarmDefinition,
      AlarmState state,
      String lifecycleState,
      String link,
      DateTime stateUpdatedAt,
      DateTime created_at,
      DateTime updated_at) {
    super(id, created_at, updated_at);
    this.setAlarmDefinition(alarmDefinition);
    this.link = link;
    this.state = state;
    this.lifecycleState = lifecycleState;
    this.setStateUpdatedAt(stateUpdatedAt);
  }

  public AlarmState getState() {
    return state;
  }

  public AlarmDb setState(AlarmState state) {
    this.state = state;
    return this;
  }

  public String getLifecycleState() {
    return lifecycleState;
  }

  public AlarmDb setLifecycleState(String lifecycleState) {
    this.lifecycleState = lifecycleState;
    return this;
  }

  public String getLink() {
    return link;
  }

  public AlarmDb setLink(String link) {
    this.link = link;
    return this;
  }

  public DateTime getStateUpdatedAt() {
    return nullSafeGetDate(this.stateUpdatedAt);
  }

  public AlarmDb setStateUpdatedAt(DateTime stateUpdatedAt) {
    this.stateUpdatedAt = nullSafeSetDate(stateUpdatedAt);
    return this;
  }

  public AlarmDefinitionDb getAlarmDefinition() {
    return alarmDefinition;
  }

  public AlarmDb setAlarmDefinition(final AlarmDefinitionDb alarmDefinition) {
    if (!alarmDefinition.hasAlarm(this)) {
      alarmDefinition.addAlarm(this);
    }
    this.alarmDefinition = alarmDefinition;
    return this;
  }

  public Collection<AlarmMetricDb> getAlarmMetrics() {
    return this.alarmMetrics != null ? this.alarmMetrics : (this.alarmMetrics = Sets.newHashSet());
  }

  public AlarmDb setAlarmMetrics(final Collection<AlarmMetricDb> alarmMetrics) {
    if (alarmMetrics == null || alarmMetrics.isEmpty()) {
      return this;
    }

    final AlarmDb self = this;
    this.alarmMetrics = Sets.newHashSetWithExpectedSize(alarmMetrics.size());

    FluentIterable.from(alarmMetrics)
        .transform(new Function<AlarmMetricDb, AlarmMetricDb>() {
          @Nullable
          @Override
          public AlarmMetricDb apply(@Nullable final AlarmMetricDb input) {
            assert input != null;
            input.setAlarm(self);
            return input;
          }
        })
        .copyInto(this.alarmMetrics);
    return this;
  }

  public AlarmDb addAlarmMetric(final AlarmMetricDb alarmMetric) {
    if (alarmMetric == null || this.hasAlarmMetric(alarmMetric)) {
      return this;
    }
    this.getAlarmMetrics().add(alarmMetric);
    alarmMetric.setAlarm(this);
    return this;
  }

  public AlarmDb removeAlarmMetric(final AlarmMetricDb alarmDb) {
    if (alarmDb == null || this.alarmMetrics == null) {
      return this;
    }
    this.alarmMetrics.remove(alarmDb);
    return this;
  }

  public boolean hasAlarmMetric(final AlarmMetricDb alarm) {
    return alarm != null && (this.alarmMetrics != null && this.alarmMetrics.contains(alarm));
  }

  public Collection<SubAlarmDb> getSubAlarms() {
    return this.subAlarms != null ? this.subAlarms : (this.subAlarms = Sets.newHashSet());
  }

  public AlarmDb setSubAlarms(final Collection<SubAlarmDb> subAlarms) {
    if (subAlarms == null || subAlarms.isEmpty()) {
      return this;
    }

    final AlarmDb self = this;
    this.subAlarms = Sets.newHashSetWithExpectedSize(subAlarms.size());

    FluentIterable.from(subAlarms)
        .transform(new Function<SubAlarmDb, SubAlarmDb>() {
          @Nullable
          @Override
          public SubAlarmDb apply(@Nullable final SubAlarmDb input) {
            assert input != null;
            input.setAlarm(self);
            return input;
          }
        })
        .copyInto(this.subAlarms);
    return this;
  }

  public AlarmDb addSubAlarm(final SubAlarmDb subAlarm) {
    if (subAlarm == null || this.hasSubAlarm(subAlarm)) {
      return this;
    }
    this.getSubAlarms().add(subAlarm);
    subAlarm.setAlarm(this);
    return this;
  }

  public AlarmDb removeSubAlarm(final SubAlarmDb subAlarm) {
    if (subAlarm == null || this.subAlarms == null) {
      return this;
    }
    this.subAlarms.remove(subAlarm);
    return this;
  }

  public boolean hasSubAlarm(final SubAlarmDb subAlarm) {
    return subAlarm != null && (this.subAlarms != null && this.subAlarms.contains(subAlarm));
  }

  public interface Queries {
    String DELETE_BY_ALARMDEFINITION_ID = "Alarm.deleteByAlarmDefinitionId";
    String DELETE_BY_ID = "Alarm.deleteById";
    String FIND_BY_ID = "Alarm.findById";
  }
}
