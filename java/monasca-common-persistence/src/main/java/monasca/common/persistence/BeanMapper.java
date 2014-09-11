package monasca.common.persistence;

/*
 * Copyright 2004 - 2011 Brian McCallister
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
 */

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * A result set mapper which maps the fields in a statement into a JavaBean. This uses the JDK's
 * built in bean mapping facilities, so it does not support nested properties.
 * 
 * <p>
 * Additionally this bean mapper maps pascal case named columns to camel case named bean properties.
 */
public class BeanMapper<T> implements ResultSetMapper<T> {
  private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
  public static final DateTimeFormatter DATETIME_FORMATTER = ISODateTimeFormat.dateTimeNoMillis()
      .withZoneUTC();

  private final Class<T> type;
  private final Map<String, PropertyDescriptor> properties =
      new HashMap<String, PropertyDescriptor>();

  public BeanMapper(Class<T> type) {
    this.type = type;
    try {
      BeanInfo info = Introspector.getBeanInfo(type);
      for (PropertyDescriptor descriptor : info.getPropertyDescriptors())
        properties.put(descriptor.getName(), descriptor);
    } catch (IntrospectionException e) {
      throw new IllegalArgumentException(e);
    }
  }

  static String pascalCaseToCamelCase(String str) {
    StringBuilder sb = new StringBuilder();
    String[] tokens = str.split("_");
    for (int i = 0; i < tokens.length; i++) {
      String s = tokens[i];
      char c = s.charAt(0);
      sb.append(i == 0 ? Character.toLowerCase(c) : Character.toUpperCase(c));
      if (s.length() > 1)
        sb.append(s.substring(1, s.length()).toLowerCase());
    }

    return sb.toString();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public T map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
    T bean;
    try {
      bean = type.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("A bean, %s, was mapped "
          + "which was not instantiable", type.getName()), e);
    }

    ResultSetMetaData metadata = rs.getMetaData();

    for (int i = 1; i <= metadata.getColumnCount(); ++i) {
      String name = pascalCaseToCamelCase(metadata.getColumnLabel(i).toLowerCase());
      PropertyDescriptor descriptor = properties.get(name);

      if (descriptor != null) {
        Class<?> type = descriptor.getPropertyType();

        Object value;

        if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
          value = rs.getBoolean(i);
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
          value = rs.getByte(i);
        } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
          value = rs.getShort(i);
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
          value = rs.getInt(i);
        } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
          if (metadata.getColumnType(i) == Types.TIMESTAMP)
            value = rs.getTimestamp(i).getTime();
          else
            value = rs.getLong(i);
        } else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
          value = rs.getFloat(i);
        } else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
          value = rs.getDouble(i);
        } else if (type.isAssignableFrom(BigDecimal.class)) {
          value = rs.getBigDecimal(i);
        } else if (type.isAssignableFrom(Timestamp.class)) {
          value = rs.getTimestamp(i);
        } else if (type.isAssignableFrom(Time.class)) {
          value = rs.getTime(i);
        } else if (type.isAssignableFrom(Date.class)) {
          value = rs.getDate(i);
        } else if (type.isAssignableFrom(DateTime.class)) {
          Timestamp ts = rs.getTimestamp(i);
          value = ts == null ? null : new DateTime(ts.getTime(), DateTimeZone.UTC);
        } else if (type.isAssignableFrom(String.class)) {
          if (metadata.getColumnType(i) == Types.TIMESTAMP)
            value = DATETIME_FORMATTER.print(rs.getTimestamp(i).getTime());
          else
            value = rs.getString(i);
        } else if (type.isAssignableFrom(List.class)) {
          String commaStr = rs.getString(i);
          value =
              Strings.isNullOrEmpty(commaStr) ? Collections.emptyList() : COMMA_SPLITTER
                  .splitToList(commaStr);
        } else {
          value = rs.getObject(i);
        }

        if (rs.wasNull() && !type.isPrimitive()) {
          value = null;
        }

        if (type.isEnum() && value != null) {
          value = Enum.valueOf((Class) type, (String) value);
        }

        try {
          descriptor.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(String.format("Unable to access setter for "
              + "property, %s", name), e);
        } catch (InvocationTargetException e) {
          throw new IllegalArgumentException(String.format("Invocation target exception trying to "
              + "invoker setter for the %s property", name), e);
        } catch (NullPointerException e) {
          throw new IllegalArgumentException(String.format("No appropriate method to "
              + "write value %s ", value.toString()), e);
        }
      }
    }

    return bean;
  }
}
