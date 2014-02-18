package com.hpcloud.mon.common.model.metric;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hpcloud.util.Exceptions;

/**
 * Utilities for working with FlatMetrics.
 * 
 * @author Jonathan Halterman
 */
public final class FlatMetrics {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    SimpleModule module = new SimpleModule();
    module.addSerializer(new FlatMetricSerializer());
    OBJECT_MAPPER.registerModule(module);
  }

  /** FlatMetric serializer */
  private static class FlatMetricSerializer extends JsonSerializer<FlatMetric> {
    @Override
    public Class<FlatMetric> handledType() {
      return FlatMetric.class;
    }

    public void serialize(FlatMetric value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jgen.writeStartObject();

      jgen.writeStringField("namespace", value.namespace);
      if (value.dimensions != null && !value.dimensions.isEmpty())
        jgen.writeObjectField("dimensions", value.dimensions);
      jgen.writeNumberField("timestamp", value.timestamp);

      if (value.timeValues == null)
        jgen.writeNumberField("value", value.value);
      else {
        jgen.writeArrayFieldStart("time_values");
        for (double[] timeValue : value.timeValues) {
          jgen.writeStartArray();
          jgen.writeNumber((long) timeValue[0]); // Write timestamp as a long
          jgen.writeNumber(timeValue[1]);
          jgen.writeEndArray();
        }
        jgen.writeEndArray();
      }

      jgen.writeEndObject();
    }
  }

  private FlatMetrics() {
  }

  /**
   * Returns the FlatMetric for the {@code flatMetricJson}.
   * 
   * @throws RuntimeException if an error occurs while parsing {@code flatMetricJson}
   */
  public static FlatMetric fromJson(byte[] flatMetricJson) {
    try {
      String jsonStr = StringEscapeUtils.unescapeJava(new String(flatMetricJson, "UTF-8"));
      return OBJECT_MAPPER.readValue(jsonStr, FlatMetric.class);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to parse flat metric json: %s",
          new String(flatMetricJson));
    }
  }

  /**
   * Returns the JSON representation of the {@code flatMetric} else null if it could not be
   * converted to JSON.
   */
  public static String toJson(FlatMetric flatMetric) {
    try {
      return OBJECT_MAPPER.writeValueAsString(flatMetric);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
