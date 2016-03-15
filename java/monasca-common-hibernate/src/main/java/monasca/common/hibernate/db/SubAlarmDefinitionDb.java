/*
 * Copyright 2015-2016 FUJITSU LIMITED
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.joda.time.DateTime;

import monasca.common.model.alarm.AlarmOperator;
import monasca.common.model.alarm.AlarmSubExpression;

@Entity
@Table(name = "sub_alarm_definition")
@NamedQueries({
    @NamedQuery(
        name = SubAlarmDefinitionDb.Queries.BY_ALARMDEFINITION_ID,
        query = "from SubAlarmDefinitionDb sad where sad.alarmDefinition.id = :id order by sad.id"
    ),
    @NamedQuery(
        name = SubAlarmDefinitionDb.Queries.BY_ALARMDEFINITIONDIMENSION_SUBEXPRESSION_ID,
        query = "SELECT sadd from " +
            "SubAlarmDefinitionDb sad, " +
            "SubAlarmDefinitionDimensionDb sadd " +
            "where sadd.subAlarmDefinitionDimensionId.subExpression.id = sad.id " +
            "AND sad.alarmDefinition.id = :id"
    ),
    @NamedQuery(
        name = SubAlarmDefinitionDb.Queries.DELETE_BY_IDS,
        query = "delete SubAlarmDefinitionDb where id in :ids"
    )
})
public class SubAlarmDefinitionDb
    extends AbstractAuditablePersistable<String> {
  private static final long serialVersionUID = 8898225134690206198L;

  @JoinColumn(name = "alarm_definition_id", nullable = false)
  @ManyToOne(cascade = {
      CascadeType.REMOVE,
      CascadeType.PERSIST,
      CascadeType.REFRESH
  }, fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private AlarmDefinitionDb alarmDefinition;

  @Column(name = "function", length = 10, nullable = false)
  private String function;

  @Column(name = "metric_name", length = 100)
  private String metricName;

  @Column(name = "operator", length = 5, nullable = false)
  private String operator;

  @Column(name = "threshold", nullable = false)
  private Double threshold;

  @Column(name = "period", length = 11, nullable = false)
  private Integer period;

  @Column(name = "periods", length = 11, nullable = false)
  private Integer periods;

  @Column(name = "is_deterministic", length = 1, nullable = false)
  private boolean deterministic = AlarmSubExpression.DEFAULT_DETERMINISTIC;

  public SubAlarmDefinitionDb() {
    super();
  }

  public SubAlarmDefinitionDb(String id,
                              AlarmDefinitionDb alarmDefinition,
                              String function,
                              String metricName,
                              String operator,
                              Double threshold,
                              Integer period,
                              Integer periods,
                              DateTime created_at,
                              DateTime updated_at) {
    this(id, alarmDefinition, function, metricName, operator, threshold, period, periods, created_at,
         updated_at, AlarmSubExpression.DEFAULT_DETERMINISTIC);
  }

  public SubAlarmDefinitionDb(String id,
                              AlarmDefinitionDb alarmDefinition,
                              String function,
                              String metricName,
                              String operator,
                              Double threshold,
                              Integer period,
                              Integer periods,
                              DateTime created_at,
                              DateTime updated_at,
                              boolean deterministic) {
    super(id, created_at, updated_at);
    this.alarmDefinition = alarmDefinition;
    this.function = function;
    this.metricName = metricName;
    this.operator = operator;
    this.threshold = threshold;
    this.period = period;
    this.periods = periods;
    this.deterministic = deterministic;
  }

  public SubAlarmDefinitionDb setPeriods(final Integer periods) {
    this.periods = periods;
    return this;
  }

  public SubAlarmDefinitionDb setPeriod(final Integer period) {
    this.period = period;
    return this;
  }

  public SubAlarmDefinitionDb setThreshold(final Double threshold) {
    this.threshold = threshold;
    return this;
  }

  public SubAlarmDefinitionDb setOperator(final String operator) {
    this.operator = operator;
    return this;
  }

  public SubAlarmDefinitionDb setOperator(final AlarmOperator operator) {
    return this.setOperator(operator.name().toUpperCase());
  }

  public SubAlarmDefinitionDb setMetricName(final String metricName) {
    this.metricName = metricName;
    return this;
  }

  public SubAlarmDefinitionDb setFunction(final String function) {
    this.function = function;
    return this;
  }

  public SubAlarmDefinitionDb setAlarmDefinition(final AlarmDefinitionDb alarmDefinition) {
    this.alarmDefinition = alarmDefinition;
    return this;
  }

  public AlarmDefinitionDb getAlarmDefinition() {
    return this.alarmDefinition;
  }

  public String getFunction() {
    return this.function;
  }

  public String getMetricName() {
    return this.metricName;
  }

  public String getOperator() {
    return this.operator;
  }

  public Double getThreshold() {
    return this.threshold;
  }

  public Integer getPeriod() {
    return this.period;
  }

  public Integer getPeriods() {
    return this.periods;
  }

  public boolean isDeterministic() {
    return this.deterministic;
  }

  public SubAlarmDefinitionDb setDeterministic(final boolean isDeterministic) {
    this.deterministic = isDeterministic;
    return this;
  }

  public interface Queries {
    String BY_ALARMDEFINITION_ID = "SubAlarmDefinition.byAlarmDefinitionId";
    String BY_ALARMDEFINITIONDIMENSION_SUBEXPRESSION_ID = "SubAlarmDefinition.byAlarmDefinitionDimension.subExpressionId";
    String DELETE_BY_IDS = "SubAlarmDefinition.deleteByIds";
  }
}
