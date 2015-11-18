/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
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
 */
package monasca.common.middleware;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config implements AuthConstants {

  // Thee faithful logger
  private static final Logger logger = LoggerFactory
    .getLogger(Config.class);

  private static final Config instance = new Config();

  public static final String TOKEN = "token";
  public static final String PASSWORD = "password";

  // Application wide init param -- ServletContext
  private ServletContext context = null;

  private TokenCache client = null;

  // Auth client factory
  private AuthClientFactory factory = null;

  //the time to cache token
  private long timeToCacheToken;

  // flag to set if auth decision can be delegated to next filter
  private boolean delayAuthDecision;

  // retries and pauseTime configuration for retry logic
  private int retries;
  private int pauseTime;

  // flag to include catalog in the response
  private boolean includeCatalog;

  // configuration for admin authentication method to be used for 2-way SSL
  private String adminAuthMethod;

  // configuration for admin default project
  private String adminProjectId;
  private String adminProjectName;
  private String adminUserDomainId;
  private String adminUserDomainName;
  private String adminProjectDomainId;
  private String adminProjectDomainName;

  // flag to indicate if the filter is already intialized with required parameters
  private volatile boolean initialized = false;

  //context is not getting properly filed so will use FilterConfig
  private FilterConfig filterConfig;

  private Config() {
  }

  public static Config getInstance() {
    return instance;
  }

  public synchronized void initialize(FilterConfig config, ServletRequest req) throws ServletException {
    this.context = config.getServletContext();
    this.filterConfig = config;

    try {

      // Initialize auth server connection parameters...

      String host = filterConfig.getInitParameter(SERVER_VIP);

      int port = Integer.parseInt(filterConfig.getInitParameter(SERVER_PORT));

      // Initialize Certificates

      String keyStore = filterConfig.getInitParameter(KEYSTORE);
      String keyPass = filterConfig.getInitParameter(KEYSTORE_PASS);
      String trustStore = filterConfig.getInitParameter(TRUSTSTORE);
      String trustPass = filterConfig.getInitParameter(TRUSTSTORE_PASS);

      String adminToken = getValue(ADMIN_TOKEN, "");
      final boolean useHttps = getValue(USE_HTTPS, false);
      int timeout = getValue(CONN_TIMEOUT, 0);
      boolean clientAuth = getValue(CONN_SSL_CLIENT_AUTH, true);
      int maxActive = getValue(CONN_POOL_MAX_ACTIVE, 3);
      int maxIdle = getValue(CONN_POOL_MAX_IDLE, 3);
      long evictPeriod = getValue(CONN_POOL_EVICT_PERIOD, 60000L);
      long minIdleTime = getValue(CONN_POOL_MIN_IDLE_TIME, 90000L);
      retries = getValue(CONN_TIMEOUT_RETRIES, 3);
      pauseTime = getValue(PAUSE_BETWEEN_RETRIES, 100);
      delayAuthDecision = getValue(DELAY_AUTH_DECISION, false);
      includeCatalog = getValue(INCLUDE_SERVICE_CATALOG, true);
      adminAuthMethod = getValue(ADMIN_AUTH_METHOD, "");
      adminProjectId = getValue(ADMIN_PROJECT_ID, "");
      adminProjectName = getValue(ADMIN_PROJECT_NAME, "");
      adminUserDomainId = getValue(ADMIN_USER_DOMAIN_ID, "");
      adminUserDomainName = getValue(ADMIN_USER_DOMAIN_NAME, "");
      adminProjectDomainId = getValue(ADMIN_PROJECT_DOMAIN_ID, "");
      adminProjectDomainName = getValue(ADMIN_PROJECT_DOMAIN_NAME, "");
      timeToCacheToken = getValue(TIME_TO_CACHE_TOKEN, 600);
      long maxTokenCacheSize = getValue(MAX_TOKEN_CACHE_SIZE, 1048576);

      this.factory = AuthClientFactory.build(host, port, useHttps, timeout,
        clientAuth, keyStore, keyPass, trustStore, trustPass,
        maxActive, maxIdle, evictPeriod, minIdleTime, adminToken);

      verifyRequiredParamsForAuthMethod();
      this.client = new TokenCache(maxTokenCacheSize, timeToCacheToken);
      logger.info("Using https {}", useHttps);
      if (useHttps) {
        logger.info("Auth host (2-way SSL: " + clientAuth + "): " + host);
      }
      logger.info("Read Servlet Initialization Parameters ");
      initialized = true;
    } catch (Throwable t) {
      logger.error("Failure initializing connection to authentication endpoint : {}",
        t.getMessage());
      throw new ServletException(
        "Failure initializing connection to authentication endpoint  :: "
          + t.getMessage(), t);
    }
  }

  private boolean isEmpty(final String value) {
    return value == null || value.isEmpty();
  }

  public boolean isInitialized() {
    return initialized;
  }

  protected String getAdminProjectId() {
    return adminProjectId;
  }

  protected String getAdminProjectName(){
    return adminProjectName;
  }

  protected String getAdminUserDomainId(){
    return adminUserDomainId;
  }

  protected String getAdminUserDomainName(){
    return adminUserDomainName;
  }

  protected String getAdminProjectDomainId(){
    return adminProjectDomainId;
  }

  protected String getAdminProjectDomainName(){
    return adminProjectDomainName;
  }

  protected String getAdminAccessKey() {
    if (context.getAttribute(ADMIN_ACCESS_KEY) != null) {
      return (String) context.getAttribute(ADMIN_ACCESS_KEY);
    } else {
      return getValue(ADMIN_ACCESS_KEY, "");
    }
  }

  protected String getAdminSecretKey() {
    if (context.getAttribute(ADMIN_SECRET_KEY) != null) {
      return (String) context.getAttribute(ADMIN_SECRET_KEY);
    } else {
      return getValue(ADMIN_SECRET_KEY, "");
    }
  }

  protected String getAdminToken() {
    return getValue(ADMIN_TOKEN, "");
  }

  protected String getAdminAuthMethod() {
    return adminAuthMethod;
  }

  protected String getAdminUser() {
    if (context.getAttribute(ADMIN_USER) != null) {
      return (String) context.getAttribute(ADMIN_USER);
    } else {
      return getValue(ADMIN_USER, "");
    }
  }

  protected String getAdminPassword() {
    if (context.getAttribute(ADMIN_PASSWORD) != null) {
      String password = (String) context.getAttribute(ADMIN_PASSWORD);
      return password;
    } else {
      return getValue(ADMIN_PASSWORD, "");
    }
  }

  protected boolean isIncludeCatalog() {
    return includeCatalog;
  }

  protected ServletContext getConfig() {
    return context;
  }

  protected TokenCache getClient() {
    return client;
  }

  protected AuthClientFactory getFactory() {
    return factory;
  }

  protected boolean isDelayAuthDecision() {
    return delayAuthDecision;
  }

  protected int getRetries() {
    return retries;
  }

  protected int getPauseTime() {
    return pauseTime;
  }

  public long getTimeToCacheToken() {
    return timeToCacheToken;
  }

  public void setTimeToCacheToken(long timeToCachedToken) {
    this.timeToCacheToken = timeToCachedToken;
  }

  public void setClient(TokenCache client) {
    this.client = client;
  }

  @SuppressWarnings("unchecked")
  private <T> T getValue(String paramName, T defaultValue) {
    Class<?> type = defaultValue.getClass();

    String initparamValue = filterConfig.getInitParameter(paramName);
    if (!isEmpty(initparamValue)) {
      if (type.equals(Integer.class)) {
        int paramValue = Integer.parseInt(initparamValue);
        return (T) type.cast(paramValue);
      } else if (type.equals(Long.class)) {
        long paramValue = Long.parseLong(initparamValue);
        return (T) type.cast(paramValue);
      } else if (type.equals(Boolean.class)) {
        boolean paramValue = Boolean.parseBoolean(initparamValue);
        return (T) type.cast(paramValue);
      } else if (type.equals(String.class)) {
        return (T) type.cast(initparamValue);
      }
    }
    return defaultValue;
  }

  private void verifyRequiredParamsForAuthMethod() {
    if (isEmpty(getAdminAuthMethod()) || getAdminAuthMethod().equalsIgnoreCase(TOKEN)) {
      if (isEmpty(getAdminToken())) {
        throw new AdminAuthException(String.format(
            "adminToken must be set if adminAuthMethod is %s.", TOKEN));
      }
    } else if (getAdminAuthMethod().equalsIgnoreCase(PASSWORD)) {
      if (isEmpty(getAdminUser()) || isEmpty(getAdminPassword())) {
        throw new AdminAuthException(String.format(
            "adminUser and adminPassword must be set if adminAuthMethod is %s.", PASSWORD));
      }
    } else {
      throw new AdminAuthException(String.format(
          "Unrecognized value '%s' for adminAuthMethod. Valid values are %s or %s",
          getAdminAuthMethod(), TOKEN, PASSWORD));
    }
  }
}
