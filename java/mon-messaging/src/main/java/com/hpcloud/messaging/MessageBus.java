package com.hpcloud.messaging;

import com.hpcloud.service.Service;

/**
 * Message bus. Acts as a registry for channels.
 * 
 * @see <a href="http://eaipatterns.com/MessageBus.html">Message Bus</a>
 * @author Jonathan Halterman
 */
public interface MessageBus extends Service {
  /**
   * Binds the {@code channel} to the bus according to the {@link MessageChannel#name()}.
   * 
   * @throws NullPointerException if {@code channel} is null
   * @throws IllegalStateException if a local MessageChannel is already bound
   */
  void bind(MessageChannel channel);

  /**
   * Returns the bound MessageChannel for the {@code address} else {@code null} if none has been
   * bound.
   * 
   * @throws NullPointerException if {@code address} is null
   */
  MessageChannel channelFor(String address);

  /**
   * Publishes the {@code message} to all handlers that are registered for the {@code message}.
   * 
   * @throws NullPointerException if {@code message} is null
   * @throws IllegalStateException if the bus has not been started
   */
  void publish(Object message);

  /**
   * Publishes the {@code message} to all registered handlers for the {@code address}.
   * 
   * @throws NullPointerException if {@code message} or {@code address} are null
   * @throws IllegalStateException if the bus has not been started
   */
  void publish(Object message, String address);

  /**
   * Registers the {@code handler} to handle messages for handler's class name.
   * 
   * @throws NullPointerException if {@code handler} is null
   * @throws IllegalStateException if no message type can be resolved for the {@code handler}
   */
  void register(MessageHandler<?> handler);

  /**
   * Registers the {@code handler} for the {@code address} with the bus.
   * 
   * @throws NullPointerException if {@code handler} or {@code address} are null
   * @throws IllegalStateException if no {@link ChannelAdapter} is registered for the
   *           {@code address}
   */
  void register(MessageHandler<?> handler, String address);

  /**
   * Unregisters the {@code handler} for the handler's class name.
   * 
   * @throws NullPointerException if {@code handler} is null
   */
  void unregister(MessageHandler<?> handler);

  /**
   * Unregisters the {@code handler} for the {@code address} from the bus.
   * 
   * @throws NullPointerException if {@code handler} or {@code address} are null
   */
  void unregister(MessageHandler<?> handler, String address);
}
