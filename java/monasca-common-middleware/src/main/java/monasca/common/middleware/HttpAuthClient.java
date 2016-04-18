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

import static monasca.common.middleware.AuthConstants.TOKEN;
import static monasca.common.middleware.AuthConstants.AUTH_SUBJECT_TOKEN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class HttpAuthClient implements AuthClient {

  private static final Logger logger = LoggerFactory.getLogger(HttpAuthClient.class);

  private static final int DELTA_TIME_IN_SEC = 30;
  private static final String APPLICATION_JSON = "application/json";
  // SimpleDateFormat is not thread safe, so use ThreadLocal
  private static ThreadLocal<SimpleDateFormat> expiryFormat = new ThreadLocal<>();

  private final Config appConfig = Config.getInstance();

  private HttpClient client;
  private String adminToken;
  private String adminTokenExpiry;
  private URI uri;

  public HttpAuthClient(HttpClient client, URI uri) {
    this.client = client;
    this.uri = uri;
  }

  @Override
  public String validateTokenForServiceEndpointV3(String token) throws ClientProtocolException {
    String newUri = uri.toString() + "/v3/auth/tokens/";
    Header[] header = new Header[]{new BasicHeader(AUTH_SUBJECT_TOKEN, token)};
    return verifyUUIDToken(token, newUri, header);
  }

  private String verifyUUIDToken(String token, String newUri,
                                 Header[] headers)
      throws ClientProtocolException {

    HttpGet httpGet = new HttpGet(newUri);

    try {

      httpGet.setHeader("Accept", APPLICATION_JSON);
      httpGet.setHeader("Content-Type", APPLICATION_JSON);

      if (headers != null) {
        for (Header header : headers) {
          httpGet.setHeader(header);
        }
      }

      HttpResponse response = sendGet(httpGet);

      HttpEntity entity = response.getEntity();
      int code = response.getStatusLine().getStatusCode();

      InputStream instream;
      try {
        if (code == 404) {
          instream = entity.getContent();
          instream.close();
          //
          // Don't log the whole token, just the last ten characters
          //
          throw new AuthException("Authorization failed for user token ending with: "
             + token.substring(token.length() - 10));
        }

        if (code != 200) {
          adminToken = null;
          instream = entity.getContent();
          instream.close();
          String reasonPhrase = response.getStatusLine().getReasonPhrase();

          throw new AuthException("Failed to validate via HTTP " + code + " " + reasonPhrase);
        }
      } catch (IOException e) {
        throw new ClientProtocolException("IO Exception: problem closing stream ", e);
      }

      return parseResponse(response);

    } finally {

      httpGet.releaseConnection();

    }
  }

  private HttpResponse sendPost(HttpPost httpPost)
      throws ClientProtocolException {
    HttpResponse response;

    try {
      response = client.execute(httpPost);
      int code = response.getStatusLine().getStatusCode();
      if (!(code == 201 || code == 200 || code == 203)) {
        adminToken = null;
        throw new AdminAuthException(
            "Failed to authenticate admin credentials " + code
            + response.getStatusLine().getReasonPhrase());
      }
    } catch (IOException e) {
      final String message;
      if ((e.getMessage() == null) && (e.getCause() != null)) {
        message = e.getCause().getMessage();
      } else {
        message = e.getMessage();
      }
      logger.error("Failure authenticating adminUser: {}", message);
      httpPost.abort();
      throw new AdminAuthException(
          "Failure authenticating adminUser :" + message, e);
    }
    return response;
  }

  private HttpResponse sendGet(HttpGet httpGet)
      throws ClientProtocolException {
    HttpResponse response;

    if (appConfig.getAdminAuthMethod().equalsIgnoreCase(Config.TOKEN)) {
      httpGet.setHeader(new BasicHeader(TOKEN, appConfig.getAdminToken()));
    } else {
      httpGet.setHeader(new BasicHeader(TOKEN, getAdminToken()));
    }

    try {

      response = client.execute(httpGet);

    } catch (ConnectException c) {
      httpGet.abort();
      throw new ServiceUnavailableException(c.getMessage());
    } catch (IOException e) {
      httpGet.abort();

      throw new ClientProtocolException(
          "IO Exception during GET request ", e);
    }
    return response;
  }

  private String parseResponse(HttpResponse response) {
    StringBuffer json = new StringBuffer();
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      InputStream instream;
      try {
        instream = entity.getContent();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(instream));
        String line = reader.readLine();
        while (line != null) {
          json.append(line);
          line = reader.readLine();
        }
        instream.close();
        reader.close();
      } catch (Exception e) {
        throw new AuthException("Failed to parse Http Response ", e);
      }
    }

    return json.toString();
  }

  private String getAdminToken() throws ClientProtocolException {

    if (adminTokenExpiry != null) {
      if (isExpired(adminTokenExpiry)) {
        adminToken = null;
      }
    }

    if (adminToken == null) {

      String authUri = uri + "/v3/auth/tokens";
      HttpPost httpPost = new HttpPost(authUri);

      try {

        StringEntity params = getUnscopedV3AdminTokenRequest();
        httpPost.setHeader("Accept", APPLICATION_JSON);
        httpPost.setHeader("Content-Type", APPLICATION_JSON);
        httpPost.setEntity(params);
        HttpResponse response = sendPost(httpPost);
        adminToken = response.getFirstHeader(AUTH_SUBJECT_TOKEN).getValue();
        String json = parseResponse(response);
        JsonObject
            token =
            new JsonParser().parse(json).getAsJsonObject().get("token").getAsJsonObject();
        adminTokenExpiry = token.get("expires_at").getAsString();

      } finally {

        httpPost.releaseConnection();

      }
    }
    return adminToken;
  }

  private String buildAuth(final String userName, final String password,
                           final String projectId, final String projectName,
                           final String userDomainId, final String userDomainName,
                           final String projectDomainId, final String projectDomainName) {

    final JsonObject UserDomain = new JsonObject();
    if (!userDomainId.isEmpty()) {
      UserDomain.addProperty("id", userDomainId);
    } else if (!userDomainName.isEmpty()) {
      UserDomain.addProperty("name", userDomainName);
    } else {
      UserDomain.addProperty("id", "default");
    }
    final JsonObject user = new JsonObject();
    user.addProperty("name", userName);
    user.addProperty("password", password);
    user.add("domain", UserDomain);

    final JsonObject passwordHolder = new JsonObject();
    passwordHolder.add("user", user);

    final JsonArray methods = new JsonArray();
    methods.add(new JsonPrimitive("password"));

    final JsonObject identity = new JsonObject();
    identity.add("methods", methods);
    identity.add("password", passwordHolder);

    boolean scopeDefined = false;
    final JsonObject project = new JsonObject();
    // If project id is available, it is preferred
    if (!projectId.isEmpty()) {
      project.addProperty("id", projectId);
      scopeDefined = true;

    } else if (!projectName.isEmpty()) {
      final JsonObject ProjectDomain = new JsonObject();
      if (!projectDomainId.isEmpty()) {
        ProjectDomain.addProperty("id", projectDomainId);
      } else if (!projectDomainName.isEmpty()) {
        ProjectDomain.addProperty("name", projectDomainName);
      } else {
        ProjectDomain.addProperty("id", "default");
      }
      project.add("domain", ProjectDomain);
      project.addProperty("name", projectName);
      scopeDefined = true;
    }

    final JsonObject auth = new JsonObject();
    auth.add("identity", identity);
    if (scopeDefined) {
      final JsonObject scope = new JsonObject();
      scope.add("project", project);
      auth.add("scope", scope);
    }

    final JsonObject outer = new JsonObject();
    outer.add("auth", auth);

    return outer.toString();
  }

  private StringEntity getUnscopedV3AdminTokenRequest() {
    final String body;
    if (appConfig.getAdminAuthMethod().equalsIgnoreCase(Config.PASSWORD)) {
      body = buildAuth(appConfig.getAdminUser(), appConfig.getAdminPassword(),
                       appConfig.getAdminProjectId(), appConfig.getAdminProjectName(),
                       appConfig.getAdminUserDomainId(), appConfig.getAdminUserDomainName(),
                       appConfig.getAdminProjectDomainId(), appConfig.getAdminProjectDomainName());
    } else {
      String
          msg =
          String.format("Admin auth method %s not supported", appConfig.getAdminAuthMethod());
      throw new AdminAuthException(msg);
    }
    try {
      return new StringEntity(body);
    } catch (UnsupportedEncodingException e) {
      throw new AdminAuthException("Invalid V3 authentication request " + e);
    }
  }

  private boolean isExpired(String expires) {
    Date tokenExpiryDate;
    if (expiryFormat.get() == null) {
      expiryFormat.set(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
      expiryFormat.get().setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    try {
      // The date looks like: 2014-11-13T02:34:59.953729Z
      // SimpleDateFormat can't handle the microseconds so take them off
      final String tmp = expires.replaceAll("\\.[\\d]+Z", "Z");
      tokenExpiryDate = expiryFormat.get().parse(tmp);
    } catch (ParseException e) {
      logger.warn("Failure parsing Admin Token expiration date: {}", e.getMessage());
      return true;
    }
    Date current = new Date();
    return tokenExpiryDate.getTime() < (current.getTime() + DELTA_TIME_IN_SEC * 1000);
  }

  public void reset() {
  }

}
