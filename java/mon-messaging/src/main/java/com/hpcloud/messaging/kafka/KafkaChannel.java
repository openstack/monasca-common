package com.hpcloud.messaging.kafka;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.hpcloud.messaging.MessageHandler;
import com.hpcloud.messaging.internal.AbstractPublishSubscribeChannel;
import com.hpcloud.messaging.internal.StandardChannelAdapter;

/**
 * Kafka channel implementation.
 * 
 * @author Jonathan Halterman
 */
@Singleton
public class KafkaChannel extends AbstractPublishSubscribeChannel<JsonNode, JsonNode> {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaChannel.class);

  private final KafkaConfiguration config;
  // private Connection connection;
  private volatile boolean open;

  @Inject
  public KafkaChannel(KafkaConfiguration config, MetricRegistry metricRegistry) {
    super("kafka", new StandardChannelAdapter<JsonNode, JsonNode>(JsonNode.class, JsonNode.class),
        metricRegistry);
    this.config = config;
    Preconditions.checkNotNull(config, "config");
  }

  @Override
  public void close() {
    LOG.info("Closing Kafka channel");

    try {
      // TODO close kafka cxn
    } finally {
      open = false;
    }
  }

  @Override
  public void open() {
    LOG.info("Opening Kafka channel");
    // TODO open kafka cxn
  }

  /**
   * Sends the {@code message} to the {@code topic}.
   */
  public void send(Object message, String topic) {
    Preconditions.checkNotNull(message, "message");
    Preconditions.checkNotNull(topic, "topic");
    if (!open)
      throw new IllegalStateException("The message cannot be sent since the channel is not open");

    // TODO send message
  }

  /**
   * Subscribes the {@code subscriber} to the {@code address} where {@code address} should consist
   * of exchangeName/routingKey.
   */
  @Override
  public void subscribe(MessageHandler<?> subscriber, String address) {
    super.subscribe(subscriber, address);
    if (!open)
      return;

    // TODO create topic subscription
  }

  /**
   * Unsubscribes the {@code subscriber} for the {@code address} where {@code address} should
   * consist of exchangeName/routingKey.
   */
  @Override
  public void unsubscribe(MessageHandler<?> subscriber, String address) {
    super.unsubscribe(subscriber, address);

    // TODO remove topic subscription
  }
}