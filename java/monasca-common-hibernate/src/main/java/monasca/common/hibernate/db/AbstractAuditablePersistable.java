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
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.google.common.base.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import monasca.common.hibernate.core.AuditablePersistable;

@DynamicInsert
@DynamicUpdate
@MappedSuperclass
abstract class AbstractAuditablePersistable<T extends Serializable>
    extends AbstractPersistable<T>
    implements AuditablePersistable<T> {
  static final String DATE_TIME_TYPE = "org.jadira.usertype.dateandtime.joda.PersistentDateTime";
  static final String DB_ZONE = "UTC";
  static final String JAVA_ZONE = "jvm";
  private static final long serialVersionUID = 2335373173379564615L;

  @Column(name = "created_at", nullable = false)
  @Type(
      type = DATE_TIME_TYPE,
      parameters = {
          @Parameter(name = "databaseZone", value = DB_ZONE),
          @Parameter(name = "javaZone", value = JAVA_ZONE)
      }
  )
  private DateTime createdAt;

  @Version
  @Column(name = "updated_at", nullable = false)
  @Type(
      type = DATE_TIME_TYPE,
      parameters = {
          @Parameter(name = "databaseZone", value = DB_ZONE),
          @Parameter(name = "javaZone", value = JAVA_ZONE)
      }
  )
  private DateTime updatedAt;

  AbstractAuditablePersistable() {
    this(null, null, null);
  }

  AbstractAuditablePersistable(final T id) {
    this(id, null, null);
  }

  AbstractAuditablePersistable(final T id,
                               final DateTime createdAt,
                               final DateTime updatedAt) {
    super(id);
    this.setDates(createdAt, updatedAt);
  }

  @Override
  public DateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public AuditablePersistable<T> setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @Override
  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public AuditablePersistable<T> setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Ensures that both {@link #createdAt} and {@link #updatedAt} will be
   * set to the earliest possible value in case passssed values are {@code NULL}
   *
   * @param createdAt created date
   * @param updatedAt updated date
   */
  private void setDates(final DateTime createdAt,
                        final DateTime updatedAt) {
    if (createdAt == null && updatedAt == null) {
      this.updatedAt = DateTime.now();
      this.createdAt = DateTime.now();
    } else if (createdAt == null) {
      this.createdAt = DateTime.now();
    } else if (updatedAt == null) {
      this.updatedAt = DateTime.now();
    } else {
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("createdAt", createdAt)
        .add("updatedAt", updatedAt)
        .toString();
  }
}
