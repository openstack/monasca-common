package com.hpcloud.messaging.kafka;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Jonathan Halterman
 */
public class KafkaConfiguration {
  @NotEmpty public String[] zookeeperUris;
  @NotEmpty public String[] brokerUris;
  @NotEmpty public String healthCheckTopic;
}
