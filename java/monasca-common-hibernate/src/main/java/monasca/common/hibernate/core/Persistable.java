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

/**
 * Persistable class.
 *
 * Defines most basic interface <b>Hibernate</b> entities should implement.
 */
public interface Persistable<T extends Serializable>
    extends Serializable {
  /**
   * Returns {@code is} of an entity
   *
   * @return ID of an entity
   */
  T getId();

  /**
   * Allows to set {@code id}
   *
   * @param id primary key
   *
   * @return {@code self}
   */
  Persistable setId(T id);

  /**
   * Evaluates if an entity is new or not.
   * Returns {@link Boolean#FALSE} if entity was persisted at least once
   *
   * @return true if new, false otherwise
   */
  boolean isNew();
}
