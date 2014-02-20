package com.hpcloud.util.concurrent;

/**
 * Listens to execution related events.
 * 
 * @authorn Jonathan Halterman
 */
public interface ExecutionListener {
  void afterExecute(Runnable r, Throwable t);

  void beforeExecute(Thread t, Runnable r);
}