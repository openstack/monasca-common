package com.hpcloud.util;

import com.google.common.base.Preconditions;

/**
 * Utilities for working with exceptions.
 * 
 * @author Jonathan Halterman
 */
public final class Exceptions {
  /**
   * A marker exception class that we look for in order to unwrap the exception into the user
   * exception, to provide a cleaner stack trace.
   */
  @SuppressWarnings("serial")
  private static class UnhandledCheckedUserException extends RuntimeException {
    public UnhandledCheckedUserException(Exception ex, String msg, Object... args) {
      super(String.format(msg, args), ex);
    }

    public UnhandledCheckedUserException(Throwable cause) {
      super(cause);
    }
  }

  private Exceptions() {
  }

  /** Throw <b>any</b> exception as a RuntimeException. */
  public static RuntimeException sneakyThrow(Throwable throwable) {
    Preconditions.checkNotNull(throwable, "throwable");
    Exceptions.<RuntimeException>sneakyThrow0(throwable);
    return null;
  }

  /**
   * Throws a new unchecked exception wrapping the {@code cause}.
   * 
   * @throws RuntimeException wrapping the {@code cause}
   */
  public static void throwUnchecked(Throwable cause) {
    throw new UnhandledCheckedUserException(cause);
  }

  /** Returns a new unchecked exception wrapping the {@code cause}. */
  public static RuntimeException uncheck(Exception ex, String msg, Object... args) {
    return new UnhandledCheckedUserException(ex, msg, args);
  }

  /** Returns a new unchecked exception wrapping the {@code cause}. */
  public static RuntimeException uncheck(Throwable cause) {
    return new UnhandledCheckedUserException(cause);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
    throw (T) t;
  }
}
