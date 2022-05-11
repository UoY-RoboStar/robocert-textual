/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/

package robocalc.robocert.tests.generator.tockcsp.seq.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.fragment.LoopFragmentHeaderGenerator;
import robostar.robocert.LoopFragment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the duration fragment header CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class LoopFragmentHeaderGeneratorTest {
  /** The system under test. */
  @Inject private LoopFragmentHeaderGenerator gen;

  @Inject private RoboCertFactory factory;

  @Inject private ExpressionFactory exprs;

  private LoopFragment fragment;

  @BeforeEach
  void setUp() {
    final var inner = factory.createInteractionOperand();
    fragment = factory.createLoopFragment();
    fragment.setBody(inner);
    fragment.setBound(factory.createDiscreteBound());
  }

  /** Tests that a loop with no bound object becomes the standard csp loop process. */
  @Test
  void noBoundObject() {
    fragment.setBound(null);
    assertThat(fragment, generatesCSPLoopHeader("loop"));
  }

  /** Makes sure no-bound loops (which are ill-formed) can't generate CSP. */
  @Test
  void noBounds() {
    assertThrows(NullPointerException.class, () -> gen.generate(fragment));
  }

  /** Tests that a loop with a lower bound only is generated properly. */
  @Test
  void lowerBound() {
    fragment.getBound().setLower(exprs.integer(3));
    assertThat(fragment, generatesCSPLoopHeader("BoundedLoopLB(3)"));
  }

  /**
   * Tests that a loop with a zero lower-bound and given upper-bound generates an upper-bound
   * process.
   */
  @Test
  void upperBound() {
    fragment.getBound().setLower(exprs.integer(0));
    fragment.getBound().setUpper(exprs.integer(5));
    assertThat(fragment, generatesCSPLoopHeader("BoundedLoopUB(5)"));
  }

  /**
   * Tests that a loop with a null lower-bound and given upper-bound generates an exact-bound
   * process.
   */
  @Test
  void exactBound() {
    fragment.getBound().setUpper(exprs.integer(5));
    assertThat(fragment, generatesCSPLoopHeader("BoundedLoop(5)"));
  }

  /** Tests that a loop with both bounds is generated properly. */
  @Test
  void bothBounds() {
    fragment.getBound().setLower(exprs.integer(4));
    fragment.getBound().setUpper(exprs.integer(6));
    assertThat(fragment, generatesCSPLoopHeader("BoundedLoopRange(4, 6)"));
  }

  private Matcher<LoopFragment> generatesCSPLoopHeader(String expected) {
    return generatesCSP(expected, gen::generate);
  }
}
