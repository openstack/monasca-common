/*
 * (C) Copyright 2015-2016 Hewlett Packard Enterprise Development Company LP.
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
package monasca.common.util;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class Conversions {
  private static final String[] SUPPORTED_VARIANT_TO_ENUM_TYPES = new String[]{
      String.class.getSimpleName(),
      Number.class.getSimpleName(),
      Enum.class.getSimpleName()
  };

  /**
   * Converts a Java Object of type Number to an Integer
   * @param variant Java Object of type Number
   * @return Integer
   * @exception IllegalArgumentException
   */
  public static Integer variantToInteger(Object variant) {
    if (variant instanceof Number) {
      return ((Number) variant).intValue();
    } else {
      throw new IllegalArgumentException(String.format("Variant of type \"%s\", and value \"%s\" is not a Number.",
                                                       variant.getClass(), variant));
    }
  }

  /**
   * Converts a Java Object to DateTime instance
   *
   * @param variant object of type supported in {@link org.joda.time.convert.ConverterManager}
   *
   * @return DateTime in {@link DateTimeZone#UTC}
   *
   * @throws IllegalArgumentException
   * @see #variantToDateTime(Object, DateTimeZone)
   * @see DateTime
   * @see DateTimeZone#UTC
   */
  public static DateTime variantToDateTime(final Object variant) {
    return variantToDateTime(variant, DateTimeZone.UTC);
  }


  /**
   * Converts a Java Object to DateTime instance using given {@code timeZone}
   *
   * @param variant  object of type supported in {@link org.joda.time.convert.ConverterManager}
   * @param timeZone timeZone to be used
   *
   * @return DateTime in {@code timeZone}
   *
   * @throws IllegalArgumentException
   * @see #variantToDateTime(Object)
   * @see DateTime
   * @see DateTimeZone
   */
  public static DateTime variantToDateTime(final Object variant, final DateTimeZone timeZone) {
    if (variant instanceof DateTime) {
      return ((DateTime) variant).toDateTime(timeZone);
    }
    return new DateTime(variant, timeZone);
  }

  /**
   * Converts variant to {@code enumClazz} instance.
   *
   * Supported variants are:
   * <ol>
   * <li>{@link String}, trimmed and upper-cased</li>
   * <li>{@link Number}, taken from {@link Class#getEnumConstants}</li>
   * <li>{@link Enum}, simple cast</li>
   * </ol>
   *
   * @param variant   object of type supported by this method, see above
   * @param enumClazz desired {@link Enum}
   * @param <T>       enumType of {@code enumClazz}
   *
   * @return valid enum class instance
   *
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T variantToEnum(final Object variant, final Class<T> enumClazz) {
    if (variant == null) {
      return null;
    }

    if (variant instanceof String) {
      return Enum.valueOf(enumClazz, ((String) variant).trim().toUpperCase());
    } else if (variant instanceof Number) {
      final Integer index = variantToInteger(variant);
      final T[] enumConstants = enumClazz.getEnumConstants();
      if (index < 0 || index >= enumConstants.length) {
        throw new IllegalArgumentException(
            String.format("Variant of type \"%s\", and value \"%s\" is out of range [, %d]",
                variant.getClass(),
                variant,
                enumConstants.length
            )
        );
      }

      return enumConstants[index];
    } else if (variant instanceof Enum) {
      return (T) variant;
    }

    throw new IllegalArgumentException(String.format("\"%s\", and value \"%s\" is not one of %s",
        variant.getClass(), variant, Arrays.toString(SUPPORTED_VARIANT_TO_ENUM_TYPES)));

  }

  /**
   * Converts a Java Object to Boolean
   *
   * @param variant object of type Boolean or Number
   *
   * @return Boolean TRUE if Boolean and TRUE or Number and value is 1
   */
  public static Boolean variantToBoolean(final Object input) {
    if (input instanceof Boolean) {
      return (Boolean) input;
    }
    return "1".equals(input.toString());
  }
}
