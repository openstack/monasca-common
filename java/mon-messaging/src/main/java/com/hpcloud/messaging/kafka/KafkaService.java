package com.hpcloud.messaging.kafka;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.google.common.base.Joiner;
import com.hpcloud.service.Service;

public class KafkaService implements Service {
  private final KafkaConfiguration config;
  private Producer<String, String> producer;

  @Inject
  public KafkaService(KafkaConfiguration config) {
    this.config = config;
  }

  @Override
  public void start() throws Exception {
    Properties props = new Properties();
    props.put("metadata.broker.list", Joiner.on(',').join(config.brokerUris));
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("partitioner.class", "example.producer.SimplePartitioner");
    props.put("request.required.acks", "1");
    ProducerConfig config = new ProducerConfig(props);
    producer = new Producer<String, String>(config);
  }

  public void send(String message, Map<String, Object> meta, String topic, String key) {
    KeyedMessage<String, String> keyedMessage = new KeyedMessage<>(topic, key, message);
    producer.send(keyedMessage);
  }

  @Override
  public void stop() throws Exception {
    producer.close();
  }
}
