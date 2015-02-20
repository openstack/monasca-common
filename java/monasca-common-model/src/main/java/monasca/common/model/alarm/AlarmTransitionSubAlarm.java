/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.model.alarm;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a sub alarm for an alarm state transition.
 */
public class AlarmTransitionSubAlarm implements Serializable {
  private static final long serialVersionUID = 1342836348772121866L;

  public AlarmSubExpression subAlarmExpression;
  public AlarmState subAlarmState;
  public List<Double> currentValues;

  public AlarmTransitionSubAlarm() {}

  public AlarmTransitionSubAlarm(AlarmSubExpression subAlarmExpression, AlarmState subAlarmState, List<Double> currentValues) {
    this.subAlarmExpression = subAlarmExpression;
    this.subAlarmState = subAlarmState;
    this.currentValues = currentValues;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof AlarmTransitionSubAlarm))
      return false;
    AlarmTransitionSubAlarm other = (AlarmTransitionSubAlarm) obj;
    if (subAlarmState != other.subAlarmState)
      return false;
    if (subAlarmExpression == null) {
      if (other.subAlarmExpression != null)
        return false;
    } else if (!subAlarmExpression.equals(other.subAlarmExpression))
        return false;
    if (currentValues == null) {
      if (other.currentValues != null)
        return false;
      } else if (!currentValues.equals(other.currentValues))
        return false;
    return true;
  }

  @Override
  public int hashCode() {
    int result = subAlarmExpression != null ? subAlarmExpression.hashCode() : 0;
    result = 31 * result + (subAlarmState != null ? subAlarmState.hashCode() : 0);
    result = 31 * result + (currentValues != null ? currentValues.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AlarmTransitionSubAlarm [subAlarmExpression=" + subAlarmExpression + ", subAlarmState=" + subAlarmState + ", values=" + currentValues + "]";
  }
}
