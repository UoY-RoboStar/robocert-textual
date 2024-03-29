/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.core.ExpressionGenerator;
import robostar.robocert.util.ExpressionFactory;
import robostar.robocert.textual.tests.util.DummyVariableFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link ExpressionGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class ExpressionGeneratorTest {

  @Inject
  private ExpressionFactory ef;
  @Inject
  private ExpressionGenerator eg;
  @Inject
  private DummyVariableFactory vf;

  /**
   * Tests that generating the Boolean literals works properly.
   */
  @Test
  void testGenerateBoolExprs() {
    assertThat(ef.bool(true), generates("true"));
    assertThat(ef.bool(false), generates("false"));
  }

  /**
   * Tests that generating integer literals works properly.
   */
  @Test
  void testGenerateIntExprs() {
    assertThat(ef.integer(0), generates("0"));
    assertThat(ef.integer(42), generates("42"));
    // This last case is unlikely to happen in practice, because the parser
    // would consider -1 to be -(1).
    assertThat(ef.integer(-1), generates("-1"));
  }

  /**
   * Tests that generating minus expressions works properly.
   */
  @Test
  void testGenerateMinusExprs() {
    assertThat(ef.neg(ef.integer(1)), generates("-(1)"));
    assertThat(ef.neg(ef.neg(ef.integer(42))), generates("-(-(42))"));
    assertThat(ef.neg(ef.ref(vf.constant("x"))), generates("-(const_x)"));
  }

  /**
   * Tests that generating some logical expressions works properly.
   */
  @Test
  void testGenerateLogicalExprs() {
    assertThat(ef.and(ef.bool(true), ef.bool(false)), generates("(true) and (false)"));
    assertThat(ef.or(ef.ref(vf.mem("x")), ef.ref(vf.constant("y"))),
        generates("(Bnd__x) or (const_y)"));
  }

  /**
   * Tests that generating some arithmetic expressions works properly.
   */
  @Test
  void testGenerateArithExprs() {
    // TODO(@MattWindsor91): work out how to get the correct types here
    assertThat(ef.plus(ef.integer(54), ef.integer(321)), generates("Plus(54, 321, Object)"));
    assertThat(ef.div(ef.integer(12), ef.integer(345)), generates("Div(12, 345, Object)"));
  }

  /**
   * Tests that generating relational expressions works properly.
   */
  @Test
  void testGenerateLessOrEqual() {
    assertThat(ef.le(ef.integer(42), ef.integer(56)), generates("(42) <= (56)"));
  }

  /**
   * Shortcut for building the Hamcrest matcher for expressions.
   *
   * @param expected the expected output.
   * @return the matcher.
   */
  private Matcher<Expression> generates(String expected) {
    return generatesCSP(expected, eg::generate);
  }
}
