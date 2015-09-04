/*
 * Copyright 2015 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package monasca.common.hibernate.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.common.base.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import monasca.common.hibernate.core.Persistable;

/**
 * <b>Abstract</b> implementation for {@link Persistable}.
 *
 * Defines primary key of a specific entity.
 * Primary key may take any arbitrary type that
 * is required by specific case.
 *
 * @param <T> primary key type
 *
 * @see AbstractAuditablePersistable
 */
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
abstract class AbstractPersistable<T extends Serializable>
    implements Persistable<T> {
  private static final long serialVersionUID = -4841075518435739989L;

  @Id
  @Column(name = "id", length = 36)
  protected T id;

  AbstractPersistable() {
    super();
  }

  AbstractPersistable(final T id) {
    this();
    this.id = id;
  }

  @Override
  public T getId() {
    return id;
  }

  @Override
  public Persistable<T> setId(final T id) {
    this.id = id;
    return this;
  }

  @Override
  public boolean isNew() {
    return this.id == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractPersistable that = (AbstractPersistable) o;

    return Objects.equal(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .toString();
  }
}
