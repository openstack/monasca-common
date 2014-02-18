package com.hpcloud.dropwizard;

import java.io.File;
import java.net.URL;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.Cli;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;

/**
 * Extend and have at it. Note, this class is in the "integration" group. So the server will only
 * start when running "integration" grouped tests.
 * 
 * @author Jonathan Halterman
 * @param <S> service type
 * @param <C> configuration type
 */
@Test(groups = "integration")
public abstract class DropwizardTestCase<S extends Service<C>, C extends Configuration> {
  protected static volatile Service<?> service;
  private static TestableServerCommand<?> command;
  private final Class<S> serviceType;
  private final Class<C> configurationType;
  private final String configPath;

  protected DropwizardTestCase(Class<S> serviceType, Class<C> configurationType, String configPath) {
    this.serviceType = serviceType;
    this.configurationType = configurationType;
    this.configPath = configPath;
  }

  @BeforeSuite
  public void startService() throws Exception {
    if (service == null) {
      synchronized (DropwizardTestCase.class) {
        if (service == null) {
          try {
            File configFile = new File(configPath);
            if (!configFile.exists()) {
              URL configURL = DropwizardTestCase.class.getResource(configPath);
              if (configURL != null)
                configFile = new File(configURL.getFile());
              if (!configFile.exists()) {
                System.err.println("Could not file config file: " + configPath);
                System.exit(1);
              }
            }

            S localService = serviceType.newInstance();
            service = localService;

            final Bootstrap<C> bootstrap = new Bootstrap<C>(localService);
            command = new TestableServerCommand<C>(localService, configurationType);
            bootstrap.addCommand(command);
            localService.initialize(bootstrap);
            final Cli cli = new Cli(this.getClass(), bootstrap);
            cli.run(new String[] { "test-server", configFile.getAbsolutePath() });
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
  }

  @AfterSuite
  public static void stopService() throws Throwable {
    if (command != null) {
      synchronized (DropwizardTestCase.class) {
        command.stop();
      }
    }
  }
}
