/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.util;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.IntegerExp;
import org.eclipse.xtext.EcoreUtil2;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import robostar.robocert.MessageSet;
import robostar.robocert.textual.generator.utils.MessageSetOptimiser;

/**
 * Checks that a message set optimises to a particular other message set under structural equality.
 *
 * @author Matt Windsor
 */
public class MessageSetOptimisesTo extends TypeSafeDiagnosingMatcher<MessageSet> {

  private final MessageSet expected;
  private final MessageSetOptimiser optimiser;

  /**
   * Constructs a matcher for optimised forms of message sets.
   *
   * @param optimiser the optimiser to use.
   * @param expected the expected value.
   */
  public MessageSetOptimisesTo(MessageSetOptimiser optimiser, MessageSet expected) {
    this.expected = expected;
    this.optimiser = optimiser;
  }

  /**
   * Constructs a matcher for optimised forms of message sets.
   *
   * @param optimiser the optimiser to use.
   * @param expected the expected value.
   * @return a matcher that checks that the input value optimises to the expected value.
   */
  public static MessageSetOptimisesTo optimisesTo(MessageSetOptimiser optimiser, MessageSet expected) {
    return new MessageSetOptimisesTo(optimiser, expected);
  }

  @Override
  public void describeTo(Description description) {
    describeValue(expected, description);
  }

  @Override
  protected boolean matchesSafely(MessageSet ms, Description description) {
    final var actual = optimiser.optimise(ms);
    if (EcoreUtil2.equals(expected, actual)) {
      return true;
    }

    description.appendText("optimised to ");
    describeValue(actual, description);
    return false;
  }

  private void describeValue(MessageSet value, Description description) {
    description.appendText("message set optimising to ").appendValue(value);
  }
}
