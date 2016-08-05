/*
 * Copyright 2015 FUJITSU LIMITED
 * (C) Copyright 2016 Hewlett Packard Enterprise Development LP
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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;

import monasca.common.model.alarm.AlarmNotificationMethodType;

@Entity
@Table(name = "notification_method")
@NamedQueries({
    @NamedQuery(
        name = NotificationMethodDb.Queries.NOTIFICATION_BY_TENANT_ID_AND_NAME,
        query = "from NotificationMethodDb where tenant_id = :tenantId and name = :name"
    ),
    @NamedQuery(
        name = NotificationMethodDb.Queries.FIND_BY_TENANT_ID_AND_ID,
        query = "from NotificationMethodDb where tenant_id = :tenantId and id = :id"
    ),
    @NamedQuery(
        name = NotificationMethodDb.Queries.DELETE_BY_ID,
        query = "delete from NotificationMethodDb where id = :id"
    )
})
public class NotificationMethodDb
    extends AbstractAuditablePersistable<String> {
  private static final long serialVersionUID = 106455752028781371L;
  @Column(name = "tenant_id", length = 36, nullable = false)
  private String tenantId;

  @Column(name = "name", length = 250)
  private String name;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "address", length = 512, nullable = false)
  private String address;

  @Column(name = "period", nullable = false)
  private Integer period;

  public NotificationMethodDb() {
    super();
  }

  public NotificationMethodDb(String id,
                              String tenantId,
                              String name,
                              String type,
                              String address,
                              Integer period,
                              DateTime created_at,
                              DateTime updated_at) {
    super(id, created_at, updated_at);
    this.tenantId = tenantId;
    this.name = name;
    this.type = type;
    this.address = address;
    this.period = period;
  }

  public NotificationMethodDb(String id,
                              String tenantId,
                              String name,
                              String type,
                              String address,
                              DateTime created_at,
                              DateTime updated_at) {
    super(id, created_at, updated_at);
    this.tenantId = tenantId;
    this.name = name;
    this.type = type;
    this.address = address;
    this.period = 0;
  }

  public NotificationMethodDb setAddress(final String address) {
    this.address = address;
    return this;
  }

  public NotificationMethodDb setType(final String type) {
    this.type = type;
    return this;
  }

  public NotificationMethodDb setName(final String name) {
    this.name = name;
    return this;
  }

  public NotificationMethodDb setTenantId(final String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public NotificationMethodDb setPeriod(final Integer period) {
    this.period = period;
    return this;
  }

  public String getTenantId() {
    return this.tenantId;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public String getAddress() {
    return this.address;
  }

  public Integer getPeriod() {
    return this.period;
  }

  public interface Queries {
    String NOTIFICATION_BY_TENANT_ID_AND_NAME = "NotificationMethod.finByTenantIdAndName";
    String DELETE_BY_ID = "NotificationMethod.deleteById";
    String FIND_BY_TENANT_ID_AND_ID = "NotificationMethod.findByTenantIdAndId";
  }

}
