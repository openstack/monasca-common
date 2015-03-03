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

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import monasca.common.util.Exceptions;

/**
 * Utilities for working with Metrics.
 */
public final class Metrics {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    SimpleModule module = new SimpleModule();
    module.addSerializer(new MetricSerializer());
    OBJECT_MAPPER.registerModule(module);
  }

  /** Metric serializer */
  private static class MetricSerializer extends JsonSerializer<Metric> {
    @Override
    public Class<Metric> handledType() {
      return Metric.class;
    }

    public void serialize(Metric value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeStartObject();

      jgen.writeStringField("name", value.name);
      if (value.dimensions != null && !value.dimensions.isEmpty()) {
        jgen.writeObjectField("dimensions", value.dimensions);
      }
      jgen.writeNumberField("timestamp", value.timestamp);

      jgen.writeNumberField("value", value.value);
      if (value.valueMeta != null && !value.valueMeta.isEmpty()) {
        jgen.writeObjectField("value_meta", value.valueMeta);
      }
      jgen.writeEndObject();
    }
  }

  private Metrics() {
  }

  /**
   * Returns the Metric for the {@code metricJson}.
   * 
   * @throws RuntimeException if an error occurs while parsing {@code metricJson}
   */
  public static Metric fromJson(byte[] metricJson) {
    try {
      String jsonStr = StringEscapeUtils.unescapeJava(new String(metricJson, "UTF-8"));
      return OBJECT_MAPPER.readValue(jsonStr, Metric.class);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to parse metric json: %s", new String(metricJson));
    }
  }

  /**
   * Returns the JSON representation of the {@code metric} else null if it could not be converted to
   * JSON.
   */
  public static String toJson(Metric metric) {
    try {
      return OBJECT_MAPPER.writeValueAsString(metric);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
