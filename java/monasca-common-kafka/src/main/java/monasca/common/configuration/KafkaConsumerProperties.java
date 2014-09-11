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
import java.util.Properties;

public class KafkaConsumerProperties implements Serializable {

    private static final long serialVersionUID = 2369697347070190744L;

    public static Properties createKafkaProperties(KafkaConsumerConfiguration kafkaConfiguration) {
        Properties properties = new Properties();

        properties.put("group.id", kafkaConfiguration.getGroupId());
        properties.put("zookeeper.connect", kafkaConfiguration.getZookeeperConnect());
        properties.put("consumer.id", kafkaConfiguration.getConsumerId());
        properties.put("socket.timeout.ms", kafkaConfiguration.getSocketTimeoutMs().toString());
        properties.put("socket.receive.buffer.bytes", kafkaConfiguration.getSocketReceiveBufferBytes().toString());
        properties.put("fetch.message.max.bytes", kafkaConfiguration.getFetchMessageMaxBytes().toString());
        properties.put("auto.commit.enable", kafkaConfiguration.getAutoCommitEnable().toString());
        properties.put("auto.commit.interval.ms", kafkaConfiguration.getAutoCommitIntervalMs().toString());
        properties.put("queued.max.message.chunks", kafkaConfiguration.getQueuedMaxMessageChunks().toString());
        properties.put("rebalance.max.retries", kafkaConfiguration.getRebalanceMaxRetries().toString());
        properties.put("fetch.min.bytes", kafkaConfiguration.getFetchMinBytes().toString());
        properties.put("fetch.wait.max.ms", kafkaConfiguration.getFetchWaitMaxMs().toString());
        properties.put("rebalance.backoff.ms", kafkaConfiguration.getRebalanceBackoffMs().toString());
        properties.put("refresh.leader.backoff.ms", kafkaConfiguration.getRefreshLeaderBackoffMs().toString());
        properties.put("auto.offset.reset", kafkaConfiguration.getAutoOffsetReset());
        properties.put("consumer.timeout.ms", kafkaConfiguration.getConsumerTimeoutMs().toString());
        properties.put("client.id", kafkaConfiguration.getClientId());
        properties.put("zookeeper.session.timeout.ms", kafkaConfiguration.getZookeeperSessionTimeoutMs().toString());
        properties.put("zookeeper.connection.timeout.ms", kafkaConfiguration.getZookeeperConnectionTimeoutMs().toString());
        properties.put("zookeeper.sync.time.ms", kafkaConfiguration.getZookeeperSyncTimeMs().toString());

        return properties;
    }
}
