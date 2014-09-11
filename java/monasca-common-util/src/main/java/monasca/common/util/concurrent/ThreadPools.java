/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Factory methods for creating thread pools.
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
