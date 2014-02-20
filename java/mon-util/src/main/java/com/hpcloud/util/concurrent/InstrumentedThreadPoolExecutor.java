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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;

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

  InstrumentedThreadPoolExecutor(MetricRegistry metricRegistry, String name, int corePoolSize,
      int maximumPoolSize, long keepAliveTime, TimeUnit unit,
      final BlockingQueue<Runnable> workQueue, ThreadFactory factory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, factory);
    this.name = name;
    requestRate = metricRegistry.meter(MetricRegistry.name(getClass(), "request"));
    rejectedRate = metricRegistry.meter(MetricRegistry.name(getClass(), "rejected"));
    executionTimer = metricRegistry.timer(MetricRegistry.name(getClass(), "execution"));
    metricRegistry.register(MetricRegistry.name(getClass(), "queue.size"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
        return getQueue().size();
      }
    });
    metricRegistry.register(MetricRegistry.name(getClass(), "threads.count"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
        return getPoolSize();
      }
    });
    metricRegistry.register(MetricRegistry.name(getClass(), "threads.active"),
        new Gauge<Integer>() {
          @Override
          public Integer getValue() {
            return getActiveCount();
          }
        });
    metricRegistry.register(MetricRegistry.name(getClass(), "threads.idle"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
        return getPoolSize() - getActiveCount();
      }
    });
    metricRegistry.register(MetricRegistry.name(getClass(), "threads.percent-active"),
        new RatioGauge() {
          @Override
          protected Ratio getRatio() {
            return Ratio.of(getPoolSize(), getActiveCount());
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
