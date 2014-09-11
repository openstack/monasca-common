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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

@Test
public class SqlStatementsTest {
  public void testUnionAllStatementFor() {
    Map<String, String> dimensions = new HashMap<String, String>();
    dimensions.put("flavor_id", "937");
    dimensions.put("image_id", "12");

    assertEquals(
        SqlStatements.unionAllStatementFor(dimensions, "dimension_name", "value"),
        "select 'flavor_id' dimension_name, '937' value union all select 'image_id' dimension_name, '12' value");
  }

  public void testUnionStatementFor() {
    List<String> actions = new ArrayList<String>();
    actions.add("123");
    actions.add("234");

    assertEquals(SqlStatements.unionStatementFor(actions, "action_id"),
        "select '123' action_id union select '234' action_id");
  }
}
