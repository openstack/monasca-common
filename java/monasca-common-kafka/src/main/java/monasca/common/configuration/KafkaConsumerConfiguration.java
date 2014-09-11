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
package monasca.common.configuration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaConsumerConfiguration implements Serializable {

    private static final long serialVersionUID = -1666706655354785832L;

    @JsonProperty
    String topic;

    @JsonProperty
    Integer numThreads;

    @JsonProperty
    String groupId;

    @JsonProperty
    String zookeeperConnect;

    @JsonProperty
    String consumerId;

    @JsonProperty
    Integer socketTimeoutMs;

    @JsonProperty
    Integer socketReceiveBufferBytes;

    @JsonProperty
    Integer fetchMessageMaxBytes;

    @JsonProperty
    Boolean autoCommitEnable;

    @JsonProperty
    Integer autoCommitIntervalMs;

    @JsonProperty
    Integer queuedMaxMessageChunks;

    @JsonProperty
    Integer rebalanceMaxRetries;

    @JsonProperty
    Integer fetchMinBytes;

    @JsonProperty
    Integer fetchWaitMaxMs;

    @JsonProperty
    Integer rebalanceBackoffMs;

    @JsonProperty
    Integer refreshLeaderBackoffMs;

    @JsonProperty
    String autoOffsetReset;

    @JsonProperty
    Integer consumerTimeoutMs;

    @JsonProperty
    String clientId;

    @JsonProperty
    Integer zookeeperSessionTimeoutMs;

    @JsonProperty
    Integer zookeeperConnectionTimeoutMs;

    @JsonProperty
    Integer zookeeperSyncTimeMs;

    public String getTopic() {
        return topic;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getZookeeperConnect() {
        return zookeeperConnect;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public Integer getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public Integer getSocketReceiveBufferBytes() {
        return socketReceiveBufferBytes;
    }

    public Integer getFetchMessageMaxBytes() {
        return fetchMessageMaxBytes;
    }

    public Boolean getAutoCommitEnable() {
        return autoCommitEnable;
    }

    public Integer getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    public Integer getQueuedMaxMessageChunks() {
        return queuedMaxMessageChunks;
    }

    public Integer getRebalanceMaxRetries() {
        return rebalanceMaxRetries;
    }

    public Integer getFetchMinBytes() {
        return fetchMinBytes;
    }

    public Integer getFetchWaitMaxMs() {
        return fetchWaitMaxMs;
    }

    public Integer getRebalanceBackoffMs() {
        return rebalanceBackoffMs;
    }

    public Integer getRefreshLeaderBackoffMs() {
        return refreshLeaderBackoffMs;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public Integer getConsumerTimeoutMs() {
        return consumerTimeoutMs;
    }

    public String getClientId() {
        return clientId;
    }

    public Integer getZookeeperSessionTimeoutMs() {
        return zookeeperSessionTimeoutMs;
    }

    public Integer getZookeeperConnectionTimeoutMs() {
        return zookeeperConnectionTimeoutMs;
    }

    public Integer getZookeeperSyncTimeMs() {
        return zookeeperSyncTimeMs;
    }

}
