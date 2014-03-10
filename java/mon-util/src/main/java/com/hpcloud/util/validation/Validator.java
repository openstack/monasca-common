package com.hpcloud.util.validation;

import static java.lang.String.format;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * A simple fa√ßade for Hibernate Validator.
 */
public class Validator {
  private final ValidatorFactory factory;

  public Validator() {
    this.factory = Validation.buildDefaultValidatorFactory();
  }

  /**
   * Validates the given object, and returns a list of error messages, if any. If the returned list
   * is empty, the object is valid.
   * 
   * @param o a potentially-valid object
   * @param <T> the type of object to validate
   * @return a list of error messages, if any, regarding {@code o}'s validity
   */
  public <T> ImmutableList<String> validate(T object) {
    final Set<String> errors = Sets.newHashSet();
    final Set<ConstraintViolation<T>> violations = factory.getValidator().validate(object);
    for (ConstraintViolation<T> v : violations)
      errors.add(format("%s %s (was %s)", v.getPropertyPath(), v.getMessage(), v.getInvalidValue()));
    return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
  }
}
