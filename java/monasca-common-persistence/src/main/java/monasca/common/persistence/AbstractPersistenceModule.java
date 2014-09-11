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
package monasca.common.persistence;

import org.skife.jdbi.v2.DBI;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Support module for binding persistent types.
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
