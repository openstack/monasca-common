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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.common.base.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import monasca.common.hibernate.core.Persistable;
import monasca.common.hibernate.type.BinaryId;
import monasca.common.hibernate.type.BinaryIdType;

@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@TypeDef(
    name = "monasca.common.hibernate.type.BinaryId",
    typeClass = BinaryIdType.class
)
abstract class AbstractUUIDPersistable
    implements Persistable<BinaryId> {
  private static final long serialVersionUID = 6192850092568538880L;
  @Id
  @Type(type = "monasca.common.hibernate.type.BinaryId")
  @Column(name = "id", length = 20, updatable = false, nullable = false)
  protected BinaryId id;

  AbstractUUIDPersistable() {
    super();
    this.id = null;
  }

  AbstractUUIDPersistable(final BinaryId id) {
    this.id = id;
  }

  protected AbstractUUIDPersistable(final byte[] id) {
    this(new BinaryId(id));
  }

  @Override
  public BinaryId getId() {
    return this.id;
  }

  @Override
  public Persistable<BinaryId> setId(final BinaryId id) {
    this.id = id;
    return this;
  }

  @Override
  public boolean isNew() {
    return this.id == null || this.id.getBytes() == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractUUIDPersistable that = (AbstractUUIDPersistable) o;

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
