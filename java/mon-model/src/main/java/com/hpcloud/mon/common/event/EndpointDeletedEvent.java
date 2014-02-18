package com.hpcloud.mon.common.event;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Jonathan Halterman
 */
@JsonRootName(value = "endpoint-deleted")
public class EndpointDeletedEvent implements Serializable {
  private static final long serialVersionUID = -2100681808766534155L;

  public String tenantId;
  public String endpointId;

  public EndpointDeletedEvent() {
  }

  public EndpointDeletedEvent(String tenantId, String endpointId) {
    this.tenantId = tenantId;
    this.endpointId = endpointId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EndpointDeletedEvent other = (EndpointDeletedEvent) obj;
    if (endpointId == null) {
      if (other.endpointId != null)
        return false;
    } else if (!endpointId.equals(other.endpointId))
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
    result = prime * result + ((endpointId == null) ? 0 : endpointId.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format("EndpointDeletedEvent [tenantId=%s, endpointId=%s]", tenantId, endpointId);
  }
}
