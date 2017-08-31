/*
 * (c) Copyright 2017 SUSE LLC.
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
package monasca.common.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CassandraDbConfiguration {

  @JsonProperty
  int maxConnections;

  public int getMaxConnections() {
    return maxConnections;
  }

  @JsonProperty
  int maxRequests;

  public int getMaxRequests() {
    return maxRequests;
  }

  @JsonProperty
  int connectionTimeout;

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @JsonProperty
  int readTimeout;

  public int getReadTimeout() {
    return readTimeout;
  }

  @JsonProperty
  String[] contactPoints;

  public String[] getContactPoints() {
    return contactPoints;
  }

  @JsonProperty
  int port;

  public int getPort() {
    return port;
  }

  @JsonProperty
  int readConsistencyLevel;

  @JsonProperty
  String consistencyLevel;

  public String getConsistencyLevel() {
    return consistencyLevel;
  }

  @JsonProperty
  String keyspace;

  public String getKeySpace() {
    return keyspace;
  }

  @JsonProperty
  String user;

  public String getUser() {
    return user;
  }

  @JsonProperty
  String password;

  public String getPassword() {
    return password;
  }

  @JsonProperty
  long maxDefinitionCacheSize;

  public long getDefinitionMaxCacheSize() {
    return maxDefinitionCacheSize;
  }

  @JsonProperty
  int maxWriteRetries;

  public int getMaxWriteRetries() {
    return maxWriteRetries;
  }

  @JsonProperty
  int maxBatches;

  public int getMaxBatches() {
    return maxBatches;
  }

  @JsonProperty
  int retentionPolicy;

  public int getRetentionPolicy() {
    return retentionPolicy;
  }

  @JsonProperty
  String localDataCenter;

  public String getLocalDataCenter() {
    return localDataCenter;
  }
}
