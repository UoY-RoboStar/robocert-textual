/*******************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robocalc.robocert.tests.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.IntExpr;

/**
 * Checks that an expression is an {@link IntExpr} with a particular value.
 *
 * @author Matt Windsor
 */
public class IsIntExprWithValue extends TypeSafeDiagnosingMatcher<CertExpr> {

  private final int expected;

  /**
   * Constructs a matcher for integer expressions with the expected value.
   *
   * @param expected the expected value.
   */
  public IsIntExprWithValue(int expected) {
    this.expected = expected;
  }

  /**
   * Constructs a matcher for integer expressions with the expected value.
   *
   * @param expected the expected value.
   * @return a matcher that requires integer expressions and
   */
  public static IsIntExprWithValue intExprWithValue(int expected) {
    return new IsIntExprWithValue(expected);
  }

  @Override
  public void describeTo(Description description) {
    describeValue(expected, description);
  }

  @Override
  protected boolean matchesSafely(CertExpr expr, Description description) {
    if (expr instanceof IntExpr ix) {
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
