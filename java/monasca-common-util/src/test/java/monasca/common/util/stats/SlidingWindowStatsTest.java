/*
 * (C) Copyright 2014-2016 Hewlett Packard Enterprise Development LP
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
package monasca.common.util.stats;

import static monasca.common.testing.Assert.assertArraysEqual;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import monasca.common.util.time.TimeResolution;

@Test
public class SlidingWindowStatsTest {
  public void testIndexOf() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 1, 5, 2, 5);

    // Window 1, 2, 3, 4, 5, 6, 7
    assertEquals(window.indexOf(0), 0);
    assertEquals(window.indexOf(1), 1);
    assertEquals(window.indexOf(2), 2);
    assertEquals(window.indexOf(4), 4);
    assertEquals(window.indexOf(6), 6);

    // Window 8, 9, 10, 4, 5, 6, 7
    window.slideViewTo(8, 1);
    assertEquals(window.indexOf(0), 3);
    assertEquals(window.indexOf(1), 4);
    assertEquals(window.indexOf(2), 5);
    assertEquals(window.indexOf(4), 0);
    assertEquals(window.indexOf(6), 2);

    // Window 8, 9, 10, 11, 12, 6, 7
    window.slideViewTo(10, 1);
    assertEquals(window.indexOf(0), 5);
    assertEquals(window.indexOf(1), 6);
    assertEquals(window.indexOf(2), 0);
    assertEquals(window.indexOf(4), 2);
    assertEquals(window.indexOf(6), 4);

    // Window 15, 9, 10, 11, 12, 13, 14
    window.slideViewTo(13, 1);
    assertEquals(window.indexOf(0), 1);
    assertEquals(window.indexOf(1), 2);
    assertEquals(window.indexOf(2), 3);
    assertEquals(window.indexOf(4), 5);
    assertEquals(window.indexOf(6), 0);
  }

  public void shouldGetTimestamps() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 1, 5, 2, 10);

    assertArraysEqual(window.getTimestamps(), new long[] { 6, 7, 8, 9, 10 });
    window.slideViewTo(14, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 10, 11, 12, 13, 14 });

    window = new SlidingWindowStats(Statistics.Average.class, TimeResolution.ABSOLUTE, 3, 3, 2, 6);

    assertArraysEqual(window.getTimestamps(), new long[] { 0, 3, 6 });
    window.slideViewTo(14, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 9, 12, 15 });
  }

  public void shouldSlideViewTo() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 3, 3, 2, 6);

    window.slideViewTo(2, 1);
    window.slideViewTo(8, 1);
    assertEquals(window.getTimestamps(), new long[] { 3, 6, 9 });

    window.slideViewTo(10, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 3, 6, 9 });
    window.slideViewTo(12, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 6, 9, 12 });

    window.slideViewTo(14, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 9, 12, 15 });

    window.slideViewTo(18, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 12, 15, 18 });

    // Attempt to slide backwards - Noop
    window.slideViewTo(10, 1);
    assertArraysEqual(window.getTimestamps(), new long[] { 12, 15, 18 });
  }

  public void testSlides() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 60, 1, 2, 0);

    assertTrue(window.addValue(1.0, 20, false));
    window.slideViewTo(20, 30);
    assertTrue(window.addValue(1.0, 0, false));

    window.slideViewTo(100, 30);
    assertFalse(window.addValue(1.0, 0, false));
    assertTrue(window.addValue(1.0, 61, false));

    window.slideViewTo(121, 30);
    assertTrue(window.addValue(1.0, 61, false));
    assertTrue(window.addValue(1.0, 121, false));

    window.slideViewTo(180, 30);
    assertFalse(window.addValue(1.0, 61, false));
    assertTrue(window.addValue(1.0, 121, false));
    assertTrue(window.addValue(1.0, 181, false));

    window.slideViewTo(241, 30);
    assertFalse(window.addValue(1.0, 121, false));
    assertTrue(window.addValue(1.0, 181, false));
    assertTrue(window.addValue(1.0, 241, false));

    window.slideViewTo(360, 30);
    assertFalse(window.addValue(1.0, 241, false));
    assertTrue(window.addValue(1.0, 300, false));
    assertTrue(window.addValue(1.0, 361, false));
  }

  public void shouldAddValueAndGetWindowValues() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 3, 3, 2, 9);
    for (int i = 0; i < 5; i++)
      window.addValue(999, i * 3, false);

    assertEquals(window.getWindowValues(), new double[] { 999, 999, 999, 999, 999 });

    window.slideViewTo(12, 1);
    assertEquals(window.getWindowValues(), new double[] { 999, 999, 999, 999, Double.NaN });

    window.addValue(888, 17, false);
    assertEquals(window.getWindowValues(), new double[] { 999, 999, 999, 999, 888 });
  }

  public void shouldAddValueAndGetViewValues() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 3, 3, 2, 9);
    for (int i = 0; i < 5; i++)
      window.addValue(999, i * 3, false);

    assertEquals(window.getViewValues(), new double[] { 999, 999, 999 });

    window.slideViewTo(15, 1);
    assertEquals(window.getViewValues(), new double[] { 999, 999, 999 });

    window.addValue(777, 15, false);
    window.addValue(888, 18, false);
    assertEquals(window.getViewValues(), new double[] { 999, 999, 999 });
    window.slideViewTo(21, 1);
    assertEquals(window.getViewValues(), new double[] { 999, 777, 888 });
  }

  public void shouldAddOutOfWindowValueWithForce() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Last.class,
        TimeResolution.ABSOLUTE, 3, 1, 2, 9);
    window.addValue(999, 3, true);
    assertEquals(window.getViewValues(), new double[] { 999 });
    window.addValue(899, 10, true);
    assertEquals(window.getViewValues(), new double[] { 899 });
  }

  public void shouldNotAddOutOfWindowValueWithoutForce() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 3, 1, 2, 9);
    window.addValue(999, 3, false);
    assertEquals(window.getViewValues(), new double[] { Double.NaN });
    window.addValue(899, 10, false);
    assertEquals(window.getViewValues(), new double[] { Double.NaN });
  }

  public void shouldIgnoreOutOfOrderValue() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Last.class,
        TimeResolution.ABSOLUTE, 3, 1, 2, 9);
    window.addValue(999, 3, true);
    window.addValue(998, 2, true);
    assertEquals(window.getViewValues(), new double[] { 999 });
  }

  public void testIndexOfTime() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Average.class,
        TimeResolution.ABSOLUTE, 3, 3, 2, 15);

    // Slots look like 6 9 12 15 18
    assertEquals(window.indexOfTime(5), -1);
    assertEquals(window.indexOfTime(9), 1);
    assertEquals(window.indexOfTime(10), 1);
    assertEquals(window.indexOfTime(12), 2);
    assertEquals(window.indexOfTime(13), 2);
    assertEquals(window.indexOfTime(15), 3);
    assertEquals(window.indexOfTime(17), 3);
    assertEquals(window.indexOfTime(20), 4);
    assertEquals(window.indexOfTime(21), -1);

    window.slideViewTo(20, 1);

    // Slots like 21 24 12 15 18
    assertEquals(window.indexOfTime(11), -1);
    assertEquals(window.indexOfTime(12), 2);
    assertEquals(window.indexOfTime(15), 3);
    assertEquals(window.indexOfTime(17), 3);
    assertEquals(window.indexOfTime(20), 4);
    assertEquals(window.indexOfTime(22), 0);
    assertEquals(window.indexOfTime(26), 1);
    assertEquals(window.indexOfTime(28), -1);

    window.slideViewTo(23, 1);

    // Slots like 21 24 27 15 18
    assertEquals(window.indexOfTime(14), -1);
    assertEquals(window.indexOfTime(19), 4);
    assertEquals(window.indexOfTime(20), 4);
    assertEquals(window.indexOfTime(22), 0);
    assertEquals(window.indexOfTime(26), 1);
    assertEquals(window.indexOfTime(28), 2);
    assertEquals(window.indexOfTime(31), -1);
  }

  public void shouldGetValue() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 5, 3, 2, 20);
    // Logical window is 5 10 15
    window.addValue(2, 5, false);
    window.addValue(3, 10, false);
    window.addValue(4, 15, false);

    assertEquals(window.getValue(5), 2.0);
    assertEquals(window.getValue(10), 3.0);
    assertEquals(window.getValue(15), 4.0);

    // Slide logical window to 10 15 20
    window.slideViewTo(25, 1);
    window.addValue(5, 24, false);

    assertEquals(window.getValue(10), 3.0);
    assertEquals(window.getValue(15), 4.0);
    assertEquals(window.getValue(20), 5.0);
  }

  public void testLengthToIndex() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 1, 5, 2, 6);
    // Window 1, 2, 3, 4, 5, 6, 7
    assertEquals(window.lengthToIndex(6), 7);
    assertEquals(window.lengthToIndex(4), 5);
    assertEquals(window.lengthToIndex(2), 3);
    assertEquals(window.lengthToIndex(1), 2);
    assertEquals(window.lengthToIndex(0), 1);

    // Window 8, 2, 3, 4, 5, 6, 7
    window.slideViewTo(7, 0);
    assertEquals(window.lengthToIndex(6), 6);
    assertEquals(window.lengthToIndex(4), 4);
    assertEquals(window.lengthToIndex(2), 2);
    assertEquals(window.lengthToIndex(1), 1);
    assertEquals(window.lengthToIndex(0), 7);

    // Window 8, 9, 10, 4, 5, 6, 7
    window.slideViewTo(9, 1);
    assertEquals(window.lengthToIndex(6), 4);
    assertEquals(window.lengthToIndex(4), 2);
    assertEquals(window.lengthToIndex(2), 7);
    assertEquals(window.lengthToIndex(1), 6);
    assertEquals(window.lengthToIndex(0), 5);

    // Window 8, 9, 10, 11, 12, 13, 7
    window.slideViewTo(12, 1);
    assertEquals(window.lengthToIndex(6), 1);
    assertEquals(window.lengthToIndex(4), 6);
    assertEquals(window.lengthToIndex(2), 4);
    assertEquals(window.lengthToIndex(1), 3);
    assertEquals(window.lengthToIndex(0), 2);
  }

  public void shouldGetValuesUpTo() {
    SlidingWindowStats window = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 5, 3, 2, 20);
    // Window is 5 10 15 20 25
    window.addValue(2, 5, false);
    window.addValue(3, 10, false);
    window.addValue(4, 15, false);

    assertEquals(window.getValuesUpTo(20), new double[] { 2, 3, 4, Double.NaN });
    assertEquals(window.getValuesUpTo(18), new double[] { 2, 3, 4 });
    assertEquals(window.getValuesUpTo(12), new double[] { 2, 3 });
    assertEquals(window.getValuesUpTo(9), new double[] { 2 });

    // Window is 30 10 15 20 25
    window.slideViewTo(22, 1);
    window.addValue(5, 22, false);
    assertEquals(window.getValuesUpTo(22), new double[] { 3, 4, 5 });
    assertEquals(window.getValuesUpTo(15), new double[] { 3, 4 });
    assertEquals(window.getValuesUpTo(12), new double[] { 3 });

    // Window is 30 35 15 20 25
    window.slideViewTo(27, 1);
    window.addValue(6, 26, false);
    assertEquals(window.getValuesUpTo(27), new double[] { 4, 5, 6 });
    assertEquals(window.getValuesUpTo(24), new double[] { 4, 5 });
    assertEquals(window.getValuesUpTo(18), new double[] { 4 });

    // Assert out of bounds
    try {
      assertEquals(window.getValuesUpTo(9), new double[] {});
      fail();
    } catch (Exception expected) {
    }

    // Assert out of bounds
    try {
      assertEquals(window.getValuesUpTo(41), new double[] {});
      fail();
    } catch (Exception expected) {
    }
  }

  public void testToString() {
    SlidingWindowStats smallWindow = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 5, 3, 2, 20);
    assertEquals(smallWindow.toString(),
        "SlidingWindowStats timescale = ABSOLUTE slotWidth = 5 viewEndTimestamp = 20 slotEndTimestamp = 20 [(5=NaN, 10=NaN, 15=NaN), 20=NaN, 25=NaN]");

    SlidingWindowStats bigWindow = new SlidingWindowStats(Statistics.Sum.class,
        TimeResolution.ABSOLUTE, 10, 10, 2, 50);
    assertEquals(bigWindow.toString(),
        "SlidingWindowStats timescale = ABSOLUTE slotWidth = 10 viewEndTimestamp = 50 slotEndTimestamp = 50 [(... 20=NaN, 30=NaN, 40=NaN), 50=NaN, 60=NaN]");
  }
}
