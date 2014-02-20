package com.hpcloud.messaging;

/**
 * Handles messages of type {@code <T>}.
 * 
 * @param <T> message type
 * @author Jonathan Halterman
 */
public interface MessageHandler<T> {
  /**
   * Handles the {@code message}.
   */
  void handle(T message);
}
