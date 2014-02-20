package com.hpcloud.messaging.functional;

import org.jodah.concurrentunit.ConcurrentTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;
import com.hpcloud.messaging.MessageBus;
import com.hpcloud.messaging.MessageBusConfiguration;
import com.hpcloud.messaging.MessageHandler;
import com.hpcloud.messaging.StandardMessageBus;
import com.hpcloud.messaging.SynchronousMessageDispatcher;

@Test
public class MessageHandlerInstanceTest extends ConcurrentTestCase {
  private MessageBus bus;

  static class Foo {
  }

  @BeforeClass
  protected void beforeClass() throws Exception {
    MessageBusConfiguration busConfig = new MessageBusConfiguration();
    bus = new StandardMessageBus(busConfig, new SynchronousMessageDispatcher(),
        new MetricRegistry());
    bus.start();
  }

  @AfterClass
  protected void afterClass() throws Exception {
    bus.stop();
  }

  public void shouldDeliverMessageToInstanceHandler() throws Throwable {
    final Foo foo = new Foo();
    bus.register(new MessageHandler<Foo>() {
      @Override
      public void handle(Foo message) {
        threadAssertEquals(message, foo);
        resume();
      }
    });

    bus.publish(foo);
    await(2000);
  }
}
