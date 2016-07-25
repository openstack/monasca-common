/*
 * Copyright 2015 FUJITSU LIMITED
 * (C) Copyright 2016 Hewlett Packard Enterprise Development LP
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

import monasca.common.model.alarm.AlarmState;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "sub_alarm")
@NamedQueries({
    @NamedQuery(
        name = SubAlarmDb.Queries.BY_ALARMDEFINITION_ID,
        query = "select sa from SubAlarmDb as sa, AlarmDb as a where sa.alarm.id=a.id and a.alarmDefinition.id = :id"
    ),
    @NamedQuery(
        name = SubAlarmDb.Queries.BY_ALARM_ID,
        query = "from SubAlarmDb where alarm_id = :id"
    ),
    @NamedQuery(
        name = SubAlarmDb.Queries.UPDATE_EXPRESSION_BY_SUBEXPRESSION_ID,
        query = "update SubAlarmDb set expression=:expression where subExpression.id=:alarmSubExpressionId"
    )
})
public class SubAlarmDb
    extends AbstractAuditablePersistable<String> {
  private static final long serialVersionUID = 5719744905744636511L;
  private static final String DEFAULT_EXPRESSION = "";

  @JoinColumn(name = "alarm_id", nullable = false)
  @ManyToOne(cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private AlarmDb alarm;

  @JoinColumn(name = "sub_expression_id")
  @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private SubAlarmDefinitionDb subExpression;

  @Lob
  @Type(type = "text")
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "expression", nullable = false, length = 16777215)
  private String expression = DEFAULT_EXPRESSION;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private AlarmState state;

  public SubAlarmDb() {
    super();
  }

  /** This constructor will be deleted once the API tests have changed to no longer use it */
  public SubAlarmDb(String id,
                    AlarmDb alarm,
                    String expression,
                    DateTime created_at,
                    DateTime updated_at) {
    this(id, alarm, null, expression, AlarmState.OK, created_at, updated_at);
  }

  public SubAlarmDb(String id,
                    AlarmDb alarm,
                    String expression,
                    AlarmState state,
                    DateTime created_at,
                    DateTime updated_at) {
    this(id, alarm, null, expression, state, created_at, updated_at);
  }

  /** This constructor will be deleted once the API tests have changed to no longer use it */
  public SubAlarmDb(String id,
                    AlarmDb alarm,
                    SubAlarmDefinitionDb subExpression,
                    String expression,
                    DateTime created_at,
                    DateTime updated_at) {
    this(id, alarm, subExpression, expression, AlarmState.OK, created_at, updated_at);
  }

  public SubAlarmDb(String id,
                    AlarmDb alarm,
                    SubAlarmDefinitionDb subExpression,
                    String expression,
                    AlarmState state,
                    DateTime created_at,
                    DateTime updated_at) {
    super(id, created_at, updated_at);
    this.alarm = alarm;
    this.subExpression = subExpression;
    this.expression = expression;
    this.state = state;
  }

  public SubAlarmDb setExpression(final String expression) {
    this.expression = expression;
    return this;
  }

  public SubAlarmDb setSubExpression(final SubAlarmDefinitionDb subExpression) {
    this.subExpression = subExpression;
    return this;
  }

  public SubAlarmDb setAlarm(final AlarmDb alarm) {
    if (alarm != null) {
      if (!alarm.hasSubAlarm(this)) {
        alarm.addSubAlarm(this);
      }
      this.alarm = alarm;
    }
    return this;
  }

  public AlarmDb getAlarm() {
    return this.alarm;
  }

  public SubAlarmDefinitionDb getSubExpression() {
    return this.subExpression;
  }

  public String getExpression() {
    return this.expression;
  }

  public AlarmState getState() {
    return state;
  }

  public SubAlarmDb setState(AlarmState state) {
    this.state = state;
    return this;
  }

  public interface Queries {
    String BY_ALARMDEFINITION_ID = "SubAlarm.byAlarmDefinitionId";
    String BY_ALARM_ID = "SubAlarm.byAlarmId";
    String UPDATE_EXPRESSION_BY_SUBEXPRESSION_ID = "SubAlarm.updateExpressionBySubexpressionId";
  }
}
