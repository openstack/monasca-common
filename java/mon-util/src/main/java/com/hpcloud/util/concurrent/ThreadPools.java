package com.hpcloud.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Factory methods for producing thread pools.
 * 
 * @author Jonathan Halterman
 */
public final class ThreadPools {
  private ThreadPools() {
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(
      MetricRegistry metricRegistry, String name) {
    return new InstrumentedThreadPoolExecutor(metricRegistry, name, 0, Integer.MAX_VALUE, 60l,
        TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(
      MetricRegistry metricRegistry, String name, int maxPoolSize) {
    return new InstrumentedThreadPoolExecutor(metricRegistry, name, 0, maxPoolSize, 60l,
        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(
      MetricRegistry metricRegistry, String name, int corePoolSize, int maxPoolSize) {
    return new InstrumentedThreadPoolExecutor(metricRegistry, name, corePoolSize, maxPoolSize, 60l,
        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedFixedThreadPool(
      MetricRegistry metricRegistry, String name, int threadCount) {
    return new InstrumentedThreadPoolExecutor(metricRegistry, name, threadCount, threadCount, 0L,
        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedSingleThreadPool(
      MetricRegistry metricRegistry, String name) {
    return new InstrumentedThreadPoolExecutor(metricRegistry, name, 1, 1, 0l,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }
}
