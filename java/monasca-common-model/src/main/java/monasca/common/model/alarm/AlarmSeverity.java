/*
 * (C) Copyright 2014,2016 Hewlett Packard Enterprise Development Company LP
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum AlarmSeverity {
  LOW, MEDIUM, HIGH, CRITICAL;

  public static AlarmSeverity fromString(String text) {
    if (text != null) {
      for (AlarmSeverity alarmSeverity : AlarmSeverity.values()) {
        if (text.equalsIgnoreCase(alarmSeverity.toString())) {
          return alarmSeverity;
        }
      }
    }
    return null;
  }

  @JsonCreator
  public static AlarmSeverity fromJson(@JsonProperty("severity") String text) {
    return AlarmSeverity.valueOf(text.toUpperCase());
  }
}
