package com.hpcloud.util.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;
import com.hpcloud.util.validation.Validator;

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
