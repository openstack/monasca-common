package com.hpcloud.dropwizard.persistence;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;

import org.skife.jdbi.v2.DBI;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;

/**
 * Module that binds DBI types to a single database instance provided for the specified environment
 * and database configuration.
 * 
 * @author Jonathan Halterman
 */
public class DatabaseModule extends AbstractModule {
  private final Environment environment;
  private final DataSourceFactory config;

  public DatabaseModule(Environment environment, DataSourceFactory config) {
    this.environment = environment;
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(DataSourceFactory.class).toInstance(config);
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
