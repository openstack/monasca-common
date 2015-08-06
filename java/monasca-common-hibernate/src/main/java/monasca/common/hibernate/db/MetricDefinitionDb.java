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
import javax.persistence.Entity;
import javax.persistence.Table;

import monasca.common.hibernate.type.BinaryId;

@Entity
@Table(name = "metric_definition")
public class MetricDefinitionDb
    extends AbstractUUIDPersistable {
  private static final long serialVersionUID = 292896181025585969L;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  @Column(name = "tenant_id", length = 36, nullable = false)
  private String tenantId;

  @Column(name = "region", length = 255, nullable = false)
  private String region;

  public MetricDefinitionDb() {
    super();
  }

  public MetricDefinitionDb(BinaryId id, String name, String tenantId, String region) {
    super(id);
    this.name = name;
    this.tenantId = tenantId;
    this.region = region;
  }

  public MetricDefinitionDb(byte[] id, String name, String tenantId, String region) {
    this(new BinaryId(id), name, tenantId, region);
  }

  public MetricDefinitionDb setRegion(final String region) {
    this.region = region;
    return this;
  }

  public MetricDefinitionDb setTenantId(final String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public MetricDefinitionDb setName(final String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public String getTenantId() {
    return this.tenantId;
  }

  public String getRegion() {
    return this.region;
  }

}
