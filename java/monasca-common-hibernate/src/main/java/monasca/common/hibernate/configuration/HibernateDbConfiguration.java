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
package monasca.common.hibernate.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class HibernateDbConfiguration {
  private static final String DEFAULT_HBM2DDL_AUTO_VALUE = "validate";

  @NotNull
  @JsonProperty
  boolean supportEnabled;
  @NotEmpty
  @JsonProperty
  String providerClass;
  @NotEmpty
  @JsonProperty
  String dataSourceClassName;
  @NotEmpty
  @JsonProperty
  String user;
  @NotEmpty
  @JsonProperty
  String password;

  @JsonProperty
  String initialConnections;
  @JsonProperty
  String maxConnections;
  @JsonProperty
  String autoConfig;
  @JsonProperty
  String dataSourceUrl;
  @JsonProperty
  String serverName;
  @JsonProperty
  String portNumber;
  @JsonProperty
  String databaseName;

  public String getDataSourceUrl() {
    return this.dataSourceUrl;
  }

  public boolean getSupportEnabled() {
    return supportEnabled;
  }

  public String getProviderClass() {
    return providerClass;
  }

  public void setProviderClass(String providerClass) {
    this.providerClass = providerClass;
  }

  public String getDataSourceClassName() {
    return dataSourceClassName;
  }

  public String getServerName() {
    return serverName;
  }

  public String getPortNumber() {
    return portNumber;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getInitialConnections() {
    return initialConnections;
  }

  public String getMaxConnections() {
    return maxConnections;
  }

  /**
   * Returns {@code hbm2ddl.auto} telling hibernate how to handle schema. By default will return {@code validate}.
   * For more information how each of the possible value works refer to official Hibernate documentation.
   *
   * @return {@link String} hbm2ddl.auto value
   * @see <a href="https://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch03.html#configuration-optional">3.7. Miscellaneous Properties</a>
   */
  public String getAutoConfig() {
    return this.autoConfig == null ? DEFAULT_HBM2DDL_AUTO_VALUE : this.autoConfig;
  }
}
