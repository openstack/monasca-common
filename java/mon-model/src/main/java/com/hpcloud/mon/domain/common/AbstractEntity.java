package com.hpcloud.mon.domain.common;

import java.io.Serializable;

/**
 * Defines an entity with a surrogate key.
 * 
 * @author Jonathan Halterman
 * @see http://domaindrivendesign.org/search/node/Entity
 */
public abstract class AbstractEntity implements Serializable {
  private static final long serialVersionUID = -7055330640094842914L;

  protected String id;

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractEntity other = (AbstractEntity) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
}
