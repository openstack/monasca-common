package com.hpcloud.dropwizard;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.logging.LoggingFactory;

import java.util.Map;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;

import org.testng.annotations.BeforeMethod;

import com.beust.jcommander.internal.Maps;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;

/**
 * Support class for testing resources.
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractResourceTest {
  private final Set<Object> singletons = Sets.newHashSet();
  private final Set<Class<?>> providers = Sets.newHashSet();
  private final Map<String, Boolean> features = Maps.newHashMap();
  private final Map<String, Object> properties = Maps.newHashMap();
  private final ObjectMapper mapper = Jackson.newObjectMapper();
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
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

  public ObjectMapper getObjectMapper() {
    return mapper;
  }

  public Validator getValidator() {
    return validator;
  }

  @BeforeMethod
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
        final DropwizardResourceConfig config = DropwizardResourceConfig.forTesting(new MetricRegistry());
        for (Class<?> provider : providers)
          config.getClasses().add(provider);
        for (Map.Entry<String, Boolean> feature : features.entrySet())
          config.getFeatures().put(feature.getKey(), feature.getValue());
        for (Map.Entry<String, Object> property : properties.entrySet())
          config.getProperties().put(property.getKey(), property.getValue());
        config.getSingletons().add(new JacksonMessageBodyProvider(mapper, validator));
        config.getSingletons().addAll(singletons);
        return new LowLevelAppDescriptor.Builder(config).build();
      }
    };

    test.setUp();
  }

  protected abstract void setupResources() throws Exception;
}