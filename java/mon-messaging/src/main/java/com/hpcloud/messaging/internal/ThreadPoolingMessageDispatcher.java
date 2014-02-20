package com.hpcloud.messaging.internal;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.hpcloud.messaging.MessageDispatcher;
import com.hpcloud.messaging.MessageHandler;
import com.hpcloud.util.Types;
import com.hpcloud.util.concurrent.ThreadPools;

/**
 * Dispatches messages on a thread pool.
 * 
 * @author Jonathan Halterman
 */
public class ThreadPoolingMessageDispatcher implements MessageDispatcher {
  private final Logger LOG = LoggerFactory.getLogger(ThreadPoolingMessageDispatcher.class);
  private final MetricRegistry metricRegistry;
  private ExecutorService executor;

  public ThreadPoolingMessageDispatcher(MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  @Override
  public <T> void dispatch(final T message, final MessageHandler<T> handler) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          if (LOG.isDebugEnabled())
            LOG.debug("Delivering message to handler {}", Types.deProxy(handler.getClass()));
          handler.handle(message);
        } catch (Exception e) {
          LOG.error("Error while handling message: {}", message, e);
        }
      }
    });
  }

  @Override
  public void start() throws Exception {
    executor = ThreadPools.newInstrumentedCachedThreadPool(metricRegistry, "message-bus");
  }

  @Override
  public void stop() throws Exception {
    executor.shutdown();
  }
}
