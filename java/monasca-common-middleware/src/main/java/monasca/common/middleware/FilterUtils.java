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


import static monasca.common.middleware.AuthConstants.AUTH_IDENTITY_STATUS;

import static monasca.common.middleware.AuthConstants.AUTH_ROLES;

import static monasca.common.middleware.AuthConstants.AUTH_USER_ID;
import static monasca.common.middleware.AuthConstants.AUTH_DOMAIN_ID;
import static monasca.common.middleware.AuthConstants.AUTH_DOMAIN_NAME;
import static monasca.common.middleware.AuthConstants.AUTH_PROJECT_ID;
import static monasca.common.middleware.AuthConstants.AUTH_PROJECT_NAME;
import static monasca.common.middleware.AuthConstants.AUTH_USER_NAME;
import static monasca.common.middleware.AuthConstants.IdentityStatus;
import static monasca.common.middleware.AuthConstants.AUTH_PROJECT_DOMAIN_ID;
import static monasca.common.middleware.AuthConstants.AUTH_PROJECT_DOMAIN_NAME;
import static monasca.common.middleware.AuthConstants.AUTH_USER_DOMAIN_ID;
import static monasca.common.middleware.AuthConstants.AUTH_USER_DOMAIN_NAME;
import static monasca.common.middleware.AuthConstants.AUTH_HP_IDM_ROLES;
import static monasca.common.middleware.AuthConstants.AUTH_SERVICE_CATALOG;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FilterUtils {

  private FilterUtils() {
  }

  private static final Config appConfig = Config.getInstance();

  // Thee faithful logger
  private static final Logger logger = LoggerFactory
    .getLogger(FilterUtils.class);

  public static void destroyFilter() {

    TokenCache client = appConfig.getClient();

    if (client != null)
      appConfig.setClient(null);

    AuthClientFactory factory = appConfig.getFactory();
    // Shutdown factory
    if (factory != null) {
      factory.shutdown();
    }
  }

  public static ServletRequest wrapRequestFromHttpResponse(
    ServletRequest req, String data) {
    wrapRequestFromHttpV3Response(req, data);
    return req;
  }

  private static void wrapRequestFromHttpV3Response(ServletRequest req,
    String data) {
    StringBuilder tenants = new StringBuilder();
    StringBuilder nonTenants = new StringBuilder();
    JsonParser jp = new JsonParser();
    JsonObject token = jp.parse(data).getAsJsonObject().get("token")
      .getAsJsonObject();
    // Domain Scoped Token
    if (token.get("domain") != null) {
      JsonObject domain = token.get("domain").getAsJsonObject();
      req.setAttribute(AUTH_DOMAIN_ID, domain.get("id").getAsString());
      if (domain.get("name") != null) {
        req.setAttribute(AUTH_DOMAIN_NAME, domain.get("name")
          .getAsString());
      }
    }
    // Project Scoped Token
    if (token.get("project") != null) {
      JsonObject project = token.get("project").getAsJsonObject();
      req.setAttribute(AUTH_PROJECT_ID, project.get("id").getAsString());
      req.setAttribute(AUTH_PROJECT_NAME, project.get("name")
        .getAsString());

      JsonObject projectDomain = project.get("domain").getAsJsonObject();
      // special case where the value of id is null and the
      // projectDomain.get("id") != null
      if (!projectDomain.get("id").equals(JsonNull.INSTANCE)) {
        req.setAttribute(AUTH_PROJECT_DOMAIN_ID, projectDomain
          .get("id").getAsString());
      }
      if (projectDomain.get("name") != null) {
        req.setAttribute(AUTH_PROJECT_DOMAIN_NAME,
          projectDomain.get("name"));
      }
    }
    // User info
    if (token.get("user") != null) {
      JsonObject user = token.get("user").getAsJsonObject();
      req.setAttribute(AUTH_USER_ID, user.get("id").getAsString());
      req.setAttribute(AUTH_USER_NAME, user.get("name").getAsString());

      JsonObject userDomain = user.get("domain").getAsJsonObject();
      if (userDomain.get("id") != null) {
        req.setAttribute(AUTH_USER_DOMAIN_ID, userDomain.get("id")
          .getAsString());
      }
      if (userDomain.get("name") != null) {
        req.setAttribute(AUTH_USER_DOMAIN_NAME, userDomain.get("name")
          .getAsString());
      }

    }
    // Roles
    JsonArray roles = token.getAsJsonArray("roles");
    if (roles != null) {
      Iterator<JsonElement> it = roles.iterator();
      StringBuilder roleBuilder = new StringBuilder();
      while (it.hasNext()) {

        //Changed to meet my purposes
        JsonObject role = it.next().getAsJsonObject();
        String currentRole = role.get("name").getAsString();
        roleBuilder.append(currentRole).append(",");
      }
      //My changes to meet my needs
      req.setAttribute(AUTH_ROLES, roleBuilder.toString());
    }
    String tenantRoles = (tenants.length() > 0) ? tenants.substring(1)
      : tenants.toString();
    String nonTenantRoles = (nonTenants.length() > 0) ? nonTenants
      .substring(1) : nonTenants.toString();
    if (!tenantRoles.equals("")) {
      req.setAttribute(AUTH_ROLES, tenantRoles);
    }
    if (!nonTenantRoles.equals("")) {
      req.setAttribute(AUTH_HP_IDM_ROLES, nonTenantRoles);
    }
    // Catalog
    if (token.get("catalog") != null && appConfig.isIncludeCatalog()) {
      JsonArray catalog = token.get("catalog").getAsJsonArray();
      req.setAttribute(AUTH_SERVICE_CATALOG, catalog.toString());
    }
  }

  public static ServletRequest wrapRequest(ServletRequest req, Object data) {
    if (data == null) {
      req.setAttribute(AUTH_IDENTITY_STATUS,
        IdentityStatus.Invalid.toString());
      logger.debug("Failed Authentication. Setting identity status header to Invalid");
    }
    req.setAttribute(AUTH_IDENTITY_STATUS,
      IdentityStatus.Confirmed.toString());
    if (data instanceof String) {
      wrapRequestFromHttpResponse(req, ((String) data));
    }
    return req;
  }

  // Insert token into cache
  public static void cacheToken(String token, Object auth) {
    appConfig.getClient().put(token, (String) auth);
  }

  // Get token from cache
  public static Object getCachedToken(String token) throws IOException {
    return appConfig.getClient().getToken(token);
  }

  public static void pause(long pauseTime) {
    try {
      Thread.sleep(pauseTime * 1000);
    } catch (InterruptedException e) {
      logger.debug("Thread is interrupted while sleeping before "
        + pauseTime + " seconds. ");
    }
  }
}
