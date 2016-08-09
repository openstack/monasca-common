/*
 * (C) Copyright 2014,2016 Hewlett Packard Enterprise Development LP
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TokenCache {

  private final LoadingCache<String, String> cache;
  private final Config appConfig = Config.getInstance();
  private AuthClientFactory factory;
  private static final Logger logger = LoggerFactory
    .getLogger(TokenCache.class);


  public TokenCache(final long maxSize, final long timeToExpire) {
    factory = appConfig.getFactory();

    cache = CacheBuilder.newBuilder().maximumSize(maxSize)
      .expireAfterWrite(timeToExpire, TimeUnit.SECONDS)
      .build(new CacheLoader<String, String>() {
        public String load(String key) throws TException, ClientProtocolException {

          String value = null;
          AuthClient client = null;
          boolean discarded = false;

          try {
            client = factory.getClient();
            value = client.validateTokenForServiceEndpointV3(key);
          } catch (ClientProtocolException e) {
            factory.discard(client);
            logger.warn("Validate token failed with ClientProtocolException. Discarding AuthClient connection from pool to create a new connection.");
            logger.warn("ClientProtocolException " + e.getMessage() + " " + e);
            discarded = true;
            throw e;
          } finally {
            if (client != null && !discarded)
              factory.recycle(client);
          }
          return value;
        }
      });
  }

  public String getToken(String key) throws ClientProtocolException {
    String value = null;
    try {
      value = cache.get(key);
    } catch (ExecutionException e) {
      logger.info("Failed to get token", e);
      throw new ClientProtocolException(e.getMessage(), e);
    }
    return value;
  }

  public void put(String key, String value) {
    cache.put(key, value);
  }

}
