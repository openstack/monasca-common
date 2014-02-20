package com.hpcloud.messaging;

/**
 * A messaging channel.
 * 
 * @author Jonathan Halterman
 */
public interface MessageChannel {
  /**
   * Returns the channel's message adapter.
   */
  ChannelAdapter<?, ?> adapter();

  /**
   * Binds the dispatcher to the channel.
   * 
   * @throws NullPointerException if the {@code dispatcher} is null
   */
  void bind(MessageDispatcher dispatcher);

  /**
   * Close the channel.
   */
  void close();

  /**
   * Returns the name of channel.
   */
  String name();

  /**
   * Opens the channel.
   * 
   * @throws ChannelOpenException if the channel cannot be opened
   */
  void open();

  /**
   * Sends the {@code message} to the {@code address}.
   * 
   * @throws NullPointerException if the {@code message} or {@code address} are null
   * @throws IllegalStateException if the channel is not open
   */
  void send(Object message, String address);
}
