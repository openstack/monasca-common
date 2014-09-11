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

import java.util.Properties;

public class KafkaProducerProperties {

    public static Properties createKafkaProperties(KafkaProducerConfiguration kafkaProducerConfiguration) {
        Properties properties = new Properties();

        properties.put("metadata.broker.list", kafkaProducerConfiguration.getMetadataBrokerList());
        properties.put("request.required.acks", kafkaProducerConfiguration.getRequestRequiredAcks().toString());
        properties.put("request.timeout.ms", kafkaProducerConfiguration.getRequestTimeoutMs().toString());
        properties.put("producer.type", kafkaProducerConfiguration.getProducerType());
        properties.put("serializer.class", kafkaProducerConfiguration.getSerializerClass());
        setIfHasValue("key.serializer.class", kafkaProducerConfiguration.getKeySerializerClass(), properties);
        setIfHasValue("partitioner.class", kafkaProducerConfiguration.getPartitionerClass(), properties);
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

    private static void setIfHasValue(final String name, final String value, final Properties properties) {
        if ((value != null) && !value.isEmpty()) {
            properties.setProperty(name, value);
        }
    }
}
