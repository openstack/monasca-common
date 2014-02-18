package com.hpcloud.persistence;

import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Produces {@link BeanMapper instances}.
 * 
 * @author Jonathan Halterman
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BeanMapperFactory implements ResultSetMapperFactory {
  @Override
  public boolean accepts(Class type, StatementContext ctx) {
    return true;
  }

  @Override
  public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
    return new BeanMapper(type);
  }
}