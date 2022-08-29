/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.generator.tockcsp.core.tgt;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.core.tgt.OverrideGenerator;
import robostar.robocert.textual.generator.utils.param.TargetParameterResolver;
import robostar.robocert.ConstAssignment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.Target;
import robostar.robocert.util.ExpressionFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.util.DummyVariableFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

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
        id__ = 0
        const_mod_rp_foo = 42 -- initialised in RoboChart
        -- end overrides
        """));

    // Instantiate "baz" at the RoboCert level:
    final var cinst = certFactory.createConstAssignment();
    cinst.getConstants().add(vars.getVars().get(2));
    cinst.setValue(exprFactory.integer(64));
    assertThat(target, generates(List.of(cinst), """
        -- begin overrides
        id__ = 0
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
