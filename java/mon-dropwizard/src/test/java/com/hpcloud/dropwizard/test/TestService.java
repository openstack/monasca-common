package com.hpcloud.dropwizard.test;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class TestService extends Service<TestConfiguration> {
  public AtomicInteger initialized = new AtomicInteger();

  public static void main(String[] args) throws Exception {
    new TestService().run(args);
  }

  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public static class TestResource {
  }

  @Override
  public void initialize(Bootstrap<TestConfiguration> bootstrap) {
    bootstrap.setName("test-as-a-service");
  }

  @Override
  public void run(TestConfiguration config, Environment environment) throws Exception {
    environment.addResource(new TestResource());

    initialized.incrementAndGet();
    assertEquals(config.name, "test");
  }
}