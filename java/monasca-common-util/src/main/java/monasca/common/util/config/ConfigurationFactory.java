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
package monasca.common.util.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;
import monasca.common.util.validation.Validator;

/**
 * Adapted from Dropwizard.
 */
public class ConfigurationFactory<T> {
  private final Class<T> configType;
  private final ObjectMapper mapper;

  private ConfigurationFactory(Class<T> configType) {
    this.configType = configType;
    this.mapper = new ObjectMapper(new YAMLFactory());
    this.mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  public static <T> ConfigurationFactory<T> forClass(Class<T> configType) {
    return new ConfigurationFactory<T>(configType);
  }

  public T build(File file) throws IOException, ConfigurationException {
    final JsonNode node = mapper.readTree(file);
    final String filename = file.toString();
    return build(node, filename);
  }

  private T build(JsonNode node, String filename) throws IOException, ConfigurationException {
    T config = mapper.readValue(new TreeTraversingParser(node), configType);
    validate(filename, config);
    return config;
  }

  private void validate(String file, T config) throws ConfigurationException {
    final ImmutableList<String> errors = new Validator().validate(config);
    if (!errors.isEmpty()) {
      throw new ConfigurationException(file, errors);
    }
  }
}
