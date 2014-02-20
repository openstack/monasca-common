package com.hpcloud.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Factory methods for producing thread pools.
 * 
 * @author Jonathan Halterman
 */
public final class ThreadPools {
  private ThreadPools() {
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(String name) {
    return new InstrumentedThreadPoolExecutor(name, 0, Integer.MAX_VALUE, 60l, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat(name + "-%s")
            .build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(String name,
      int maxPoolSize) {
    return new InstrumentedThreadPoolExecutor(name, 0, maxPoolSize, 60l, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat(name + "-%s")
            .build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedCachedThreadPool(String name,
      int corePoolSize, int maxPoolSize) {
    return new InstrumentedThreadPoolExecutor(name, corePoolSize, maxPoolSize, 60l,
        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
        new ThreadFactoryBuilder().setNameFormat(name + "-%s").build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedFixedThreadPool(String name,
      int threadCount) {
    return new InstrumentedThreadPoolExecutor(name, threadCount, threadCount, 0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat(name + "-%s")
            .build());
  }

  public static InstrumentedThreadPoolExecutor newInstrumentedSingleThreadPool(String name) {
    return new InstrumentedThreadPoolExecutor(name, 1, 1, 0l, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat(name + "-%s")
            .build());
  }
}
