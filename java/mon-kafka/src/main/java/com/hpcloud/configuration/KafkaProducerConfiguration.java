package com.hpcloud.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaProducerConfiguration {

    @JsonProperty
    String topic;

    @JsonProperty
    String metadataBrokerList;

    @JsonProperty
    Integer requestRequiredAcks;

    @JsonProperty
    Integer requestTimeoutMs;

    @JsonProperty
    String producerType;

    @JsonProperty
    String serializerClass;

    @JsonProperty
    String keySerializerClass;

    @JsonProperty
    String partitionerClass;

    @JsonProperty
    String compressionCodec;

    @JsonProperty
    String compressedTopics;

    @JsonProperty
    Integer messageSendMaxRetries;

    @JsonProperty
    Integer retryBackoffMs;

    @JsonProperty
    Integer topicMetadataRefreshIntervalMs;

    @JsonProperty
    Integer queueBufferingMaxMs;

    @JsonProperty
    Integer queueBufferingMaxMessages;

    @JsonProperty
    Integer queueEnqueueTimeoutMs;

    @JsonProperty
    Integer batchNumMessages;

    @JsonProperty
    Integer sendBufferBytes;

    @JsonProperty
    String clientId;


    public String getTopic() {
        return topic;
    }

    public String getMetadataBrokerList() {
        return metadataBrokerList;
    }

    public Integer getRequestRequiredAcks() {
        return requestRequiredAcks;
    }

    public Integer getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public String getProducerType() {
        return producerType;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public String getKeySerializerClass() {
        return keySerializerClass;
    }

    public String getPartitionerClass() {
        return partitionerClass;
    }

    public String getCompressionCodec() {
        return compressionCodec;
    }

    public String getCompressedTopics() {
        return compressedTopics;
    }

    public Integer getMessageSendMaxRetries() {
        return messageSendMaxRetries;
    }

    public Integer getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public Integer getTopicMetadataRefreshIntervalMs() {
        return topicMetadataRefreshIntervalMs;
    }

    public Integer getQueueBufferingMaxMs() {
        return queueBufferingMaxMs;
    }

    public Integer getQueueBufferingMaxMessages() {
        return queueBufferingMaxMessages;
    }

    public Integer getQueueEnqueueTimeoutMs() {
        return queueEnqueueTimeoutMs;
    }

    public Integer getBatchNumMessages() {
        return batchNumMessages;
    }

    public Integer getSendBufferBytes() {
        return sendBufferBytes;
    }

    public String getClientId() {
        return clientId;
    }


}
