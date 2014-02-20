package com.hpcloud.dropwizard.testing;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.hpcloud.dropwizard.DropwizardTestCase;

@Test(groups = "integration", enabled = false)
public class DropwizardTestCaseTest extends DropwizardTestCase<TestService, TestConfiguration> {
  public DropwizardTestCaseTest() {
    super(TestService.class, TestConfiguration.class, "test-config.yml");
  }

  public void test1() {
    assertEquals(((TestService) service).initialized.get(), 1);
  }

  public void test2() {
    assertEquals(((TestService) service).initialized.get(), 1);
  }
}
