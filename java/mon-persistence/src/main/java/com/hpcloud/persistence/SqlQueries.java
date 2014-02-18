package com.hpcloud.persistence;

import java.util.Iterator;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

/**
 * Utilities for building and performing queries.
 * 
 * @author Jonathan Halterman
 */
public final class SqlQueries {
  private SqlQueries() {
  }

  /**
   * Returns a map of key/value pairs for the given {@code keyValueSelectingSql} with the
   * {@code orderedParameters}.
   * 
   * @param handle to execute statement against
   * @param keyValueSelectingSql statement that selects a key and value
   * @param orderedParameters ordered parameters to set against the {@code keyValueSelectingSql}
   * @return a map of key value pairs
   */
  public static Map<String, String> keyValuesFor(Handle handle, String keyValueSelectingSql,
      Object... orderedParameters) {
    Query<Map<String, Object>> q = handle.createQuery(keyValueSelectingSql);
    for (int i = 0; i < orderedParameters.length; i++)
      q.bind(i, orderedParameters[i]);

    KeyValueMapper mapper = new KeyValueMapper();
    Iterator<Object> it = q.map(mapper).iterator();
    while (it.hasNext())
      it.next();

    return mapper.map;
  }
}
