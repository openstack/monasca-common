package com.hpcloud.messaging;

/**
 * Adapts messages sent and received via a channel.
 * 
 * @param <I> inbound message data type
 * @param <O> outbound message data type
 * @see <a href="http://eaipatterns.com/ChannelAdapter.html">Channel Adapter</a>
 * @author Jonathan Halterman
 */
public interface ChannelAdapter<I, O> {
  /**
   * Returns an adapted version of the inbound {@code messageBody} for the {@code topic}. Returns
   * {@code messageBody} if no translator is registered for the {@code topic} or if translation
   * fails.
   */
  I adaptInboundMessage(I messageBody, String topic);

  /**
   * Returns an adapted version of the outbound {@code messageBody} for the {@code topic}. Returns
   * {@code messageBody} if no translator is registered for the {@code topic} or if translation
   * fails.
   */
  O adaptOutboundMessage(O messageBody, String topic);

  /**
   * Returns the channel adapter's inbound data type.
   */
  Class<I> inboundType();

  /**
   * Returns the channel adapter's outbound data type.
   */
  Class<O> outboundType();

  /**
   * Sets the inbound {@code translator} for the {@code topic}.
   * 
   * @throws NullPointerException if {@code topic} is null
   * @throws IllegalArgumentException if {@code translator}'s input and output types do not match
   *           {@code <I>}
   */
  void registerInboundTranslator(MessageTranslator<I, I> translator, String topic);

  /**
   * Sets the outbound {@code translator} for the {@code topic}.
   * 
   * @throws NullPointerException if {@code topic} is null
   * @throws IllegalArgumentException if {@code translator}'s input and output types do not match
   *           {@code <O>}
   */
  void registerOutboundTranslator(MessageTranslator<O, O> translator, String topic);
}