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

public class KafkaProducerConfiguration implements Serializable {

    private static final long serialVersionUID = 7930028723426594128L;

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
