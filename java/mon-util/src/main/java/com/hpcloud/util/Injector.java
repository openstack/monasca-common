package com.hpcloud.util;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.name.Names;

/**
 * Provides fully injected instances.
 * 
 * @author Jonathan Halterman
 */
public final class Injector {
  private static volatile com.google.inject.Injector injector;

  /**
   * Supplementary factory methods for producing type literal based Guice Matchers.
   */
  public static class TypeLiteralMatchers {
    private static class SubtypeOf extends AbstractMatcher<TypeLiteral<?>> implements Serializable {
      private static final long serialVersionUID = 1239939466206498961L;
      private final TypeLiteral<?> supertype;

      /**
       * @param superType
       */
      public SubtypeOf(TypeLiteral<?> superType) {
        super();
        this.supertype = Preconditions.checkNotNull(superType, "supertype");
      }

      @Override
      public boolean equals(Object other) {
        return other instanceof SubtypeOf && ((SubtypeOf) other).supertype.equals(supertype);
      }

      @Override
      public int hashCode() {
        return 37 * supertype.hashCode();
      }

      @Override
      public boolean matches(TypeLiteral<?> subtype) {
        return (subtype.equals(supertype) || supertype.getRawType().isAssignableFrom(
            subtype.getRawType()));
      }

      @Override
      public String toString() {
        return "subtypeOf(" + supertype.getRawType() + ".class)";
      }
    }

    public static Matcher<? super TypeLiteral<?>> subtypeOf(final Class<?> superclass) {
      return new SubtypeOf(TypeLiteral.get(superclass));
    }

    public static Matcher<? super TypeLiteral<?>> subtypeOf(final TypeLiteral<?> supertype) {
      return new SubtypeOf(supertype);
    }
  }

  private Injector() {
  }

  /**
   * Checks to see if the {@code type} is injectable.
   * 
   * @throws ConfigurationException if {@code type} is not injectable
   */
  public static void checkInjectable(Class<?> type) {
    initInjector();
    injector.getBinding(type);
  }

  /**
   * Returns an instance of the {@code type} according to the registered modules.
   * 
   * @throws ConfigurationException if this injector cannot find or create the provider.
   * @throws ProvisionException if there was a runtime failure while providing an instance.
   */
  public static <T> T getInstance(Class<T> type) {
    initInjector();
    return injector.getInstance(type);
  }

  /**
   * Returns an instance of the {@code type} for the {@code name} according to the registered
   * modules.
   * 
   * @throws ConfigurationException if this injector cannot find or create the provider.
   * @throws ProvisionException if there was a runtime failure while providing an instance.
   */
  public static <T> T getInstance(Class<T> type, String name) {
    initInjector();
    return injector.getInstance(Key.get(type, Names.named(name)));
  }

  /**
   * Returns an instance of <T> for the {@code key} according to the registered modules.
   * 
   * @throws ConfigurationException if this injector cannot find or create the provider.
   * @throws ProvisionException if there was a runtime failure while providing an instance.
   */
  public static <T> T getInstance(Key<T> key) {
    initInjector();
    return injector.getInstance(key);
  }

  /**
   * Injects dependencies into the fields and methods of the {@code object}.
   */
  public static void injectMembers(Object object) {
    initInjector();
    injector.injectMembers(object);
  }

  /**
   * Returns true of a binding exists for the {@code type}, else false.
   */
  public static boolean isBound(Class<?> type) {
    return injector != null && injector.getExistingBinding(Key.get(type)) != null;
  }

  /**
   * Returns true of a binding exists for the {@code type} and {@code name}, else false.
   */
  public static boolean isBound(Class<?> type, String name) {
    return injector != null
        && injector.getExistingBinding(Key.get(type, Names.named(name))) != null;
  }

  /** Registers the {@code modules} if the {@code type} is not bound, and does so atomically. */
  public static synchronized void registerIfNotBound(Class<?> type, Module... modules) {
    if (!isBound(type))
      registerModules(modules);
  }

  public static synchronized void registerModules(Module... modules) {
    if (injector == null)
      injector = Guice.createInjector(modules);
    else
      injector = injector.createChildInjector(modules);
  }

  /**
   * Resets the injector's internal module-based configuration.
   */
  public static void reset() {
    injector = null;
  }

  /** Initializes the injector with an empty module. */
  private static void initInjector() {
    if (injector == null)
      registerModules(new AbstractModule() {
        @Override
        protected void configure() {
        }
      });
  }
}
