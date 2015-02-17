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

import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.beust.jcommander.internal.Maps;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;

/**
 * Support class for testing resources.
 */
public abstract class AbstractResourceTest {
  private final Set<Object> singletons = Sets.newHashSet();
  private final Set<Class<?>> providers = Sets.newHashSet();
  private final Map<String, Boolean> features = Maps.newHashMap();
  private final Map<String, Object> properties = Maps.newHashMap();
  protected final ObjectMapper objectMapper = Jackson.newObjectMapper();
  protected final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  protected final MetricRegistry metricRegistry = new MetricRegistry();
  protected final Environment environment = new Environment("test", objectMapper, validator,
      metricRegistry, Thread.currentThread().getContextClassLoader());
  private JerseyTest test;

  static {
    LoggingFactory.bootstrap();
  }

  public void addFeature(String feature, Boolean value) {
    features.put(feature, value);
  }

  public void addProperty(String property, Object value) {
    properties.put(property, value);
  }

  public void addProviders(Class<?>... providers) {
    for (Class<?> provider : providers)
      this.providers.add(provider);
  }

  public void addResources(Object... resources) {
    for (Object resource : resources)
      singletons.add(resource);
  }

  public void addSingletons(Object... providers) {
    for (Object provider : providers)
      singletons.add(provider);
  }

  public Client client() {
    return test.client();
  }

  public JerseyTest getJerseyTest() {
    return test;
  }

  @AfterMethod
  protected void afterTestCase() throws Exception {
    if (test != null)
      test.tearDown();
  }

  @BeforeMethod
  protected void beforeTestCase() throws Exception {
    singletons.clear();
    providers.clear();
    features.clear();
    properties.clear();
    setupResources();

    test = new JerseyTest() {
      @Override
      protected AppDescriptor configure() {
        final DropwizardResourceConfig config = DropwizardResourceConfig.forTesting(metricRegistry);
        for (Class<?> provider : providers)
          config.getClasses().add(provider);
        for (Map.Entry<String, Boolean> feature : features.entrySet())
          config.getFeatures().put(feature.getKey(), feature.getValue());
        for (Map.Entry<String, Object> property : properties.entrySet())
          config.getProperties().put(property.getKey(), property.getValue());
        config.getSingletons().add(new JacksonMessageBodyProvider(objectMapper, validator));
        config.getSingletons().addAll(singletons);
        return new LowLevelAppDescriptor.Builder(config).build();
      }
    };

    test.setUp();
  }

  /**
   * Returns a configuration object read in from the {@code fileName}.
   */
  protected <T extends Configuration> T getConfiguration(String filename,
      Class<T> configurationClass) throws Exception {
    final ConfigurationFactory<T> configurationFactory = new ConfigurationFactory<>(
        configurationClass, validator, objectMapper, "dw");
    if (filename != null) {
      final File file = new File(Resources.getResource(filename).getFile());
      if (!file.exists())
        throw new FileNotFoundException("File " + file + " not found");
      return configurationFactory.build(file);
    }

    return configurationFactory.build();
  }

  protected abstract void setupResources() throws Exception;
}
