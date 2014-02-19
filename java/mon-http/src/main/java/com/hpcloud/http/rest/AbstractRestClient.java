package com.hpcloud.http.rest;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * REST client.
 * 
 * @author Jonathan Halterman
 */
public abstract class AbstractRestClient {
  private final String serviceName;
  private final Client client;

  @Inject
  public AbstractRestClient(String serviceName, Client client) {
    this.serviceName = serviceName;
    this.client = client;
  }

  /**
   * Creates a resource.
   * 
   * @throws RestClientException if an error occurs during the request
   * @throws UnauthorizedException if the request results in a 401
   */
  protected <T> T createResource(String uri, Class<T> resourceType, @Nullable String body,
      Map<String, String> headers) {
    ClientResponse response = null;
    int statusCode = 0;

    try {
      WebResource webResource = client.resource(uri);
      WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(
          MediaType.APPLICATION_JSON_TYPE);
      if (headers != null)
        for (Entry<String, String> header : headers.entrySet())
          builder.header(header.getKey(), header.getValue());
      response = body == null ? builder.post(ClientResponse.class, "") : builder.post(
          ClientResponse.class, body);
      statusCode = response.getStatus();
    } catch (Exception e) {
      consumeResponse(response);
      throw new RestClientException(e, "Error creating %s", uri);
    }

    if (statusCode == 401)
      throw new UnauthorizedException("Unauthorized %s. %s", uri, response.getEntity(String.class));
    if (response == null || statusCode >= 400)
      throw new RestClientException(statusCode, "Error creating %s. Status code %s. %s", uri,
          statusCode, response.getEntity(String.class));

    try {
      T resource = response.getEntity(resourceType);
      return resource;
    } catch (Exception e) {
      throw new RestClientException(e, "Failed to parse resource response for", uri);
    }
  }

  /**
   * Gets a resource.
   * 
   * @throws RestClientException if an error occurs during the request
   * @throws UnauthorizedException if the request results in a 401
   * @throws ResourceNotFoundException if the request results in a 404
   */
  protected <T> T getResource(String uri, Class<T> resourceType, String resourceId,
      Map<String, String> headers) {
    ClientResponse response = null;
    int statusCode = 0;

    try {
      WebResource webResource = client.resource(uri);
      WebResource.Builder builder = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(
          MediaType.APPLICATION_JSON_TYPE);
      if (headers != null)
        for (Entry<String, String> header : headers.entrySet())
          builder.header(header.getKey(), header.getValue());
      response = builder.get(ClientResponse.class);
      statusCode = response.getStatus();
    } catch (Exception e) {
      consumeResponse(response);
      throw new RestClientException(e, "Error getting %s", uri);
    }

    if (statusCode == 401)
      throw new UnauthorizedException("Unauthorized %s. %s", uri, response.getEntity(String.class));
    if (statusCode == 404)
      throw new ResourceNotFoundException(serviceName, resourceId, "Not Found. %s", uri,
          response.getEntity(String.class));
    if (response == null || statusCode != 200)
      throw new RestClientException(statusCode, "Error getting %s. Status code %s. %s", uri,
          statusCode, response.getEntity(String.class));

    try {
      T resource = response.getEntity(resourceType);
      return resource;
    } catch (Exception e) {
      throw new RestClientException(e, "Failed to parse resource response for", uri);
    }
  }

  /**
   * Returns whether a resource exists.
   * 
   * @throws RestClientException if an error occurs during the request
   * @throws UnauthorizedException if the request results in a 401
   */
  protected boolean resourceExists(String uri, Map<String, String> headers) {
    ClientResponse response = null;
    int statusCode = 0;

    try {
      WebResource webResource = client.resource(uri);
      WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_JSON_TYPE);
      if (headers != null)
        for (Entry<String, String> header : headers.entrySet())
          builder.header(header.getKey(), header.getValue());
      response = builder.get(ClientResponse.class);
      statusCode = response.getStatus();
    } catch (Exception e) {
      consumeResponse(response);
      throw new RestClientException(e, "Error getting %s", uri);
    }

    if (statusCode == 401)
      throw new UnauthorizedException("Unauthorized %s. %s", uri, response.getEntity(String.class));
    if (response == null || (statusCode >= 400 && statusCode != 404))
      throw new RestClientException(statusCode, "Error getting %s. Status code %s. %s", uri,
          statusCode, response.getEntity(String.class));

    return statusCode == 200 || statusCode == 204;
  }

  private void consumeResponse(ClientResponse clientResponse) {
    if (clientResponse != null && clientResponse.hasEntity())
      clientResponse.close();
  }
}
