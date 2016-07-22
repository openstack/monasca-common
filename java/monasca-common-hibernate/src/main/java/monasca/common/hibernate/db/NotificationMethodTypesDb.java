/*
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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;

import monasca.common.model.alarm.AlarmNotificationMethodType;

@Entity
@Table(name = "notification_method_type")
public class NotificationMethodTypesDb {
  private static final long serialVersionUID = 106453452028781371L;

  @Id
  @Column(name = "name", length = 20)
  private String name;

  public NotificationMethodTypesDb() {
  }

  public NotificationMethodTypesDb(String name){
    this.name = name;
  }


  public void setName(final String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
