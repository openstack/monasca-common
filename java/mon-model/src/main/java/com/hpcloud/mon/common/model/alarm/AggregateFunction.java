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

  public Class<? extends Statistic> toStatistic(AggregateFunction aggregateFunction) {
    if (AggregateFunction.AVG.equals(aggregateFunction))
      return Average.class;
    if (AggregateFunction.COUNT.equals(aggregateFunction))
      return Count.class;
    if (AggregateFunction.SUM.equals(aggregateFunction))
      return Sum.class;
    if (AggregateFunction.MIN.equals(aggregateFunction))
      return Min.class;
    if (AggregateFunction.MAX.equals(aggregateFunction))
      return Max.class;
    return null;
  }
}