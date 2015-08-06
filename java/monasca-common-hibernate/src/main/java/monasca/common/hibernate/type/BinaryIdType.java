/*
 * Copyright 2015 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package monasca.common.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class BinaryIdType
    implements UserType {
  private static final int[] SQL_TYPES = new int[]{Types.BINARY};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class returnedClass() {
    return BinaryId.class;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Object deepCopy(final Object value) throws HibernateException {
    final BinaryId binaryId = (BinaryId) value;
    final byte[] bytes = binaryId.getBytes();
    if (bytes != null) {
      return new BinaryId(
          Arrays.copyOf(bytes, bytes.length)
      );
    }
    return value;
  }

  @Override
  public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner) throws HibernateException, SQLException {
    byte[] bytes = rs.getBytes(names[0]);
    if (rs.wasNull()) {
      return null;
    }
    return new BinaryId(bytes);
  }

  @Override
  public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.VARBINARY);
    } else {
      st.setBytes(index, ((BinaryId) value).getBytes());
    }
  }

  @Override
  public Serializable disassemble(final Object value) throws HibernateException {
    return (Serializable) this.deepCopy(value);
  }

  @Override
  public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
    return this.deepCopy(original);
  }

  @Override
  public boolean equals(final Object x, final Object y) throws HibernateException {
    return Objects.deepEquals(x, y);
  }

  @Override
  public int hashCode(final Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

}
