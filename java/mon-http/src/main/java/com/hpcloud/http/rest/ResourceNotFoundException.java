package com.hpcloud.http.rest;

/**
 * Resource Not Found Exception.
 * 
 * @author Jonathan Halterman
 */
public class ResourceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public final String service;
  public final String resourceId;

  public ResourceNotFoundException(String service, String resourceId, Throwable ex) {
    super(ex);
    this.service = service;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(String service, String resourceId, Throwable ex, String msg) {
    super(msg, ex);
    this.service = service;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(String service, String resourceId, Throwable ex,
      String msgFormat, Object... args) {
    super(String.format(msgFormat, args), ex);
    this.service = service;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(String service, String resourceId, String msg) {
    super(msg);
    this.service = service;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(String service, String resourceId, String msgFormat,
      Object... args) {
    super(String.format(msgFormat, args));
    this.service = service;
    this.resourceId = resourceId;
  }
}
