package com.hpcloud.mon.common.model.alarm;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Jonathan Halterman
 */
public enum AlarmState {
  UNDETERMINED, OK, ALARM;

  @JsonCreator
  public static AlarmState fromJson(String text) {
    return valueOf(text.toUpperCase());
  }
}