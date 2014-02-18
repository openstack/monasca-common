package com.hpcloud.dropwizard.persistence;

import org.skife.jdbi.v2.DBI;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

/**
 * Module that binds DBI types to a single database instance provided for the specified environment
 * and database configuration.
 * 
 * @author Jonathan Halterman
 */
public class DatabaseModule extends AbstractModule {
  private final Environment environment;
  private final DatabaseConfiguration config;

  public DatabaseModule(Environment environment, DatabaseConfiguration config) {
    this.environment = environment;
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(DatabaseConfiguration.class).toInstance(config);
    bind(DBI.class).toProvider(new Provider<DBI>() {
      @Override
      public DBI get() {
        try {
          return new DBIFactory().build(environment, config, "platform");
        } catch (ClassNotFoundException e) {
          throw new ProvisionException("Failed to provision DBI", e);
        }
      }
    }).in(Scopes.SINGLETON);
  }
}
