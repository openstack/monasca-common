package com.hpcloud.util.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.util.RatioGauge;

/**
 * ThreadPoolExecutor instrumented with extensive metrics.
 * 
 * @author Jonathan Halterman
 */
public class InstrumentedThreadPoolExecutor extends ThreadPoolExecutor {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final String name;
  private final Meter requestRate;
  private final Meter rejectedRate;
  private final Timer executionTimer;
  private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
  private final Set<ExecutionListener> listeners = new HashSet<ExecutionListener>();

  InstrumentedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit unit, final BlockingQueue<Runnable> workQueue,
      ThreadFactory factory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, factory);
    this.name = name;
    requestRate = Metrics.newMeter(getClass(), "request", name, "requests", TimeUnit.SECONDS);
    rejectedRate = Metrics.newMeter(getClass(), "rejected", name, "requests", TimeUnit.SECONDS);
    executionTimer = Metrics.newTimer(getClass(), "execution", name);
    Metrics.newGauge(getClass(), "queue size", name, new Gauge<Integer>() {
      @Override
      public Integer value() {
        return getQueue().size();
      }
    });
    Metrics.newGauge(getClass(), "threads", name, new Gauge<Integer>() {
      @Override
      public Integer value() {
        return getPoolSize();
      }
    });
    Metrics.newGauge(getClass(), "active threads", name, new Gauge<Integer>() {
      @Override
      public Integer value() {
        return getActiveCount();
      }
    });
    Metrics.newGauge(getClass(), "idle threads", name, new Gauge<Integer>() {
      @Override
      public Integer value() {
        return getPoolSize() - getActiveCount();
      }
    });
    Metrics.newGauge(getClass(), "percent active", name, new RatioGauge() {
      @Override
      protected double getDenominator() {
        return getPoolSize();
      }

      @Override
      protected double getNumerator() {
        return getActiveCount();
      }
    });

    setRejectedExecutionHandler(new RejectedExecutionHandler() {
      @Override
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        rejectedRate.mark();
        if (!workQueue.offer(r))
          log.warn("Thread pool {} rejected work.", InstrumentedThreadPoolExecutor.this.name);
        throw new RejectedExecutionException();
      }
    });
  }

  public void addListener(ExecutionListener listener) {
    listeners.add(listener);
  }

  @Override
  public void execute(Runnable r) {
    requestRate.mark();
    super.execute(r);
  }

  public void removeListener(ExecutionListener listener) {
    listeners.remove(listener);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    for (ExecutionListener listener : listeners)
      listener.afterExecute(r, t);
    long duration = System.nanoTime() - startTime.get();
    executionTimer.update(duration, TimeUnit.NANOSECONDS);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    for (ExecutionListener listener : listeners)
      listener.beforeExecute(t, r);
    startTime.set(System.nanoTime());
  }
}
