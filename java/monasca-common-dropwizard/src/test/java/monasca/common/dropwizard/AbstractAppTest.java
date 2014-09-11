package monasca.common.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.ConfigOverride;

import java.util.Enumeration;

import net.sourceforge.argparse4j.inf.Namespace;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Support class for testing applications. Starts and stops the server once for the test suite.
 * 
 * @param <C> configuration type
 */
@Test
public abstract class AbstractAppTest<C extends Configuration> {
  private final Class<? extends Application<C>> applicationClass;
  private final String configPath;

  private C configuration;
  private Application<C> application;
  private Environment environment;
  private Server jettyServer;

  public AbstractAppTest(Class<? extends Application<C>> applicationClass, String configPath,
      ConfigOverride... configOverrides) {
    this.applicationClass = applicationClass;
    this.configPath = configPath;
    for (ConfigOverride configOverride : configOverrides) {
      configOverride.addToSystemProperties();
    }
  }

  @BeforeSuite
  protected void startServer() throws Exception {
    startIfRequired();
  }

  @AfterSuite
  protected void stopServer() throws Exception {
    resetConfigOverrides();
    jettyServer.stop();
  }

  private void resetConfigOverrides() {
    for (Enumeration<?> props = System.getProperties().propertyNames(); props.hasMoreElements();) {
      String keyString = (String) props.nextElement();
      if (keyString.startsWith("dw.")) {
        System.clearProperty(keyString);
      }
    }
  }

  private void startIfRequired() {
    if (jettyServer != null) {
      return;
    }

    try {
      application = newApplication();

      final Bootstrap<C> bootstrap = new Bootstrap<C>(application) {
        @Override
        public void run(C configuration, Environment environment) throws Exception {
          environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server) {
              jettyServer = server;
            }
          });
          AbstractAppTest.this.configuration = configuration;
          AbstractAppTest.this.environment = environment;
          super.run(configuration, environment);
        }
      };

      application.initialize(bootstrap);
      final ServerCommand<C> command = new ServerCommand<>(application);
      final Namespace namespace = new Namespace(ImmutableMap.<String, Object>of("file", configPath));
      command.run(bootstrap, namespace);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public C getConfiguration() {
    return configuration;
  }

  public int getLocalPort() {
    return ((ServerConnector) jettyServer.getConnectors()[0]).getLocalPort();
  }

  public Application<C> newApplication() {
    try {
      return applicationClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public <A extends Application<C>> A getApplication() {
    return (A) application;
  }

  public Environment getEnvironment() {
    return environment;
  }
}
