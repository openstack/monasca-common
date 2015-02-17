/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.dropwizard;

import io.dropwizard.testing.FixtureHelpers;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A set of helper methods for testing the serialization and deserialization of classes to and from
 * JSON.
 * <p>
 * For example, a test for reading and writing a {@code Person} object as JSON:
 * </p>
 * 
 * <pre><code>
 * assertThat("writing a person as JSON produces the appropriate JSON object",
 *            asJson(person),
 *            is(jsonFixture("fixtures/person.json"));
 *
 * assertThat("reading a JSON object as a person produces the appropriate person",
 *            fromJson(jsonFixture("fixtures/person.json"), Person.class),
 *            is(person));
 * </code></pre>
 */
public class JsonHelpers {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonHelpers() { /* singleton */
  }

  /**
   * Converts the given object into a canonical JSON string.
   * 
   * @param object an object
   * @return {@code object} as a JSON string
   * @throws IllegalArgumentException if there is an error encoding {@code object}
   */
  public static String asJson(Object object) throws IOException {
    return MAPPER.writeValueAsString(object);
  }

  /**
   * Converts the given JSON string into an object of the given type.
   * 
   * @param json a JSON string
   * @param klass the class of the type that {@code json} should be converted to
   * @param <T> the type that {@code json} should be converted to
   * @return {@code json} as an instance of {@code T}
   * @throws IOException if there is an error reading {@code json} as an instance of {@code T}
   */
  public static <T> T fromJson(String json, Class<T> klass) throws IOException {
    return MAPPER.readValue(json, klass);
  }

  /**
   * Converts the given JSON string into an object of the given type.
   * 
   * @param json a JSON string
   * @param reference a reference of the type that {@code json} should be converted to
   * @param <T> the type that {@code json} should be converted to
   * @return {@code json} as an instance of {@code T}
   * @throws IOException if there is an error reading {@code json} as an instance of {@code T}
   */
  public static <T> T fromJson(String json, TypeReference<T> reference) throws IOException {
    return MAPPER.readValue(json, reference);
  }

  /**
   * Loads the given fixture resource as a normalized JSON string.
   * 
   * @param filename the filename of the fixture
   * @return the contents of {@code filename} as a normalized JSON string
   * @throws IOException if there is an error parsing {@code filename}
   */
  public static String jsonFixture(String filename) throws IOException {
    return MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(filename),
        JsonNode.class));
  }
}
