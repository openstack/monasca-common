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
package monasca.common.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.testng.annotations.Test;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.palominolabs.metrics.guice.InstrumentationModule;

@Test
public class SerializationTest {
  @JsonRootName("instance-id")
  public static class AnnotatedCommand {
    public String value = "test";
  }

  public static class TestCommand {
    public String uuid;
    public int type;

    public TestCommand() {
    }

    public TestCommand(String uuid, int type) {
      this.uuid = uuid;
      this.type = type;
    }

    @Timed
    public void handleIt() {
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      TestCommand other = (TestCommand) obj;
      if (type != other.type)
        return false;
      if (uuid == null) {
        if (other.uuid != null)
          return false;
      } else if (!uuid.equals(other.uuid))
        return false;
      return true;
    }
  }

  public static class TestInjectedCommand {
    transient @Inject String value;
  }

  @SuppressWarnings("unchecked")
  private static <T> T proxyFor(Class<T> type) throws Exception {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(TestCommand.class);
    enhancer.setCallbackType(NoOp.class);
    Class<T> enhanced = enhancer.createClass();
    return enhanced.newInstance();
  }

  @JsonRootName(value = "create-instance")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class CreateInstanceCommand {
    public String uuid;
    public String remoteUuid;
    public String remoteHostName;
    public String tenantId = "default_tenant";
    public String userData;
    public Duration pollingFrequency;
    public Duration waitDuration;

    public CreateInstanceCommand() {
    }

    public CreateInstanceCommand(String uuid, @Nullable String remoteUuid, String remoteHostName,
        @Nullable String tenantId, @Nullable String userData, @Nullable Duration pollingFrequency,
        @Nullable Duration waitDuraion) {
      this.uuid = Preconditions.checkNotNull(uuid);
      this.remoteUuid = remoteUuid;
      this.remoteHostName = Preconditions.checkNotNull(remoteHostName);
      if (tenantId != null)
        this.tenantId = tenantId;
      this.userData = userData;
      this.pollingFrequency = pollingFrequency;
      this.waitDuration = waitDuraion;
    }
  }

  @Test(enabled = false)
  public void shouldDeserializeFromJsonElement() {
    JsonNode node = Serialization.toJsonNode("{\"uuid\":\"123\",\"type\":1}");
    TestCommand command = Serialization.fromJson(node, TestCommand.class);
    assertEquals(command, new TestCommand("123", 1));
  }

  public void shouldDeserializeFromWrappedJsonElement() {
    TestCommand command = new TestCommand("123", 1);
    JsonNode node = Serialization.toJsonNode(command);
    Serialization.registerTarget(TestCommand.class);
    TestCommand command2 = Serialization.fromJson(node);
    assertEquals(command2, command);
  }

  public void shouldDeserialize() {
    TestCommand command = new TestCommand("123", 1);
    String json = "{\"TestCommand\":{\"uuid\":\"123\",\"type\":1}}";
    Serialization.registerTarget(TestCommand.class);
    TestCommand command2 = Serialization.fromJson(json);
    assertEquals(command2, command);
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldFailToDeserializeOnUnknownTargetType() {
    String json = "{\"Blah\":{\"uuid\":\"123\",\"type\":1}}";
    Serialization.fromJson(json);
  }

  public void shouldDeserializeWithRootType() {
    TestCommand command = new TestCommand("123", 1);
    String json = "{\"TestCommand\":{\"uuid\":\"123\",\"type\":1}}";
    TestCommand command2 = Serialization.fromJson(json, TestCommand.class);
    assertEquals(command2, command);
  }

  public void shouldRegisterTargetUsingAnnotatedName() {
    Serialization.registerTarget(AnnotatedCommand.class);
    assertTrue(Serialization.targetTypes.containsKey("instance-id"));
  }

  public void shouldSerializeWrappedValueFromJsonToNode() {
    String json = "{\"TestCommand\":{\"uuid\":\"123\",\"type\":1}}";
    JsonNode node = Serialization.toJsonNode(json);
    TestCommand cmd = Serialization.fromJson(node, TestCommand.class);
    assertEquals(cmd, new TestCommand("123", 1));
  }

  public void shouldSerialize() {
    TestCommand command = new TestCommand("123", 1);
    String json = "{\"TestCommand\":{\"uuid\":\"123\",\"type\":1}}";
    String ser = Serialization.toJson(command);
    assertEquals(ser, json);
  }

  // TODO enable after https://github.com/FasterXML/jackson-databind/issues/412 is fixed
  @Test(enabled = false)
  public void shouldSerializeAndDeserializeProxies() throws Throwable {
    TestCommand cmd = proxyFor(TestCommand.class);
    cmd.uuid = "1234";
    cmd.type = 1;

    String json = Serialization.toJson(cmd);
    TestCommand cmd1 = Serialization.fromJson(json);
    assertEquals(cmd.uuid, cmd1.uuid);
    assertEquals(cmd.type, cmd1.type);
  }

  public void shouldSerializeAndDeserializeToRegisteredType() {
    TestCommand command = new TestCommand("123", 1);
    String json = Serialization.toJson(command);
    assertEquals(Serialization.fromJson(json), command);
  }

  public void toJsonShouldUseAnnotatedRootName() {
    String json = Serialization.toJson(new AnnotatedCommand());
    String expected = "{\"instance-id\":{\"value\":\"test\"}}";
    assertEquals(json, expected);
  }

  // TODO enable after https://github.com/FasterXML/jackson-databind/issues/412 is fixed
  @Test(enabled = false)
  public void shouldSerializeGuiceInstrumentedProxies() {
    Injector.reset();
    Injector.registerModules(new InstrumentationModule());
    TestCommand cmd = Injector.getInstance(TestCommand.class);
    cmd.uuid = "1234";
    cmd.type = 5;
    String json = Serialization.toJson(cmd);
    TestCommand cmd1 = Serialization.fromJson(json);
    assertEquals(cmd, cmd1);
  }

  public void shouldProduceFullyInjectedInstances() {
    Injector.reset();
    Injector.registerModules(new AbstractModule() {
      protected void configure() {
        bind(String.class).toInstance("test");
      }
    });

    String json = Serialization.toJson(new TestInjectedCommand());
    TestInjectedCommand cmd = Serialization.fromJson(json);
    assertEquals(cmd.value, "test");
  }

  public void shouldParseSingleQuotedJson() {
    String json = "{'args': {'volume': {'df': '/dev/vdb        505G  8.7G  471G   2% /mnt'}, 'state': 1, 'hostname': '3b2350a6-c28e-4d01-8a8d-c7c9b512c0f0'}, 'method': 'update_instance_state'}";
    assertNotNull(Serialization.toJsonNode(json));
  }

  public void shouldSupportSuperflousElements() {
    Serialization.registerTarget(TestCommand.class);
    String json = "{\"TestCommand\":{\"uuid\":\"123\",\"type\":1,\"foo\":\"bar\"}}";
    TestCommand cmd = Serialization.fromJson(json);
    assertEquals(cmd.uuid, "123");
  }

  @Test(enabled = false)
  public void shouldSerializeAndDeserializeNullValues() {
  }

  public void shouldSerializeAndDeserializePrimitives() {
    int i = 1;
    String json = Serialization.toJson(i);
    int ii = Serialization.<Integer>fromJson(json);
    assertEquals(i, ii);

    Integer j = 1;
    json = Serialization.toJson(i);
    Integer jj = Serialization.<Integer>fromJson(json);
    assertEquals(j, jj);
  }
}
