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
package monasca.common.util.validation;

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
