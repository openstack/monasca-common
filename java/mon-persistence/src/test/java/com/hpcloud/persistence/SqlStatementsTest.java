package com.hpcloud.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
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
