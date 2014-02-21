package com.hpcloud.mon.common.model;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

@Test
public class NamespacesTest {
  public void shouldReturnEmptyForRequiredUserDefinedDimensions() {
    List<String> list = Namespaces.getRequiredDimensions("userdefined");
    assertTrue(list.isEmpty());
  }

  public void shouldValidateObjectStoreMetricName() {
    assertTrue(Namespaces.isValidMetricName("hpcs.object-store", "project_write_bytes"));
  }
}
