package com.hpcloud.messaging.internal;

import java.util.Collection;

import javax.annotation.concurrent.ThreadSafe;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.hpcloud.messaging.ChannelAdapter;
import com.hpcloud.messaging.MessageHandler;
import com.hpcloud.messaging.PublishSubscribeChannel;
import com.hpcloud.util.Serialization;

/**
 * Base publish/subscribe channel adapter implementation.
 * 
 * @author Jonathan Halterman
 */
@ThreadSafe
public abstract class AbstractPublishSubscribeChannel<I, O> extends AbstractMessageChannel<I, O>
    implements PublishSubscribeChannel {
  protected final Multimap<String, MessageHandler<?>> subscribers = Multimaps.synchronizedMultimap(HashMultimap.<String, MessageHandler<?>>create());
  protected final Meter messagesReceived;

  public AbstractPublishSubscribeChannel() {
    this(null, null, new MetricRegistry());
  }

  public AbstractPublishSubscribeChannel(String name, ChannelAdapter<I, O> adapter,
      MetricRegistry metricRegistry) {
    super(name, adapter);
    messagesReceived = metricRegistry.meter(getClass().getName() + ".messages.received");
    metricRegistry.register(getClass().getName() + ".subscribers", new Gauge<Integer>() {
      @Override
      public Integer getValue() {
        return subscribers.size();
      }
    });
  }

  @Override
  public void close() {
  }

  @Override
  public void open() {
  }

  @Override
  public void subscribe(MessageHandler<?> subscriber, String topic) {
    Preconditions.checkNotNull(subscriber, "subscriber");
    Preconditions.checkNotNull(topic, "topic");
    Class<?> messageType = TypeResolver.resolveRawArgument(MessageHandler.class,
        subscriber.getClass());
    if (messageType != Unknown.class && messageType != Object.class)
      Serialization.registerTarget(messageType);
    subscribers.put(topic, subscriber);
  }

  @Override
  public void unsubscribe(MessageHandler<?> subscriber, String topic) {
    Preconditions.checkNotNull(subscriber, "subscriber");
    Preconditions.checkNotNull(topic, "topic");
    subscribers.remove(topic, subscriber);
  }

  /**
   * Handles an inbound {@code messageBody} that was consumed from a {@code topic}, adapting it
   * using the bound ChannelAdapter and dispatching it to all subscribed handlers using the bound
   * dispatcher.
   */
  @SuppressWarnings("unchecked")
  protected void handle(I messageBody, String address) {
    Preconditions.checkState(dispatcher != null, "A dispatcher has not yet been bound");
    Object message = adaptedMessageFor(messageBody, address);
    if (message == null)
      return;

    Collection<MessageHandler<?>> handlers = subscribers.get(address);
    if (!handlers.isEmpty()) {
      // TODO separate message copies for each handler
      synchronized (subscribers) {
        for (MessageHandler<?> handler : handlers) {
          Class<?> messageType = TypeResolver.resolveRawArgument(MessageHandler.class,
              handler.getClass());
          if (messageType.isAssignableFrom(message.getClass()))
            dispatcher.dispatch(message, (MessageHandler<Object>) handler);
        }
      }
    }

    messagesReceived.mark();
  }

  private Object adaptedMessageFor(I messageBody, String address) {
    return adapter == null ? messageBody : adapter.adaptInboundMessage(messageBody, address);
  }
}
