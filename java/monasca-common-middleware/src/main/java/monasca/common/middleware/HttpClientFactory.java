/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package monasca.common.middleware;

import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * An HTTP factory.
 *
 * @author liemmn
 */
public class HttpClientFactory extends AuthClientFactory {
  private HttpClientPoolFactory clientPool;

  HttpClientFactory(String host, int port, boolean useHttps, int timeout, boolean clientAuth,
    String keyStore, String keyPass, String trustStore,
    String trustPass, String adminToken, int maxActive,
    long timeBetweenEvictionRunsMillis, long minEvictableIdleTimeMillis) {
    clientPool = new HttpClientPoolFactory(host, port, useHttps, timeout, clientAuth,
      keyStore, keyPass, trustStore, trustPass, adminToken,
      maxActive, timeBetweenEvictionRunsMillis,
      minEvictableIdleTimeMillis);
    pool = new GenericObjectPool(clientPool);
  }

  @Override
  public void shutdown() {
    clientPool.shutDown();
    super.shutdown();
  }
}
