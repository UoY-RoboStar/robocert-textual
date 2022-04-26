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
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.intf.seq.InteractionContext;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.seq.fragment.DurationFragmentHeaderGenerator;
import robocalc.robocert.model.robocert.DurationFragment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the duration fragment header CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class DurationFragmentHeaderGeneratorTest {
  /** The system under test. */
  @Inject private DurationFragmentHeaderGenerator gen;

  @Inject private RoboCertFactory factory;

  @Inject private ExpressionFactory exprs;

  private DurationFragment fragment;
  private LifelineContext ctx;

  @BeforeEach
  void setUp() {
    final var act = factory.createComponentActor();
    act.setName("C");
    final var ictx = new InteractionContext(List.of(act), List.of());
    ctx = new LifelineContext(ictx, act, "x");

    final var inner = factory.createInteractionOperand();
    fragment = factory.createDurationFragment();
    fragment.setBody(inner);
    fragment.setBound(factory.createDiscreteBound());
    fragment.setActor(ctx.actor());
  }

  /** Makes sure no-bound-object durations (which are ill-formed) can't generate CSP. */
  @Test
  void noBoundObject() {
    fragment.setBound(null);
    assertThrows(NullPointerException.class, () -> gen.generate(fragment, ctx));
  }

  /** Makes sure no-bound durations (which are ill-formed) can't generate CSP. */
  @Test
  void noBounds() {
    assertThrows(NullPointerException.class, () -> gen.generate(fragment, ctx));
  }

  /** Tests that a duration with a lower bound only is generated properly. */
  @Test
  void lowerBound() {
    fragment.getBound().setLower(exprs.integer(3));
    assertThat(fragment, generatesCSPDurationHeader("DurationLB(3)"));
  }

  /**
   * Tests that a duration with a zero lower-bound and given upper-bound generates an upper-bound
   * process.
   */
  @Test
  void upperBound() {
    fragment.getBound().setLower(exprs.integer(0));
    fragment.getBound().setUpper(exprs.integer(5));
    assertThat(fragment, generatesCSPDurationHeader("DurationUB(5)"));
  }

  /**
   * Tests that a duration with a null lower-bound and given upper-bound generates an exact-bound
   * process.
   */
  @Test
  void exactBound() {
    fragment.getBound().setUpper(exprs.integer(5));
    assertThat(fragment, generatesCSPDurationHeader("Duration(5)"));
  }

  /** Tests that a duration with both bounds is generated properly. */
  @Test
  void bothBounds() {
    fragment.getBound().setLower(exprs.integer(4));
    fragment.getBound().setUpper(exprs.integer(6));
    assertThat(fragment, generatesCSPDurationHeader("DurationRange(4, 6)"));
  }

  /** Tests that a non-singleton duration for the wrong actor is generated properly. */
  @Test
  void wrongActorNonSingleton() {
    final var a = factory.createWorld();
    a.setName("W");
    fragment.setActor(a);
    fragment.getBound().setLower(exprs.integer(4));
    fragment.getBound().setUpper(exprs.integer(6));
    assertThat(fragment, generatesCSPDurationHeader("{- duration on C -} "));
  }

  private Matcher<DurationFragment> generatesCSPDurationHeader(String expected) {
    return generatesCSP(expected, c -> gen.generate(c, ctx));
  }
}
