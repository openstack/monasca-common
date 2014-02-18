package com.hpcloud.dropwizard;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.sourceforge.argparse4j.inf.Namespace;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.ServerFactory;
import com.yammer.dropwizard.lifecycle.ServerLifecycleListener;
import com.yammer.dropwizard.validation.Validator;

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
  private final Service<C> service;
  private final Class<C> configurationType;
  private Server server;

  public TestableServerCommand(Service<C> service, Class<C> configurationType) {
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
    Environment environment = new Environment(bootstrap.getName(), configuration,
        bootstrap.getObjectMapperFactory().copy(), new Validator());
    bootstrap.runWithBundles(configuration, environment);
    ServerFactory serverFactory = new ServerFactory(configuration.getHttpConfiguration(),
        environment.getName());
    Server server = serverFactory.buildServer(environment);

    service.run(configuration, environment);

    try {
      for (ServerLifecycleListener listener : environment.getServerListeners())
        listener.serverStarted(server);
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

  public URI getRootUriForConnector(String connectorName) {
    try {
      Connector connector = getConnectorNamed(connectorName);
      String host = connector.getHost() != null ? connector.getHost() : "localhost";
      return new URI("http://" + host + ":" + connector.getPort());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  private Connector getConnectorNamed(String name) {
    Connector[] connectors = server.getConnectors();
    for (Connector connector : connectors)
      if (connector.getName().equals(name))
        return connector;
    throw new IllegalStateException("No connector named " + name);
  }
}