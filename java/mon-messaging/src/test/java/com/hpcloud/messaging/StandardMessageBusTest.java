package com.hpcloud.messaging;

import static org.testng.Assert.assertEquals;

import org.jodah.concurrentunit.ConcurrentTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.hpcloud.messaging.internal.AbstractPublishSubscribeChannel;
import com.hpcloud.util.Injector;

@Test
public class StandardMessageBusTest extends ConcurrentTestCase {
  static final TestMessage testMessage = new TestMessage("test message");
  static final TestMessage condMessage = new TestMessage("conditional message");
  MessageBusConfiguration busConfig = new MessageBusConfiguration();
  MessageDispatcher dispatcher = new SynchronousMessageDispatcher();

  public static class TestMessage {
    public String message;

    public TestMessage() {
    }

    public TestMessage(String message) {
      this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TestMessage other = (TestMessage) obj;
      if (message == null) {
        if (other.message != null)
          return false;
      } else if (!message.equals(other.message))
        return false;
      return true;
    }
  }

  class TestMessageHandler implements MessageHandler<TestMessage> {
    public void handle(TestMessage message) {
      threadAssertEquals(message, testMessage);
      resume();
    }
  }

  @BeforeClass
  protected void beforeClass() {
    Injector.registerModules(new AbstractModule() {
      protected void configure() {
        bind(TestMessageHandler.class).toInstance(new TestMessageHandler());
      }
    });
  }

  static class SomeMessage {
  }

  static class SomeMessageHandler implements MessageHandler<SomeMessage> {
    @Override
    public void handle(SomeMessage message) {
    }
  }

  public void testPrefixFor() {
    assertEquals(StandardMessageBus.prefixFor("test://address"), "test");
    assertEquals(StandardMessageBus.prefixFor("akka://"), "akka");
    assertEquals(StandardMessageBus.prefixFor("akka:/"), null);
    assertEquals(StandardMessageBus.prefixFor("akka"), null);
  }

  public void testTopicFor() {
    assertEquals(StandardMessageBus.topicFor("test://address1/address2"), "address1/address2");
    assertEquals(StandardMessageBus.topicFor("test://address"), "address");
    assertEquals(StandardMessageBus.topicFor("akka://"), null);
    assertEquals(StandardMessageBus.topicFor("akka:/"), "akka:/");
    assertEquals(StandardMessageBus.topicFor("akka"), "akka");
  }

  public void shouldPublishToChannel() throws Throwable {
    MessageChannel channel = new AbstractPublishSubscribeChannel<Object, Object>() {
      @Override
      public void send(Object message, String address) {
        threadAssertEquals(testMessage, message);
        resume();
      }
    };

    StandardMessageBus bus = new StandardMessageBus(busConfig, dispatcher, new MetricRegistry());
    bus.bind(channel);
    bus.start();
    bus.publish(testMessage);
    await(2000);
  }

  /** This behavior was removed. */
  @Test(expectedExceptions = IllegalStateException.class, enabled = false)
  public void shouldThrowOnDuplicateMessageHandlerRegistration() {
    StandardMessageBus bus = new StandardMessageBus(busConfig, dispatcher, new MetricRegistry());
    SomeMessageHandler h = new SomeMessageHandler();
    bus.register(h, "a");
    bus.register(h, "a");
  }
}