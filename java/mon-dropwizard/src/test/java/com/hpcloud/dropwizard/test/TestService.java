package com.hpcloud.dropwizard.test;

import static org.testng.Assert.assertEquals;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class TestService extends Application<TestConfiguration> {
  public AtomicInteger initialized = new AtomicInteger();

  public static void main(String[] args) throws Exception {
    new TestService().run(args);
  }

  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public static class TestResource {
  }

  @Override
  public String getName() {
    return "test-as-a-service";
  }

  @Override
  public void initialize(Bootstrap<TestConfiguration> bootstrap) {
  }

  @Override
  public void run(TestConfiguration config, Environment environment) throws Exception {
    environment.jersey().register(new TestResource());

    initialized.incrementAndGet();
    assertEquals(config.name, "test");
  }
}