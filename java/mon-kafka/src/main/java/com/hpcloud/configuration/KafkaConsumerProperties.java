package com.hpcloud.configuration;

import java.util.Properties;

public class KafkaConsumerProperties {

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
