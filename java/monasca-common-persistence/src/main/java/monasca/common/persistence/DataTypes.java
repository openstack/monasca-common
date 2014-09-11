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
