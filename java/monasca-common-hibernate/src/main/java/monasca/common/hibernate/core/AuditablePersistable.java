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

package monasca.common.hibernate.core;

import java.io.Serializable;

import org.joda.time.DateTime;

public interface AuditablePersistable<T extends Serializable>
    extends Persistable<T> {
  /**
   * Returns {@link DateTime} when an entity was created
   *
   * @return creation time
   */
  DateTime getCreatedAt();

  /**
   * Allows to set {@code createdAt} {@link DateTime}
   *
   * @param createdAt date instance
   *
   * @return {@code self}
   */
  AuditablePersistable setCreatedAt(DateTime createdAt);

  /**
   * Returns {@link DateTime} when an entity was updated
   *
   * @return most recent update time
   */
  DateTime getUpdatedAt();

  /**
   * Allows to set {@code updatedAt} {@link DateTime}
   *
   * @param updatedAt date instance
   *
   * @return {@code self}
   */
  AuditablePersistable setUpdatedAt(DateTime updatedAt);
}
