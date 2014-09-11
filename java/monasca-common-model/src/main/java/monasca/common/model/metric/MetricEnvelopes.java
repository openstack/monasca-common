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
package monasca.common.model.metric;

import com.fasterxml.jackson.core.JsonProcessingException;
import monasca.common.util.Exceptions;

/**
 * Utilities for working with MetricEnvelopes.
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
