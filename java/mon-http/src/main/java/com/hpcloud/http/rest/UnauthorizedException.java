package com.hpcloud.http.rest;

/**
 * Unauthorized Exception.
 * 
 * @author Jonathan Halterman
 */
public class UnauthorizedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UnauthorizedException(Throwable ex) {
    super(ex);
  }

  public UnauthorizedException(Throwable ex, String msg) {
    super(msg, ex);
  }

  public UnauthorizedException(Throwable ex, String msgFormat, Object... args) {
    super(String.format(msgFormat, args), ex);
  }

  public UnauthorizedException(String msg) {
    super(msg);
  }

  public UnauthorizedException(String msgFormat, Object... args) {
    super(String.format(msgFormat, args));
  }
}
