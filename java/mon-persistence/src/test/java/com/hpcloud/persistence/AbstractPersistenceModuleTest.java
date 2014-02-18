package com.hpcloud.persistence;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.skife.jdbi.v2.DBI;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.hpcloud.util.Injector;

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
