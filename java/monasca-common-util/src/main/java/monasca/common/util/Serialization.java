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
package monasca.common.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;

/**
 * Serialization utilities.
 * <p>
 * Notes: Deserialized Joda DateTime instances use UTC chronology. Comparisons should be made by
 * millis alone.
 */
public final class Serialization {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final InternalObjectMapper rootMapper = new InternalObjectMapper();
  static final Map<String, Class<?>> targetTypes = Maps.newHashMap();

  static {
    // Allow any visibility
    mapper.setVisibilityChecker(mapper.getVisibilityChecker().withFieldVisibility(Visibility.ANY));
    rootMapper.setVisibilityChecker(rootMapper.getVisibilityChecker().withFieldVisibility(
        Visibility.ANY));

    // Add serializers
    SimpleModule module = new SimpleModule("SerialiationModule");
    module.addSerializer(new DateTimeSerializer());
    mapper.registerModule(module);
    rootMapper.registerModule(module);

    // Allow empty beans
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    rootMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    // Allow root keys
    rootMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    rootMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

    // Allow single quotes
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    rootMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    // Allow unknown properties
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    rootMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Deserializer that delegates to the {@link Serialization} class.
   */
  public static class Deserializer extends JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      return Serialization.fromJson(jsonParser);
    }
  }

  /**
   * Serializer that delegates to the {@link Serialization} class.
   */
  public static class Serializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object object, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      Serialization.registerTarget(Types.deProxy(object.getClass()));
      jgen.writeObject(object);
    }
  }

  static class InternalObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    /** Reader for updating that supports proxies. */
    @Override
    public ObjectReader readerForUpdating(Object valueToUpdate) {
      JavaType t = _typeFactory.constructType(Types.deProxy(valueToUpdate.getClass()));
      return new InternalObjectReader(this, getDeserializationConfig(), t, valueToUpdate,
          _injectableValues);
    }
  }

  static class InternalObjectReader extends ObjectReader {
    private static final long serialVersionUID = 1L;

    protected InternalObjectReader(ObjectMapper mapper, DeserializationConfig config,
        JavaType valueType, Object valueToUpdate, InjectableValues injectableValues) {
      super(mapper, config, valueType, valueToUpdate, null, injectableValues);
    }
  }

  private Serialization() {
  }

  /**
   * Returns a fully injected instance that is constructed by the {@code Injector} with member
   * values being injected from the given {@code json}.
   * 
   * @throws IllegalArgumentException if {@code node} does not contain a single root key
   * @throws IllegalStateException if no target type has been registered for the {@code node}'s root
   *           key
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(byte[] json) {
    JsonNode node = null;

    try {
      node = mapper.readTree(json);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to deserialize json: {}", json);
    }

    return fromJson(node);
  }

  /**
   * Returns the {@code node} deserialized to an instance of <T> with the implementation of <T>
   * being selected from the registered targets for the node's root key.
   * 
   * @throws IllegalArgumentException if {@code node} does not contain a single root key
   * @throws IllegalStateException if no target type has been registered for the {@code node}'s root
   *           key
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(JsonNode node) {
    Preconditions.checkArgument(node.size() == 1, "The node must contain a single root key: %s",
        node);

    String rootKey = node.fieldNames().next();
    @SuppressWarnings("unchecked")
    Class<T> targetType = (Class<T>) targetTypes.get(rootKey);
    if (targetType == null)
      throw new IllegalStateException("No target type is registered for the root key " + rootKey);

    if (targetType.isPrimitive() || Primitives.isWrapperType(targetType)) {
      try {
        return rootMapper.reader(targetType).readValue(node);
      } catch (IOException e) {
        throw Exceptions.uncheck(e, "Failed to deserialize json: {}", node);
      }
    } else {
      T object = Injector.getInstance(targetType);
      injectMembers(object, node);
      return object;
    }
  }

  /**
   * Returns a fully injected instance that is constructed by the {@code Injector} with member
   * values being injected from the given {@code node}.
   * 
   * @throws IllegalArgumentException if {@code node} does not contain a single root key
   * @throws IllegalStateException if no target type has been registered for the {@code node}'s root
   *           key
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(JsonNode node, Class<T> targetType) {
    T object = Injector.getInstance(targetType);
    injectMembers(object, node);
    return object;
  }

  /**
   * Returns a fully injected instance that is constructed by the {@code Injector} with member
   * values being injected from the given {@code jsonParser}.
   * 
   * @throws IllegalArgumentException if {@code node} does not contain a single root key
   * @throws IllegalStateException if no target type has been registered for the {@code node}'s root
   *           key
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(JsonParser jsonParser) {
    JsonNode node = null;

    try {
      node = mapper.<JsonNode>readTree(jsonParser);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to deserialize json: {}", jsonParser);
    }

    return fromJson(node);
  }

  /**
   * Returns a fully injected instance that is constructed by the {@code Injector} with member
   * values being injected from the given {@code json}.
   * 
   * @throws IllegalArgumentException if {@code node} does not contain a single root key
   * @throws IllegalStateException if no target type has been registered for the {@code node}'s root
   *           key
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(String json) {
    JsonNode node = null;

    try {
      node = mapper.readTree(json);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to deserialize json: {}", json);
    }

    return fromJson(node);
  }

  /**
   * Returns an instance of {@code targetType} deserialized from the given {@code json}.
   * 
   * @throws RuntimeException if deserialization fails
   */
  public static <T> T fromJson(String json, Class<T> targetType) {
    try {
      return (T) rootMapper.readValue(json, targetType);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to deserialize json: {}", json);
    }
  }

  /**
   * Deserializes the given {@code json} and injects it into the fields and methods of the
   * {@code object}.
   * 
   * @throws RuntimeException if deserialization or injection fails
   */
  public static void injectMembers(Object object, JsonNode jsonNode) {
    try {
      rootMapper.readerForUpdating(object).readValue(jsonNode);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to inject members with json: {}", jsonNode);
    }
  }

  /**
   * Registers {@code targetType} as a target for deserialization where the root key name will be
   * matched by a @JsonRootName annotation, if present, else the simple class name.
   */
  public static void registerTarget(Class<?> targetType) {
    targetTypes.put(rootNameFor(targetType), targetType);
  }

  /**
   * Registers {@code targetType} as a target for deserialization where the root key name will be
   * the given {@code name}.
   */
  public static void registerTarget(String name, Class<?> targetType) {
    targetTypes.put(name, targetType);
  }

  /**
   * Returns {@code node} serialized to a json string for the {@code node}.
   * 
   * @throws RuntimeException if deserialization fails
   */
  public static String toJson(JsonNode node) {
    try {
      ObjectWriter writer = mapper.writer();
      return writer.writeValueAsString(node);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to serialize object: {}", node);
    }
  }

  /**
   * Returns {@code object} serialized to a json string.
   * 
   * @throws RuntimeException if deserialization fails
   */
  public static String toJson(Object object) {
    Class<?> unwrappedType = Types.deProxy(object.getClass());
    registerTarget(unwrappedType);

    try {
      ObjectWriter writer = rootMapper.writerWithType(unwrappedType);
      return writer.writeValueAsString(object);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to serialize object: {}", object);
    }
  }

  /**
   * Returns {@code object} serialized to a JsonNode.
   * 
   * @throws RuntimeException if deserialization fails
   */
  public static JsonNode toJsonNode(Object object) {
    try {
      ObjectNode rootNode = mapper.createObjectNode();
      JsonNode node = mapper.valueToTree(object);
      rootNode.put(rootNameFor(Types.deProxy(object.getClass())), node);
      return rootNode;
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to serialize object: {}", object);
    }
  }

  /**
   * Returns {@code json} serialized to a JsonNode.
   * 
   * @throws RuntimeException if serialization fails
   */
  public static JsonNode toJsonNode(String json) {
    try {
      return mapper.readTree(json);
    } catch (Exception e) {
      throw Exceptions.uncheck(e, "Failed to deserialize json: {}", json);
    }
  }

  private static String rootNameFor(Class<?> type) {
    JsonRootName rootName = type.getAnnotation(JsonRootName.class);
    return rootName == null ? type.getSimpleName() : rootName.value();
  }
}
