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

package robocalc.robocert.tests.generator.tockcsp.core.tgt;

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
import robocalc.robocert.generator.tockcsp.core.tgt.OverrideGenerator;
import robocalc.robocert.generator.utils.param.TargetParameterResolver;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.model.robocert.util.TargetFactory;
import robocalc.robocert.tests.util.DummyVariableFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests aspects of {@link OverrideGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class OverrideGeneratorTest {

  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private ExpressionFactory exprFactory;
  @Inject
  private DummyVariableFactory varFactory;
  @Inject
  private TargetFactory targetFactory;

  // TODO(@MattWindsor91): remove need to use this
  @Inject
  private TargetParameterResolver paramRes;

  /**
   * The system under test.
   */
  @Inject
  private OverrideGenerator gen;

  /**
   * Tests {@code generate} on a representative module target with one open constant, one
   * RoboChart-instantiated constant, and one RoboCert-instantiated constant.
   */
  @Test
  void testGenerate_sampleModule() {
    final var vars = varFactory.constantList("foo", "bar", "baz");
    // Instantiate "foo" at the RoboChart level
    vars.getVars().get(0).setInitial(exprFactory.integer(42));

    final var rp = chartFactory.createRoboticPlatformDef();
    rp.setName("rp");
    rp.getVariableList().add(vars);

    final var module = chartFactory.createRCModule();
    module.getNodes().add(rp);
    module.setName("mod");

    final var target = targetFactory.module(module);

    assertThat(target, generates(null, """
        -- begin overrides
        const_mod_rp_foo = 42 -- initialised in RoboChart
        -- end overrides
        """));

    // Instantiate "baz" at the RoboCert level:
    final var cinst = certFactory.createConstAssignment();
    cinst.getConstants().add(vars.getVars().get(2));
    cinst.setValue(exprFactory.integer(64));
    assertThat(target, generates(List.of(cinst), """
        -- begin overrides
        const_mod_rp_foo = 42 -- initialised in RoboChart
        const_mod_rp_baz = 64 -- initialised in RoboCert
        -- end overrides
        """));
  }

  /**
   * Shortcut for building the Hamcrest matcher for target open defs.
   *
   * @param expected the expected output.
   * @return the matcher.
   */
  private Matcher<Target> generates(List<ConstAssignment> inst, String expected) {
    return generatesCSP(expected, t -> gen.generate(inst, paramRes.parameterisation(t).toList()));
  }
}
