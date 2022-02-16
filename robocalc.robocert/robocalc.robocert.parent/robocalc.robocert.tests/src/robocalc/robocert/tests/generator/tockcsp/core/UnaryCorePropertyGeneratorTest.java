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
package robocalc.robocert.tests.generator.tockcsp.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.core.CorePropertyGenerator;
import robocalc.robocert.model.robocert.CorePropertyType;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.ProcessCSPFragment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link CorePropertyGenerator} on a few properties concerning a {@link
 * ProcessCSPFragment}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class CorePropertyGeneratorTest {
  @Inject private RoboCertFactory rc;
  @Inject private RoboChartFactory rchart;
  @Inject private CorePropertyGenerator gen;

  private ModuleTarget source;

  @BeforeEach
  void setUp() {
    final var mod = rchart.createRCModule();
    mod.setName("foo");

    source = rc.createModuleTarget();
    source.setName("bar");
    source.setModule(mod);

		final var pkg = rc.createCertPackage();
		pkg.setName("baz");
		source.setGroup(pkg);
	}

  /** Tests the generation of determinism assertions. */
  @Test
  void testDeterminism() {
    assertGeneratesBody("Targets::bar::Closed :[deterministic]", CorePropertyType.DETERMINISM);
  }

  /** Tests the generation of timed deadlock freedom assertions. */
  @Test
  void testDeadlockFree() {
    assertGeneratesBody(
        "prioritise( Targets::bar::Closed[[tock<-tock,tock<-tock']], <diff(Events,{tock',tock}),{tock}> )\\{tock} :[divergence free [FD]]",
        CorePropertyType.DEADLOCK_FREE);
  }

  @Test
  void testTimelockFree() {
    assertGeneratesBody(
        "RUN({tock}) ||| CHAOS(diff(Events, {|tock|})) [F= Targets::bar::Closed", CorePropertyType.TIMELOCK_FREE);
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
    p.setSubject(source);
    p.setType(type);
    assertThat(p, generatesCSP(expected, gen::generate));
  }
}
