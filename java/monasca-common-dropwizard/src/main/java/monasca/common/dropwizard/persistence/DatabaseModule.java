/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.dropwizard.persistence;

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
