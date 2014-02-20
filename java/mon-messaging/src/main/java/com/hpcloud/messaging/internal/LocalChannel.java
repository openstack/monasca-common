package com.hpcloud.messaging.internal;

import javax.inject.Singleton;

/**
 * Channel adapter for messaging within the local process.
 * 
 * @author Jonathan Halterman
 */
@Singleton
public class LocalChannel extends AbstractPublishSubscribeChannel {
  /**
   * Handles outbound messages using the inbound handler.
   */
  @Override
  public void send(Object message, String address) {
    handle(message, address);
  }
}