package com.hpcloud.persistence;

import java.io.Reader;
import java.sql.Clob;

/**
 * Utilities for working with SQL data types.
 */
public final class DataTypes {
  private DataTypes() {
  }

  /**
   * Returns the String read from the {@code clob}, else null if the String could not be read.
   */
  @SuppressWarnings("unused")
  public static String toString(Clob clob) {
    try {
      Reader is = clob.getCharacterStream();
      StringBuffer sb = new StringBuffer();
      char[] buffer = new char[(int) clob.length()];
      int count = 0;

      while ((count = is.read(buffer)) != -1)
        sb.append(buffer);

      return new String(sb);
    } catch (Exception e) {
      return null;
    }
  }
}
