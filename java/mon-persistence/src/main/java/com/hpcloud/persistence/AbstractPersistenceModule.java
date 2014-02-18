package com.hpcloud.persistence;

import org.skife.jdbi.v2.DBI;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Support module for binding persistent types.
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractPersistenceModule extends AbstractModule {
  protected <T> void bindSqlType(final Class<T> sqlType) {
    final Provider<DBI> dbProvider = getProvider(DBI.class);
    bind(sqlType).toProvider(new Provider<T>() {
      @Override
      public T get() {
        return dbProvider.get().onDemand(sqlType);
      }
    });
  }
}
