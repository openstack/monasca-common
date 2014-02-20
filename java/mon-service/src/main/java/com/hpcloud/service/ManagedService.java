package com.hpcloud.service;

/**
 * Support class for managed services.
 * 
 * @author Jonathan Halterman
 */
public abstract class ManagedService implements Service {
  private boolean running;
  private final String serviceName;

  protected ManagedService(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * Implementors should call this method after their service has been started.
   */
  @Override
  public void start() throws Exception {
    running = true;
  }

  /**
   * Implementors should call this method in a finally block after their service has been stopped.
   */
  @Override
  public void stop() throws Exception {
    running = false;
  }

  /**
   * Returns whether the service is running.
   */
  protected boolean isRunning() {
    return running;
  }

  /**
   * @throws IllegalStateException if the service is not running
   */
  protected void checkIsRunning() {
    if (!running)
      throw new IllegalStateException("The " + serviceName + " is not currently running.");
  }
}
