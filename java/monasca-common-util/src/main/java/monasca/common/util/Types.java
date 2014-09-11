/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monasca.common.util;

import java.lang.reflect.Method;

/**
 * Utilities for working with types.
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
