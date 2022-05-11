/********************************************************************************
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
 ********************************************************************************/
package robostar.robocert.textual.tests.generator.tockcsp.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.core.CorePropertyGenerator;
import robostar.robocert.CorePropertyType;
import robostar.robocert.ModuleTarget;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link CorePropertyGenerator} on a few properties concerning a {@link ModuleTarget}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class CorePropertyGeneratorTest {
  @Inject private RoboCertFactory rc;
  @Inject private CorePropertyGenerator gen;

  /** Tests the generation of determinism assertions. */
  @Test
  void testDeterminism() {
    assertGeneratesBody("Test::Closed::Target :[deterministic]", CorePropertyType.DETERMINISM);
  }

  /** Tests the generation of timed deadlock freedom assertions. */
  @Test
  void testDeadlockFree() {
    assertGeneratesBody(
        "prioritise( Test::Closed::Target[[ tock <- tock, tock <- tock' ]], <diff(Events, {tock', tock}), {tock}> ) \\ {tock} :[divergence free [FD]]",
        CorePropertyType.DEADLOCK_FREE);
  }

  @Test
  void testTimelockFree() {
    assertGeneratesBody(
        "RUN({tock}) ||| CHAOS(diff(Events, {|tock|})) [F= Test::Closed::Target",
        CorePropertyType.TIMELOCK_FREE);
  }

  /**
   * Asserts that generating positive and negative core assertions of the given type produces,
   * respectively, positive and negative FDR assertions with the given expected body.
   *
   * @param expected expected body, less 'assert', 'not', and tau prioritisation.
   * @param type type for which we are testing.
   */
  private void assertGeneratesBody(String expected, CorePropertyType type) {
    assertGenerates("assert %s :[tau priority]: {tock}".formatted(expected), type, false);
    assertGenerates("assert not %s :[tau priority]: {tock}".formatted(expected), type, true);
  }

  private void assertGenerates(String expected, CorePropertyType type, boolean isNegated) {
    final var p = rc.createCoreProperty();
    p.setNegated(isNegated);
    p.setType(type);

    final var g = rc.createSpecificationGroup();
    g.setName("Test");
    p.setGroup(g);

    assertThat(p, generatesCSP(expected, gen::generate));
  }
}
