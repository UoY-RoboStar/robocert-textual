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
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.core.tgt.TargetGenerator;
import robostar.robocert.util.ExpressionFactory;
import robostar.robocert.util.TargetFactory;
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

  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private ExpressionFactory exprFactory;
  @Inject
  private DummyVariableFactory varFactory;
  @Inject
  private TargetFactory targetFactory;

  /**
   * The system under test.
   */
  @Inject
  private TargetGenerator gen;

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

    final var target = targetFactory.module(module);

    assertThat(target, generatesCSP("""
        mod::O__(id__, const_mod_rp_foo, const_mod_rp_bar, const_mod_rp_baz)
        """, gen::openDef));
  }
}
