package monasca.common.model;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test
public class ServicesTest {
  public void shouldValidateObjectStoreMetricName() {
    assertTrue(Services.isValidMetricName("hpcs.object-store", "project_write_bytes"));
  }
}
