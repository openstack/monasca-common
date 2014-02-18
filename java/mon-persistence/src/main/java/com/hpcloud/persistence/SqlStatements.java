package com.hpcloud.persistence;

import java.util.List;
import java.util.Map;

/**
 * Utilities for producing SQL statements.
 * 
 * @author Jonathan Halterman
 */
public final class SqlStatements {
  private SqlStatements() {
  }

  /**
   * Build a select statement that produces a dataset for the given {@code keyValues}. This
   * statement can be used to join expected values with these actual dataset values. Example result:
   * 
   * <pre>
   * select 'flavor_id' dimension_name, '123' value union all select 'image_id' dimension_name, '456' value
   * </pre>
   */
  public static String unionAllStatementFor(Map<String, String> keyValues, String keyFieldName,
      String valueFieldName) {
    StringBuilder sb = new StringBuilder();
    int propertyCount = 0;
    for (Map.Entry<String, String> kvEntry : keyValues.entrySet()) {
      if (propertyCount != 0)
        sb.append(" union all ");
      sb.append("select '")
          .append(kvEntry.getKey())
          .append("' ")
          .append(keyFieldName)
          .append(", '")
          .append(kvEntry.getValue())
          .append("' ")
          .append(valueFieldName);
      propertyCount++;
    }

    return sb.toString();
  }

  /**
   * Build a select statement that produces a dataset for the given {@code values}. This statement
   * can be used to join expected values with these actual dataset values. Example result:
   * 
   * <pre>
   * select '123' action_id union select '234' action_id
   * </pre>
   */
  public static String unionStatementFor(List<String> values, String valueFieldName) {
    StringBuilder sb = new StringBuilder();
    int propertyCount = 0;
    for (String value : values) {
      if (propertyCount != 0)
        sb.append(" union ");
      sb.append("select '").append(value).append("' ").append(valueFieldName);
      propertyCount++;
    }

    return sb.toString();
  }
}
