package com.hpcloud.service;

/**
 * Defines a service capable of being started and stopped.
 * 
 * @author Jonathan Halterman
 */
public interface Service {
  /** Starts the service. */
  void start() throws Exception;

  /** Stops the service. */
  void stop() throws Exception;
}
