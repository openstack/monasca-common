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
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import monasca.common.model.alarm.AlarmSeverity;

@Entity
@Table(name = "alarm_definition", indexes = {
    @Index(name = "tenant_id", columnList = "tenant_id"),
    @Index(name = "deleted_at", columnList = "deleted_at")
})
@NamedQueries({
    @NamedQuery(
        name = AlarmDefinitionDb.Queries.FIND_BY_TENANT_AND_ID_NOT_DELETED,
        query = "from AlarmDefinitionDb ad " +
            "where ad.tenantId = :tenant_id " +
            "and ad.id = :id " +
            "and ad.deletedAt is NULL " +
            "group by ad.id"
    ),
    @NamedQuery(
        name = AlarmDefinitionDb.Queries.FIND_BY_TENANT_ID_AND_ID,
        query = "from AlarmDefinitionDb alarm_definition " +
            "where alarm_definition.tenantId=:tenantId " +
            "and alarm_definition.id= :id"
    )
})
public class AlarmDefinitionDb
    extends AbstractAuditablePersistable<String> {
  private static final long serialVersionUID = 2566210444329934008L;
  private static final String DEFAULT_MATCH_BY = "";
  private static final String DEFAULT_NAME = "";
  private static final boolean DEFAULT_ACTIONS_ENABLED = true;
  @Column(name = "tenant_id", length = 36, nullable = false)
  private String tenantId;

  @Column(name = "name", length = 255, nullable = false)
  private String name = DEFAULT_NAME;

  @Column(name = "description", length = 255)
  private String description;

  @Lob
  @Type(type = "text")
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "expression", nullable = false, length = 16777215)
  private String expression;

  @Column(name = "severity", nullable = false)
  @Enumerated(EnumType.STRING)
  private AlarmSeverity severity;

  @Column(name = "match_by", length = 255)
  private String matchBy = DEFAULT_MATCH_BY;

  @Column(name = "actions_enabled", length = 1, nullable = false)
  private boolean actionsEnabled = DEFAULT_ACTIONS_ENABLED;

  @Column(name = "deleted_at")
  private Date deletedAt;

  @BatchSize(size = 50)
  @OneToMany(mappedBy = "alarmDefinition", fetch = FetchType.LAZY)
  private Collection<AlarmDb> alarms;

  public AlarmDefinitionDb() {
    super();
  }

  public AlarmDefinitionDb(String id,
                           String tenantId,
                           String name,
                           String description,
                           String expression,
                           AlarmSeverity severity,
                           String matchBy,
                           boolean actionsEnabled,
                           DateTime created_at,
                           DateTime updated_at,
                           DateTime deletedAt) {
    super(id, created_at, updated_at);
    this.id = id;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.expression = expression;
    this.severity = severity;
    this.matchBy = matchBy;
    this.actionsEnabled = actionsEnabled;
    this.setDeletedAt(deletedAt);
  }

  public AlarmDefinitionDb(String id,
                           String tenantId,
                           String expression,
                           AlarmSeverity severity,
                           DateTime created_at,
                           DateTime updated_at) {
    this(id, tenantId, null, null, expression, severity, DEFAULT_MATCH_BY, DEFAULT_ACTIONS_ENABLED, created_at, updated_at, null);
  }

  public AlarmDefinitionDb setDeletedAt(final DateTime deletedAt) {
    this.deletedAt = nullSafeSetDate(deletedAt);
    return this;
  }

  public AlarmDefinitionDb setTenantId(final String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public AlarmDefinitionDb setName(final String name) {
    this.name = name;
    return this;
  }

  public AlarmDefinitionDb setDescription(final String description) {
    this.description = description;
    return this;
  }

  public AlarmDefinitionDb setExpression(final String expression) {
    this.expression = expression;
    return this;
  }

  public AlarmDefinitionDb setSeverity(final AlarmSeverity severity) {
    this.severity = severity;
    return this;
  }

  public AlarmDefinitionDb setMatchBy(final String matchBy) {
    this.matchBy = matchBy;
    return this;
  }

  public AlarmDefinitionDb setActionsEnabled(final boolean actionsEnabled) {
    this.actionsEnabled = actionsEnabled;
    return this;
  }

  public String getTenantId() {
    return tenantId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getExpression() {
    return expression;
  }

  public AlarmSeverity getSeverity() {
    return severity;
  }

  public String getMatchBy() {
    return matchBy;
  }

  public Collection<String> getMatchByAsCollection() {
    if (this.matchBy == null) {
      return Collections.emptyList();
    }
    return Lists.newArrayList(this.matchBy.split(","));
  }

  public boolean isActionsEnabled() {
    return actionsEnabled;
  }

  public DateTime getDeletedAt() {
    return nullSafeGetDate(this.deletedAt);
  }

  public boolean hasAlarm(final AlarmDb alarm) {
    return alarm != null && (this.alarms != null && this.alarms.contains(alarm));
  }

  public Collection<AlarmDb> getAlarms() {
    return this.alarms != null ? this.alarms : (this.alarms = Sets.newHashSet());
  }

  public AlarmDefinitionDb setAlarms(final Collection<AlarmDb> alarms) {
    final AlarmDefinitionDb self = this;
    this.alarms = Sets.newHashSetWithExpectedSize(alarms.size());
    FluentIterable.from(alarms)
        .transform(new Function<AlarmDb, AlarmDb>() {
          @Nullable
          @Override
          public AlarmDb apply(@Nullable final AlarmDb input) {
            assert input != null;
            input.setAlarmDefinition(self);
            return input;
          }
        })
        .copyInto(this.alarms);
    return this;
  }

  public AlarmDefinitionDb addAlarm(final AlarmDb alarmDb) {
    if (alarmDb == null || this.hasAlarm(alarmDb)) {
      return this;
    }
    this.getAlarms().add(alarmDb);
    alarmDb.setAlarmDefinition(this);
    return this;
  }

  public AlarmDefinitionDb removeAlarm(final AlarmDb alarmDb) {
    if (alarmDb == null || this.alarms == null) {
      return this;
    }
    this.getAlarms().remove(alarmDb);
    return this;
  }

  public interface Queries {
    String FIND_BY_TENANT_AND_ID_NOT_DELETED = "AlarmDefinition.byTenantAndIdNotDeleted";
    String FIND_BY_TENANT_ID_AND_ID = "AlarmDefinition.byTenantIdAndId";
  }

}
