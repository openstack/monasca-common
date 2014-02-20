package com.hpcloud.messaging.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

import com.google.common.base.Preconditions;
import com.hpcloud.messaging.ChannelAdapter;
import com.hpcloud.messaging.MessageTranslator;

/**
 * Standard channel adapter implementation.
 * 
 * @param <I> inbound message data type
 * @param <O> outbound message data type
 * @author Jonathan Halterman
 */
@ThreadSafe
public class StandardChannelAdapter<I, O> implements ChannelAdapter<I, O> {
  private final Map<String, MessageTranslator<I, I>> inboundTranslators = new ConcurrentHashMap<String, MessageTranslator<I, I>>();
  protected final Map<String, MessageTranslator<O, O>> outboundTranslators = new ConcurrentHashMap<String, MessageTranslator<O, O>>();
  private final Class<I> inboundType;
  private final Class<O> outboundType;

  /**
   * @throws NullPointerException if {@code inboundType} or {@code outboundType} are null
   */
  public StandardChannelAdapter(Class<I> inboundType, Class<O> outboundType) {
    Preconditions.checkNotNull(inboundType, "inboundType");
    Preconditions.checkNotNull(outboundType, "outboundType");
    this.inboundType = inboundType;
    this.outboundType = outboundType;
  }

  @SuppressWarnings("unchecked")
  protected StandardChannelAdapter() {
    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(ChannelAdapter.class, getClass());
    Preconditions.checkArgument(typeArguments[0] != Unknown.class
        && typeArguments[1] != Unknown.class,
        "Must declare inbound type argument <I> and outbound type argument <O> for ChannelAdapter");
    inboundType = (Class<I>) typeArguments[0];
    outboundType = (Class<O>) typeArguments[1];
  }

  @Override
  public I adaptInboundMessage(I messageBody, String topic) {
    MessageTranslator<I, I> translator = inboundTranslators.get(topic);
    if (translator != null)
      try {
        messageBody = translator.translate(messageBody);
      } catch (Exception ignore) {
      }
    return messageBody;
  }

  @Override
  public O adaptOutboundMessage(O messageBody, String topic) {
    MessageTranslator<O, O> translator = outboundTranslators.get(topic);
    if (translator != null)
      try {
        messageBody = translator.translate(messageBody);
      } catch (Exception ignore) {
      }
    return messageBody;
  }

  @Override
  public void registerInboundTranslator(MessageTranslator<I, I> translator, String topic) {
    Preconditions.checkNotNull(translator, "translator");
    Preconditions.checkNotNull(topic, "topic");
    Preconditions.checkArgument(translator.inputType().equals(inboundType)
        && translator.outputType().equals(inboundType),
        "The translator's input and output types must be %s", inboundType.getClass().getName());
    inboundTranslators.put(topic, translator);
  }

  @Override
  public void registerOutboundTranslator(MessageTranslator<O, O> translator, String topic) {
    Preconditions.checkNotNull(translator, "translator");
    Preconditions.checkNotNull(topic, "topic");
    Preconditions.checkArgument(translator.inputType().equals(outboundType)
        && translator.outputType().equals(outboundType),
        "The translator's input and output types must be %s", outboundType.getClass().getName());
    outboundTranslators.put(topic, translator);
  }

  @Override
  public Class<I> inboundType() {
    return inboundType;
  }

  @Override
  public Class<O> outboundType() {
    return outboundType;
  }
}
