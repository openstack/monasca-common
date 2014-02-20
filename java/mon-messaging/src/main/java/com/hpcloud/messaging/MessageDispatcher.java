package com.hpcloud.messaging;

import com.hpcloud.service.Service;

/**
 * Dispatches messages to handlers.
 * 
 * @author Jonathan Halterman
 */
public interface MessageDispatcher extends Service {
  /**
   * Dispatches the {@code message} to the {@code handler}.
   * 
   * @param <T> message type
   * @param message to dispatch
   * @param handler to handle message
   */
  <T> void dispatch(T message, MessageHandler<T> handler);
}
