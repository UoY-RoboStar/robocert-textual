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
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.tests.generator.tockcsp.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.DummyVariableFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests aspects of {@link TargetGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class TargetGeneratorTest {
  @Inject private RoboCertFactory certFactory;
  @Inject private RoboChartFactory chartFactory;
  @Inject private ExpressionFactory exprFactory;
  @Inject private DummyVariableFactory varFactory;

  /** The system under test. */
  @Inject private TargetGenerator gen;

  /**
   * Tests {@code openDef} on a representative module target with one open constant, one
   * RoboChart-instantiated constant, and one RoboCert-instantiated constant.
   */
  @Test
  void testOpenDef_sampleModule() {
    final var vars = varFactory.constantList("foo", "bar", "baz");
    // Instantiate "foo" at the RoboChart level
    vars.getVars().get(0).setInitial(exprFactory.integer(42));

    final var rp = chartFactory.createRoboticPlatformDef();
    rp.setName("rp");
    rp.getVariableList().add(vars);

    final var module = chartFactory.createRCModule();
    module.getNodes().add(rp);
    module.setName("mod");

    final var target = certFactory.createModuleTarget();
    target.setModule(module);

    assertThat(
        target,
        generatesOpenDef(
            null,
            "mod::D__({- id -} 0, {- const_mod_rp_foo -} 42, const_mod_rp_bar, const_mod_rp_baz)"));

    // Instantiate "baz" at the RoboCert level:
    final var cinst = certFactory.createConstAssignment();
    cinst.getConstants().add(vars.getVars().get(2));
    cinst.setValue(exprFactory.integer(64));
    assertThat(
        target,
        generatesOpenDef(
            List.of(cinst),
            "mod::D__({- id -} 0, {- const_mod_rp_foo -} 42, const_mod_rp_bar, {- const_mod_rp_baz -} 64)"));
  }

  /**
   * Shortcut for building the Hamcrest matcher for target open defs.
   *
   * @param expected the expected output.
   * @return the matcher.
   */
  private Matcher<Target> generatesOpenDef(List<ConstAssignment> inst, String expected) {
    return generatesCSP(expected, t -> gen.openDef(t, inst));
  }
}
