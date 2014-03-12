package com.hpcloud.configuration;

import java.util.Properties;

public class kafkaProducerProperties {

    public static Properties createKafkaProperties(KafkaProducerConfiguration kafkaProducerConfiguration) {
        Properties properties = new Properties();

        properties.put("metadata.broker.list", kafkaProducerConfiguration.getMetadataBrokerList());
        properties.put("request.required.acks", kafkaProducerConfiguration.getRequestRequiredAcks().toString());
        properties.put("request.timeout.ms", kafkaProducerConfiguration.getRequestTimeoutMs().toString());
        properties.put("producer.type", kafkaProducerConfiguration.getProducerType());
        properties.put("serializer.class", kafkaProducerConfiguration.getSerializerClass());
        properties.put("key.serializer.class", kafkaProducerConfiguration.getKeySerializerClass());
        properties.put("partitioner.class", kafkaProducerConfiguration.getPartitionerClass());
        properties.put("compression.codec", kafkaProducerConfiguration.getCompressionCodec());
        properties.put("compressed.topics", kafkaProducerConfiguration.getCompressedTopics());
        properties.put("message.send.max.retries", kafkaProducerConfiguration.getMessageSendMaxRetries().toString());
        properties.put("retry.backoff.ms", kafkaProducerConfiguration.getRetryBackoffMs().toString());
        properties.put("topic.metadata.refresh.interval.ms", kafkaProducerConfiguration.getTopicMetadataRefreshIntervalMs().toString());
        properties.put("queue.buffering.max.ms", kafkaProducerConfiguration.getQueueBufferingMaxMs().toString());
        properties.put("queue.buffering.max.messages", kafkaProducerConfiguration.getQueueBufferingMaxMessages().toString());
        properties.put("queue.enqueue.timeout.ms", kafkaProducerConfiguration.getQueueEnqueueTimeoutMs().toString());
        properties.put("batch.num.messages", kafkaProducerConfiguration.getBatchNumMessages().toString());
        properties.put("send.buffer.bytes", kafkaProducerConfiguration.getSendBufferBytes().toString());
        properties.put("client.id", kafkaProducerConfiguration.getClientId());

        return properties;
    }
}
