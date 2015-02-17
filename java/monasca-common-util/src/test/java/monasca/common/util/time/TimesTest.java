/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
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
package monasca.common.util.time;

import static org.testng.Assert.assertEquals;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

@Test
public class TimesTest {
  public void shouldRoundDownToNearestMinute() {
    long t = 1367971679L; // Unix time
    long rounded = Times.roundDownToNearestMinute(t);
    DateTime dt = new DateTime(t * 1000);
    DateTime dt1 = new DateTime(rounded * 1000);

    assertEquals(dt.getYear(), dt1.getYear());
    assertEquals(dt.getMonthOfYear(), dt1.getMonthOfYear());
    assertEquals(dt.getDayOfYear(), dt1.getDayOfYear());
    assertEquals(dt.getHourOfDay(), dt1.getHourOfDay());
    assertEquals(dt.getMinuteOfHour(), dt1.getMinuteOfHour());
    assertEquals(dt1.getSecondOfMinute(), 0);
    assertEquals(dt1.getMillisOfSecond(), 0);
  }

  public void shouldRoundDownToNearestSecond() {
    long t = 1363982335257L; // Java time
    long rounded = Times.roundDownToNearestSecond(t);
    DateTime dt = new DateTime(t);
    DateTime dt1 = new DateTime(rounded);

    assertEquals(dt.getYear(), dt1.getYear());
    assertEquals(dt.getMonthOfYear(), dt1.getMonthOfYear());
    assertEquals(dt.getDayOfYear(), dt1.getDayOfYear());
    assertEquals(dt.getHourOfDay(), dt1.getHourOfDay());
    assertEquals(dt.getMinuteOfHour(), dt1.getMinuteOfHour());
    assertEquals(dt.getSecondOfMinute(), dt1.getSecondOfMinute());
    assertEquals(dt1.getMillisOfSecond(), 0);
  }
}
