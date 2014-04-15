package com.hpcloud.mon.common.model.metric;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hpcloud.util.Exceptions;

/**
 * Utilities for working with MetricEnvelopes.
 * 
 * @author Jonathan Halterman
 */
public final class MetricEnvelopes {
  private MetricEnvelopes() {
  }

  /**
   * Returns the MetricEnvelope for the {@code metricJson}.
   * 
   * @throws RuntimeException if an error occurs while parsing {@code metricJson}
   */
  public static MetricEnvelope fromJson(byte[] metricJson) {
    try {
      String jsonStr = new String(metricJson, "UTF-8");
      return Metrics.OBJECT_MAPPER.readValue(jsonStr, MetricEnvelope.class);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to parse metric json: %s", new String(metricJson));
    }
  }

  /**
   * Returns the JSON representation of the {@code envelope} else null if it could not be converted
   * to JSON.
   */
  public static String toJson(MetricEnvelope envelope) {
    try {
      return Metrics.OBJECT_MAPPER.writeValueAsString(envelope);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
