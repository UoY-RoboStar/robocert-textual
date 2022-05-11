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
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.intf.seq.context.ActorContext;
import robocalc.robocert.generator.intf.seq.context.InteractionContext;
import robocalc.robocert.generator.intf.seq.context.Synchronisation;
import robocalc.robocert.generator.tockcsp.seq.fragment.DeadlineFragmentHeaderGenerator;
import robostar.robocert.DeadlineFragment;
import robostar.robocert.ParFragment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.UntilFragment;
import robostar.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the duration fragment header CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class DeadlineFragmentHeaderGeneratorTest {
  /** The system under test. */
  @Inject private DeadlineFragmentHeaderGenerator gen;

  @Inject private RoboCertFactory factory;

  @Inject private ExpressionFactory exprs;

  private DeadlineFragment fragment;
  private ActorContext ctx;

  @BeforeEach
  void setUp() {
    final var act = factory.createComponentActor();
    act.setName("C");

    final var seq = factory.createInteraction();
    final var untils = new Synchronisation<UntilFragment>(List.of(), "until", "until");
    final var pars = new Synchronisation<ParFragment>(List.of(), "par", "par");
    final var ictx = new InteractionContext(seq, List.of(act), untils, pars);
    ctx = new ActorContext(ictx, act, "x");

    final var inner = factory.createInteractionOperand();
    fragment = factory.createDeadlineFragment();
    fragment.setBody(inner);
    fragment.setUnits(exprs.integer(2));
    fragment.setActor(ctx.actor());
  }

  /** Tests that a deadline with a basic bound is generated properly. */
  @Test
  void simpleBound() {
    assertThat(fragment, generatesCSPDeadlineHeader("DeadlineF(2)"));
  }

  /** Tests that a non-singleton duration for the wrong actor is generated properly. */
  @Test
  void wrongActorNonSingleton() {
    final var a = factory.createWorld();
    a.setName("W");
    fragment.setActor(a);
    assertThat(fragment, generatesCSPDeadlineHeader("{- deadline on C -} "));
  }

  private Matcher<DeadlineFragment> generatesCSPDeadlineHeader(String expected) {
    return generatesCSP(expected, c -> gen.generate(c, ctx));
  }
}
