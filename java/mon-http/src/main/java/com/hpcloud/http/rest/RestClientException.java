package com.hpcloud.http.rest;

/**
 * REST Client Exception.
 * 
 * @author Jonathan Halterman
 */
public class RestClientException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public final int statusCode;

  public RestClientException(int statusCode, Throwable ex) {
    super(ex);
    this.statusCode = statusCode;
  }

  public RestClientException(int statusCode, Throwable ex, String msg) {
    super(msg, ex);
    this.statusCode = statusCode;
  }

  public RestClientException(int statusCode, Throwable ex, String msgFormat, Object... args) {
    super(String.format(msgFormat, args), ex);
    this.statusCode = statusCode;
  }

  public RestClientException(String msg) {
    super(msg);
    this.statusCode = 0;
  }

  public RestClientException(int statusCode, String msgFormat, Object... args) {
    super(String.format(msgFormat, args));
    this.statusCode = statusCode;
  }

  public RestClientException(Throwable ex) {
    super(ex);
    this.statusCode = 0;
  }

  public RestClientException(Throwable ex, String msgFormat, Object... args) {
    super(String.format(msgFormat, args), ex);
    this.statusCode = 0;
  }
}
