package com.hpcloud.messaging;

/**
 * Dispatches messages synchronously on the current thread. Useful for testing scenarios where
 * correlation is not required.
 * 
 * @author Jonathan Halterman
 */
public class SynchronousMessageDispatcher implements MessageDispatcher {
  @Override
  public <T> void dispatch(T message, MessageHandler<T> handler) {
    handler.handle(message);
  }

  @Override
  public void start() throws Exception {
  }

  @Override
  public void stop() throws Exception {
  }
}
