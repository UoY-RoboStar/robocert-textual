/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robostar.robocert.textual.tests.matchers;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.IntegerExp;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Checks that an expression is an {@link IntegerExp} with a particular value.
 *
 * @author Matt Windsor
 */
public class IsIntegerExpWithValue extends TypeSafeDiagnosingMatcher<Expression> {

  private final int expected;

  /**
   * Constructs a matcher for integer expressions with the expected value.
   *
   * @param expected the expected value.
   */
  public IsIntegerExpWithValue(int expected) {
    this.expected = expected;
  }

  /**
   * Constructs a matcher for integer expressions with the expected value.
   *
   * @param expected the expected value.
   * @return a matcher that requires integer expressions and
   */
  public static IsIntegerExpWithValue intExprWithValue(int expected) {
    return new IsIntegerExpWithValue(expected);
  }

  @Override
  public void describeTo(Description description) {
    describeValue(expected, description);
  }

  @Override
  protected boolean matchesSafely(Expression expr, Description description) {
    if (expr instanceof IntegerExp ix) {
      final var actual = ix.getValue();
      if (expected == actual) {
        return true;
      }

      description.appendText("was ");
      describeValue(actual, description);
      return false;
    }

    description.appendText("was non-integer expression ").appendValue(expr);
    return false;
  }

  private void describeValue(int value, Description description) {
    description.appendText("integer expression of value ").appendValue(value);
  }
}
