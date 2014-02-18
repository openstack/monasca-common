package com.hpcloud.persistence;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

@Test
public class BeanMappeTest {
  public void shouldConvertPascalCaseToCamelCase() {
    assertEquals(BeanMapper.pascalCaseToCamelCase("SOME_TEST"), "someTest");
    assertEquals(BeanMapper.pascalCaseToCamelCase("TEST"), "test");
    assertEquals(BeanMapper.pascalCaseToCamelCase("test"), "test");
  }
}
