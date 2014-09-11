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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.skife.jdbi.v2.DBI;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import monasca.common.util.Injector;

@Test
public class AbstractPersistenceModuleTest {
  static class FakeDAO {
  }

  FakeDAO dao = mock(FakeDAO.class);

  Module dbModule = new AbstractModule() {
    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
      DBI db = mock(DBI.class);
      when(db.onDemand(any(Class.class))).thenReturn(dao);
      bind(DBI.class).toInstance(db);
    }
  };

  Module persistenceModule = new AbstractPersistenceModule() {
    @Override
    protected void configure() {
      bindSqlType(FakeDAO.class);
    }
  };

  /**
   * Asserts that instances provided via a persistence module make use of a provided Database
   * instance.
   */
  public void shouldGetSqlType() {
    Injector.registerModules(dbModule, persistenceModule);
    assertEquals(Injector.getInstance(FakeDAO.class), dao);
  }
}
