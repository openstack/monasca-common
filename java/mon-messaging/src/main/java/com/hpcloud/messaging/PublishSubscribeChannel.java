package com.hpcloud.messaging;

/**
 * Publishes messages to and subscribes to messages from topics.
 * 
 * @author Jonathan Halterman
 */
public interface PublishSubscribeChannel extends MessageChannel {
  /**
   * Subscribes the {@code subscriber} to the {@code topic}.
   * 
   * @throws NullPointerException if the {@code subscriber} or {@code topic} are null
   * @throws IllegalStateException if the {@code subscriber} is already subscribed to the
   *           {@code topic}
   */
  void subscribe(MessageHandler<?> subscriber, String topic);

  /**
   * Unsubscribes the {@code subscriber} from the {@code topic}.
   * 
   * @throws NullPointerException if the {@code subscriber} or {@code topic} are null
   */
  void unsubscribe(MessageHandler<?> subscriber, String topic);
}