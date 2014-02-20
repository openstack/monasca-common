package com.hpcloud.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.hpcloud.messaging.internal.LocalChannel;
import com.hpcloud.messaging.internal.ThreadPoolingMessageDispatcher;
import com.hpcloud.service.ManagedService;

/**
 * Standard message bus implementation.
 * 
 * @author Jonathan Halterman
 */
@Singleton
@ThreadSafe
public class StandardMessageBus extends ManagedService implements MessageBus {
  private static final Logger LOG = LoggerFactory.getLogger(StandardMessageBus.class);
  @SuppressWarnings("unused") private final MessageBusConfiguration config;
  private final Map<String, MessageChannel> channels = new ConcurrentHashMap<String, MessageChannel>();
  private final MessageDispatcher dispatcher;
  private final Meter messagesSent;
  /** Used to send local messages that lack a fully qualified address. */
  private MessageChannel localChannel = new LocalChannel();

  @Inject
  public StandardMessageBus(MessageBusConfiguration config, MetricRegistry metricRegistry) {
    this(config, new ThreadPoolingMessageDispatcher(), metricRegistry);
  }

  public StandardMessageBus(MessageBusConfiguration config, MessageDispatcher dispatcher,
      MetricRegistry metricRegistry) {
    super("Message Bus");
    Preconditions.checkNotNull(config, "config");
    Preconditions.checkNotNull(dispatcher, "dispatcher");
    this.config = config;
    this.dispatcher = dispatcher;
    localChannel.bind(dispatcher);
    messagesSent = metricRegistry.meter(getClass().getName() + ".messages.sent");
  }

  static String prefixFor(String address) {
    int index = address.indexOf("://");
    return index == -1 ? null : address.substring(0, index);
  }

  static String topicFor(String address) {
    int index = address.indexOf("://");
    String result = index == -1 ? address : address.substring(index + 3);
    return result == null ? null : "".equals(result) ? null : result;
  }

  @Override
  public void bind(MessageChannel channel) {
    Preconditions.checkNotNull(channel, "channel");
    if (channel.name() == null) {
      Preconditions.checkState(localChannel instanceof LocalChannel,
          "A local channel adapter has already been registered");
      localChannel = channel;
    } else {
      Preconditions.checkState(!channels.containsKey(channel.name()),
          "A channel adapter has already been registered for %s", channel.name());
      channels.put(channel.name(), channel);
    }

    channel.bind(dispatcher);
  }

  @Override
  public MessageChannel channelFor(String address) {
    Preconditions.checkNotNull(address, "address");
    String prefix = prefixFor(address);
    return prefix == null ? localChannel : channels.get(prefix);
  }

  @Override
  public void publish(Object message) {
    checkIsRunning();
    Preconditions.checkNotNull(message, "message");
    localChannel.send(message, message.getClass().getName());
    messagesSent.mark();
  }

  @Override
  public void publish(Object message, String address) {
    checkIsRunning();
    Preconditions.checkNotNull(address, "address");
    Preconditions.checkNotNull(message, "message");
    MessageChannel channel = channelFor(address);
    if (channel != null) {
      channel.send(message, topicFor(address));
      messagesSent.mark();
    }
  }

  @Override
  public void register(MessageHandler<?> handler) {
    Preconditions.checkNotNull(handler, "handler");
    Class<?> messageType = TypeResolver.resolveRawArgument(MessageHandler.class, handler.getClass());
    Preconditions.checkArgument(!messageType.equals(Unknown.class),
        "Must declare message type argument <T> for MessageHandler");
    register(handler, messageType.getName());
  }

  @Override
  public void register(MessageHandler<?> handler, String address) {
    Preconditions.checkNotNull(address);
    Preconditions.checkNotNull(handler);
    MessageChannel channel = channelFor(address);
    Preconditions.checkState(channel != null && channel instanceof PublishSubscribeChannel,
        "No channel adapter has been registered for %s", address);
    ((PublishSubscribeChannel) channel).subscribe(handler, address);
  }

  @Override
  public void start() throws Exception {
    LOG.info("Starting message bus");
    dispatcher.start();
    new Thread("channel-open-thread") {
      public void run() {
        /**
         * Decouples starting the bus from the caller's lifecycle, so that stops are not blocked by
         * starts that may still be running if the caller synchronizes operations against the bus as
         * dropwizard/jetty does against managed services.
         */
        localChannel.open();
        for (MessageChannel channel : channels.values())
          channel.open();
      }
    }.start();
    super.start();
  }

  @Override
  public void stop() throws Exception {
    if (!isRunning())
      return;

    LOG.info("Stopping message bus");
    super.stop();
    localChannel.close();
    for (MessageChannel channel : channels.values())
      channel.close();
    dispatcher.stop();
  }

  @Override
  public void unregister(MessageHandler<?> handler) {
    Preconditions.checkNotNull(handler, "handler");
    unregister(handler, handler.getClass().getName());
  }

  @Override
  public void unregister(MessageHandler<?> handler, String address) {
    Preconditions.checkNotNull(address);
    Preconditions.checkNotNull(handler);
    MessageChannel channel = channelFor(address);
    Preconditions.checkState(channel != null && channel instanceof PublishSubscribeChannel,
        "No channel has been registered for %s", address);
    ((PublishSubscribeChannel) channel).unsubscribe(handler, address);
  }
}
