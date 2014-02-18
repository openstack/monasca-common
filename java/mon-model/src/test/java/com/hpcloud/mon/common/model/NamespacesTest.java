package com.hpcloud.mon.common.model;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.hpcloud.mon.common.model.Namespaces;

@Test
public class NamespacesTest {
  public void shouldReturnEmptyForRequiredUserDefinedDimensions() {
    List<String> list = Namespaces.getRequiredDimensions("userdefined", null);
    assertTrue(list.isEmpty());
  }

  public void shouldValidateComputeInstanceIds() {
    assertTrue(Namespaces.isValidDimensionValue("hpcs.compute", "instance_id",
        "1830d423-83cb-4958-b273-e84bafebf14e"));
    assertFalse(Namespaces.isValidDimensionValue("hpcs.compute", "instance_id",
        "aaaaaaaaaaaaaaaaaaab273ddddddddddddd"));
  }

  public void shouldValidateObjectStoreMetricName() {
    assertTrue(Namespaces.isValidMetricname("hpcs.object-store", "project_write_bytes"));
  }
}
