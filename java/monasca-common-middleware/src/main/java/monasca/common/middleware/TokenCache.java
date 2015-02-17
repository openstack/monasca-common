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

import com.google.common.cache.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TokenCache<K, V> {

  private final LoadingCache<K, V> cache;
  private final Config appConfig = Config.getInstance();
  private AuthClientFactory factory;
  private AuthClient client;
  private static final Logger logger = LoggerFactory
    .getLogger(TokenCache.class);


  public TokenCache(final long maxSize, final long timeToExpire, final Map<String, String> map) {
    factory = appConfig.getFactory();

    cache = CacheBuilder.newBuilder().maximumSize(maxSize)
      .expireAfterWrite(timeToExpire, TimeUnit.SECONDS)
      .build(new CacheLoader<K, V>() {
        public V load(K key) throws TException, ClientProtocolException {

          V value = null;
          AuthClient client = null;

          try {
            client = factory.getClient();
            if (appConfig.getAuthVersion().equals("v2.0")) {
              value = (V) client.validateTokenForServiceEndpointV2((String) key, appConfig.getServiceIds(),
                appConfig.getEndpointIds(), appConfig.isIncludeCatalog());
            } else {
              value = (V) client.validateTokenForServiceEndpointV3((String) key, map);
            }
          } finally {
            if (client != null)
              factory.recycle(client);
          }
          return value;
        }
      });
  }

  public V getToken(K key) throws ClientProtocolException {
    V value = null;
    try {
      value = cache.get(key);
    } catch (ExecutionException e) {
      logger.info("Failed to get token", e);
      throw new ClientProtocolException(e.getMessage(), e);
    }
    return value;
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

}
