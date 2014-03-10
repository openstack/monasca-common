package com.hpcloud.mon.common.model.alarm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hpcloud.util.stats.Statistic;
import com.hpcloud.util.stats.Statistics.Average;
import com.hpcloud.util.stats.Statistics.Count;
import com.hpcloud.util.stats.Statistics.Max;
import com.hpcloud.util.stats.Statistics.Min;
import com.hpcloud.util.stats.Statistics.Sum;

/**
 * @author Jonathan Halterman
 */
public enum AggregateFunction {
  MIN, MAX, SUM, COUNT, AVG;

  @JsonCreator
  public static AggregateFunction fromJson(String text) {
    return valueOf(text.toUpperCase());
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  public Class<? extends Statistic> toStatistic() {
    if (AggregateFunction.AVG.equals(this))
      return Average.class;
    if (AggregateFunction.COUNT.equals(this))
      return Count.class;
    if (AggregateFunction.SUM.equals(this))
      return Sum.class;
    if (AggregateFunction.MIN.equals(this))
      return Min.class;
    if (AggregateFunction.MAX.equals(this))
      return Max.class;
    return null;
  }
}