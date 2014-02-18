package com.hpcloud.mon.common.event;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Jonathan Halterman
 */
@JsonRootName(value = "subscription-deleted")
public class SubscriptionDeletedEvent implements Serializable {
  private static final long serialVersionUID = 4380444000660995762L;

  public String tenantId;
  public String subscriptionId;
  public String endpointId;
  public String namespace;
  public Map<String, String> dimensions;

  public SubscriptionDeletedEvent() {
  }

  public SubscriptionDeletedEvent(String tenantId, String subscriptionId, String endpointId,
      String namespace, Map<String, String> dimensions) {
    this.tenantId = tenantId;
    this.subscriptionId = subscriptionId;
    this.endpointId = endpointId;
    this.namespace = namespace;
    this.dimensions = dimensions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SubscriptionDeletedEvent other = (SubscriptionDeletedEvent) obj;
    if (dimensions == null) {
      if (other.dimensions != null)
        return false;
    } else if (!dimensions.equals(other.dimensions))
      return false;
    if (endpointId == null) {
      if (other.endpointId != null)
        return false;
    } else if (!endpointId.equals(other.endpointId))
      return false;
    if (namespace == null) {
      if (other.namespace != null)
        return false;
    } else if (!namespace.equals(other.namespace))
      return false;
    if (subscriptionId == null) {
      if (other.subscriptionId != null)
        return false;
    } else if (!subscriptionId.equals(other.subscriptionId))
      return false;
    if (tenantId == null) {
      if (other.tenantId != null)
        return false;
    } else if (!tenantId.equals(other.tenantId))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dimensions == null) ? 0 : dimensions.hashCode());
    result = prime * result + ((endpointId == null) ? 0 : endpointId.hashCode());
    result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
    result = prime * result + ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "SubscriptionDeletedEvent [tenantId=%s, subscriptionId=%s, endpointId=%s, namespace=%s, dimensions=%s]",
        tenantId, subscriptionId, endpointId, namespace, dimensions);
  }
}
