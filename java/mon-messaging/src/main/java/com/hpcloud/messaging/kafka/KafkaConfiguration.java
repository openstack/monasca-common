package com.hpcloud.messaging.kafka;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Jonathan Halterman
 */
public class KafkaConfiguration {
  @NotEmpty public String[] hosts;
  @Min(1) @Max(65535) public int port = 5672;
  @NotEmpty public String username;
  @NotEmpty public String password;
}
