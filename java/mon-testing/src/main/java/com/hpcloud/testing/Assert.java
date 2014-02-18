package com.hpcloud.testing;

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
