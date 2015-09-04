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
import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.google.common.base.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import monasca.common.hibernate.core.AuditablePersistable;

/**
 * <b>Abstract</b> implementation for {@link AuditablePersistable}.
 *
 * Defines auditable information such as:
 * <ol>
 * <li>{@link #createdAt} - creation date for entity</li>
 * <li>{@link #updatedAt} - last update date for entity</li>
 * </ol>
 *
 * @param <T> primary key type
 *
 * @see AbstractPersistable
 */
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
abstract class AbstractAuditablePersistable<T extends Serializable>
    extends AbstractPersistable<T>
    implements AuditablePersistable<T> {
  private static final long serialVersionUID = 2335373173379564615L;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Version
  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;

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

  /**
   * Null-safe method that transform {@link DateTime} into plain {@link java.util.Date}
   *
   * @param value DateTime instance
   *
   * @return plain Date
   *
   * @see #nullSafeGetDate(Date)
   */
  static Date nullSafeSetDate(@Nullable final DateTime value) {
    if (value == null) {
      return null;
    }
    return value.toDateTime(DateTimeZone.UTC).toDate();
  }

  /**
   * Null-safe method that transform {@link Date} into plain {@link DateTime}
   *
   * @param value Date instance
   *
   * @return DateTime
   *
   * @see #nullSafeSetDate(DateTime)
   */
  static DateTime nullSafeGetDate(@Nullable final Date value) {
    if (value == null) {
      return null;
    }
    return new DateTime(value.getTime(), DateTimeZone.UTC);
  }

  @Override
  public DateTime getCreatedAt() {
    return nullSafeGetDate(this.createdAt);
  }

  @Override
  public AuditablePersistable<T> setCreatedAt(DateTime createdAt) {
    this.createdAt = nullSafeSetDate(createdAt);
    return this;
  }

  @Override
  public DateTime getUpdatedAt() {
    return nullSafeGetDate(this.updatedAt);
  }

  @Override
  public AuditablePersistable<T> setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = nullSafeSetDate(updatedAt);
    return this;
  }

  /**
   * Ensures that both {@link #createdAt} and {@link #updatedAt} will be
   * set to the earliest possible value in case passed values are {@code NULL}
   *
   * @param createdAt created date
   * @param updatedAt updated date
   */
  private void setDates(final DateTime createdAt,
                        final DateTime updatedAt) {
    final Date date = DateTime.now(DateTimeZone.UTC).toDate();
    if (createdAt == null) {
      this.createdAt = date;
    } else {
      this.createdAt = createdAt.toDateTime(DateTimeZone.UTC).toDate();
    }
    if (updatedAt == null) {
      this.updatedAt = date;
    } else {
      this.updatedAt = updatedAt.toDateTime(DateTimeZone.UTC).toDate();
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
