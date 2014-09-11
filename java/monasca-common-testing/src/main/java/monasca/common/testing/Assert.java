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
package monasca.common.testing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class Assert {
  public static void assertArraysEqual(final double[] actual, final double[] expected) {
    if (expected == actual)
      return;
    if (null == expected)
      fail("expected a null array, but not null found.");
    if (null == actual)
      fail("expected not null array, but null found.");

    assertEquals(actual.length, expected.length, "arrays don't have the same size.");

    for (int i = 0; i < expected.length; i++) {
      if (expected[i] != actual[i]) {
        fail("arrays differ firstly at element [" + i + "]; " + "expected value is <" + expected[i]
            + "> but was <" + actual[i] + ">.");
      }
    }
  }

  public static void assertArraysEqual(final long[] actual, final long[] expected) {
    if (expected == actual)
      return;
    if (null == expected)
      fail("expected a null array, but not null found.");
    if (null == actual)
      fail("expected not null array, but null found.");

    assertEquals(actual.length, expected.length, "arrays don't have the same size.");

    for (int i = 0; i < expected.length; i++) {
      if (expected[i] != actual[i]) {
        fail("arrays differ firstly at element [" + i + "]; " + "expected value is <" + expected[i]
            + "> but was <" + actual[i] + ">.");
      }
    }
  }
}
