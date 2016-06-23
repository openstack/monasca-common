/*
 * (C) Copyright 2015-2016 Hewlett Packard Enterprise Development Company LP.
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
package monasca.common.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.TimeZone;

import com.beust.jcommander.internal.Lists;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

@Test
public class ConversionsTest {
  public void testInteger() {
    Integer value = Conversions.variantToInteger(new Integer(Integer.MAX_VALUE));
    assertEquals(value, new Integer(Integer.MAX_VALUE));
  }

  public void testLong() {
    Integer value1 = Conversions.variantToInteger(new Long(1));
    assertEquals(value1, new Integer(1));

    Integer value2 = Conversions.variantToInteger(Long.MAX_VALUE);
    assertEquals(value2, new Integer(-1));
  }

  public void testBigDecimal() {
    Integer value = Conversions.variantToInteger(new BigDecimal(Long.MAX_VALUE));
    assertEquals(value, new Integer(-1));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testString() {
    Conversions.variantToInteger(new String("1"));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testObject() {
    Conversions.variantToInteger(new Object());
  }

  public void testDateTimeShouldNotEqualDifferentTZExplicit() {
    final DateTime now = DateTime.now(DateTimeZone.UTC);
    assertNotEquals(now, Conversions.variantToDateTime(now, DateTimeZone.forOffsetHours(2)));
  }

  public void testDateTimeShouldNotEqualDifferentTZImplicit() {
    // This test fails if the timezone of the JVM is UTC so skip
    // it in that case 
    if (!TimeZone.getDefault().equals(TimeZone.getTimeZone("UTC"))) {
      final DateTime now = DateTime.now();
      assertNotEquals(now, Conversions.variantToDateTime(now));
    }
  }

  public void testDateTimeShouldEqualSameTZImplicit() {
    final DateTime now = DateTime.now(DateTimeZone.UTC);
    assertEquals(now, Conversions.variantToDateTime(now));
  }

  public void testDateTimeShouldEqualSameTZExplicit() {
    final DateTime now = DateTime.now(DateTimeZone.UTC);
    assertEquals(now, Conversions.variantToDateTime(now, DateTimeZone.UTC));
  }

  public void testEnumFromString() {
    assertEquals(MockEnum.THIS, Conversions.variantToEnum("THIS", MockEnum.class));
  }

  public void testEnumFromStringLowerCased() {
    assertEquals(MockEnum.THIS, Conversions.variantToEnum("this", MockEnum.class));
  }

  public void testEnumFromStringWithSpaces() {
    assertEquals(MockEnum.THIS, Conversions.variantToEnum("   THIS          ", MockEnum.class));
  }

  public void testEnumFromNumber() {
    assertEquals(MockEnum.IS, Conversions.variantToEnum(1, MockEnum.class));
  }

  public void testEnumFromNumberDouble() {
    assertEquals(MockEnum.IS, Conversions.variantToEnum(1.0, MockEnum.class));
  }

  public void testEnumFromEnum() {
    assertEquals(MockEnum.TEST, Conversions.variantToEnum(MockEnum.TEST, MockEnum.class));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testEnumShouldFailUnsupportedType(){
    Conversions.variantToEnum(Lists.newArrayList(),MockEnum.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testEnumShouldFailInvalidEnumIndex(){
    final int invalidIndex = MockEnum.class.getEnumConstants().length + 1;
    Conversions.variantToEnum(invalidIndex, MockEnum.class);
  }

  private enum MockEnum {
    THIS,IS,TEST
  }

  public void testVariantToBoolean() {
    assertTrue(Conversions.variantToBoolean(new Long(1)));
    assertFalse(Conversions.variantToBoolean(new Long(0)));

    assertTrue(Conversions.variantToBoolean(new Integer(1)));
    assertFalse(Conversions.variantToBoolean(new Integer(0)));

    assertTrue(Conversions.variantToBoolean(new Short((short)1)));
    assertFalse(Conversions.variantToBoolean(new Short((short)0)));

    assertTrue(Conversions.variantToBoolean(Boolean.TRUE));
    assertFalse(Conversions.variantToBoolean(Boolean.FALSE));
  }
}
