/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
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
package robocalc.robocert.tests.generator.tockcsp.seq.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.message.NamedSetModuleGenerator;
import robocalc.robocert.model.robocert.util.TargetFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the named message set CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class NamedSetModuleGeneratorTest {
  @Inject private RoboChartFactory chartFactory;
  @Inject private TargetFactory targetFactory;

  // System under test.
  @Inject private NamedSetModuleGenerator msg;

  /**
   * Tests generation of a module for a specification group targeting a controller nested inside a
   * module, but with no user-defined sets.
   *
   * <p>This is a regression test for GitHub issue #109.
   */
  @Test
  void testGenerate_NestedTarget() {
    // TODO(@MattWindsor91): if we ever change the universe definition, this will break.

    final var ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");

    // nesting
    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");
    mod.getNodes().add(ctrl);

    final var target = targetFactory.controller(ctrl);

    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);

    final var expected = """
module MsgSets
exports
  Universe = Mod::Ctrl::sem__events
endmodule
    """;

    assertThat(grp, generatesCSP(expected, msg::generate));
  }
}
