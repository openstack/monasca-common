package com.hpcloud.dropwizard;

import static com.google.common.base.Preconditions.checkArgument;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.validation.Validation;

import net.sourceforge.argparse4j.inf.Namespace;

import org.eclipse.jetty.server.Server;

/**
 * Normally ServerCommand is in charge of starting the service, but that's not particularly well
 * suited for integration testing as it joins the current thread and keeps the Server instance to
 * itself.
 * 
 * This implementation is based on the original ServerCommand, but in addition to being stoppable it
 * provides a few convenience methods for tests.
 * 
 * @author Kim A. Betti <kim@developer-b.com>
 * @author Jonathan Halterman
 */
public class TestableServerCommand<C extends Configuration> extends ConfiguredCommand<C> {
  private final Application<C> service;
  private final Class<C> configurationType;
  private Server server;

  public TestableServerCommand(Application<C> service, Class<C> configurationType) {
    super("test-server", "Starts an HTTP test-server running the service");
    this.service = service;
    this.configurationType = configurationType;
  }

  @Override
  protected Class<C> getConfigurationClass() {
    return configurationType;
  }

  @Override
  protected void run(Bootstrap<C> bootstrap, Namespace namespace, C configuration) throws Exception {
    final Environment environment = new Environment(bootstrap.getApplication().getName(),
        bootstrap.getObjectMapper(),
        Validation.buildDefaultValidatorFactory()
                  .getValidator(),
        bootstrap.getMetricRegistry(),
        bootstrap.getClassLoader());
    configuration.getMetricsFactory().configure(environment.lifecycle(),
        bootstrap.getMetricRegistry());
    bootstrap.run(configuration, environment);
    Server server = configuration.getServerFactory().build(environment);

    service.run(configuration, environment);

    try {
      server.start();
    } catch (Exception e) {
      System.out.println("Unable to start test-server, shutting down");
      e.printStackTrace();
      server.stop();
    }
  }

  public void stop() throws Exception {
    try {
      stopJetty();
    } finally {
      unRegisterLoggingMBean();
    }
  }

  /**
   * We won't be able to run more then a single test in the same JVM instance unless we do some
   * tidying and un-register a logging m-bean added by Dropwizard.
   */
  private void unRegisterLoggingMBean() throws Exception {
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    ObjectName loggerObjectName = new ObjectName("com.yammer:type=Logging");
    if (server.isRegistered(loggerObjectName))
      server.unregisterMBean(loggerObjectName);
  }

  private void stopJetty() throws Exception {
    if (server != null) {
      server.stop();
      checkArgument(server.isStopped());
    }
  }

  public boolean isRunning() {
    return server.isRunning();
  }
}