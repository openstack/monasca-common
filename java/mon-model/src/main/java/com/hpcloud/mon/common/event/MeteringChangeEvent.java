package com.hpcloud.mon.common.event;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author Todd Walk
 */
@JsonRootName(value = "metering-change")
public class MeteringChangeEvent implements Serializable {
  private static final long serialVersionUID = 4380444000660995888L;

  public String tenantId;
  public String monitoringLevel;

  public MeteringChangeEvent() {
  }

  public MeteringChangeEvent(String tenantId, String monitoringLevel) {
    this.tenantId = tenantId;
    this.monitoringLevel = monitoringLevel;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MeteringChangeEvent other = (MeteringChangeEvent) obj;
    if (monitoringLevel == null) {
      if (other.monitoringLevel != null)
        return false;
    } else if (!monitoringLevel.equals(other.monitoringLevel))
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
    result = prime * result + ((monitoringLevel == null) ? 0 : monitoringLevel.hashCode());
    result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return String.format("MeteringChangeEvent [tenantId=%s, monitoringLevel=%s]", tenantId,
        monitoringLevel);
  }
}
