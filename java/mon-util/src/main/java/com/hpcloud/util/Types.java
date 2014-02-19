package com.hpcloud.util;

import java.lang.reflect.Method;

/**
 * Utilities for working with types.
 * 
 * @author Jonathan Halterman
 */
public final class Types {
  private static Class<?> JAVASSIST_PROXY_FACTORY_CLASS;
  private static Method JAVASSIST_IS_PROXY_CLASS_METHOD;

  static {
    try {
      JAVASSIST_PROXY_FACTORY_CLASS = Types.class.getClassLoader().loadClass(
          "javassist.util.proxy.ProxyFactory");
      JAVASSIST_IS_PROXY_CLASS_METHOD = JAVASSIST_PROXY_FACTORY_CLASS.getMethod("isProxyClass",
          new Class<?>[] { Class.class });
    } catch (Exception ignore) {
    }
  }

  private Types() {
  }

  /**
   * Returns the proxied type, if any, else returns the given {@code type}.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> deProxy(Class<?> type) {
    // Ignore JDK proxies
    if (type.isInterface())
      return (Class<T>) type;

    // Enhanced by CGLib
    if (type.getName().contains("$$Enhancer"))
      return (Class<T>) type.getSuperclass();

    // Javassist
    try {
      if (JAVASSIST_IS_PROXY_CLASS_METHOD != null
          && (Boolean) JAVASSIST_IS_PROXY_CLASS_METHOD.invoke(null, type))
        return (Class<T>) type.getSuperclass();
    } catch (Exception ignore) {
    }

    return (Class<T>) type;
  }
}
