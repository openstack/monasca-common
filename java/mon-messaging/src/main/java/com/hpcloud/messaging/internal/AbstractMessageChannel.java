package com.hpcloud.messaging.internal;

import com.google.common.base.Preconditions;
import com.hpcloud.messaging.MessageChannel;
import com.hpcloud.messaging.MessageDispatcher;
import com.hpcloud.messaging.MessageTranslator;

/**
 * Base message channel implementation.
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractMessageChannel implements MessageChannel {
  protected final String name;
  protected final String qualifiedName;
  protected final MessageTranslator<Object, ?> inboundTranslator;
  protected final MessageTranslator<?, Object> outboundTranslator;
  protected MessageDispatcher dispatcher;

  protected AbstractMessageChannel() {
    this(null, null, null);
  }

  protected AbstractMessageChannel(String name, MessageTranslator<Object, ?> inboundTranslator,
      MessageTranslator<?, Object> outboundTranslator) {
    this.name = name;
    this.inboundTranslator = inboundTranslator;
    this.outboundTranslator = outboundTranslator;
    qualifiedName = name == null ? "" : name + "://";
  }

  @Override
  public void bind(MessageDispatcher dispatcher) {
    Preconditions.checkNotNull(dispatcher, "dispatcher");
    this.dispatcher = dispatcher;
  }

  @Override
  public void close() {
  }

  @Override
  public MessageTranslator<?, ?> inboundTranslator() {
    return inboundTranslator;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public void open() {
  }

  @Override
  public MessageTranslator<?, ?> outboundTranslator() {
    return outboundTranslator;
  }
}
