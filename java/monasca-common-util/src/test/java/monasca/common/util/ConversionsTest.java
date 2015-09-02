/*
 * Copyright (c) 2015 Hewlett-Packard Development Company, L.P.
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

import org.testng.annotations.Test;


import java.math.BigDecimal;

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
}
