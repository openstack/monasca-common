package com.hpcloud.messaging.internal;

import com.google.common.base.Preconditions;
import com.hpcloud.messaging.ChannelAdapter;
import com.hpcloud.messaging.MessageChannel;
import com.hpcloud.messaging.MessageDispatcher;

/**
 * Base message channel implementation.
 * 
 * @param <I> inbound message data type
 * @param <O> outbound message data type
 * @author Jonathan Halterman
 */
public abstract class AbstractMessageChannel<I, O> implements MessageChannel {
  protected final String name;
  protected final String qualifiedName;
  protected final ChannelAdapter<I, O> adapter;
  protected MessageDispatcher dispatcher;

  protected AbstractMessageChannel() {
    this(null, null);
  }

  protected AbstractMessageChannel(String name, ChannelAdapter<I, O> adapter) {
    this.name = name;
    this.adapter = adapter;
    qualifiedName = name == null ? "" : name + "://";
  }

  @Override
  public ChannelAdapter<I, O> adapter() {
    return adapter;
  }

  @Override
  public void bind(MessageDispatcher dispatcher) {
    Preconditions.checkNotNull(dispatcher, "dispatcher");
    this.dispatcher = dispatcher;
  }

  @Override
  public String name() {
    return name;
  }
}
