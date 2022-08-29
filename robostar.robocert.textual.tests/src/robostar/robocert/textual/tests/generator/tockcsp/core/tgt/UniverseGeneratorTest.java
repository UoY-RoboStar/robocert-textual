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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.core.tgt.UniverseGenerator;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests aspects of {@link UniverseGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class UniverseGeneratorTest {

  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private TargetFactory targetFactory;

  /**
   * The system under test.
   */
  @Inject
  private UniverseGenerator gen;

  /**
   * Tests generation of sem-events for a specification group targeting a controller nested inside a
   * module, but with no user-defined sets.
   *
   * <p>This is a regression test for GitHub issue #109.
   */
  @Test
  void testGenerate_NestedTarget() {
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

    assertThat(grp.getTarget(), generatesCSP("Mod::Ctrl::sem__events", gen::generate));
  }


  /**
   * Tests generation of sem-events for a specification group targeting components of a module.
   *
   * <p>This is a regression test for GitHub issue #123.
   */
  @Test
  void testGenerate_InModule() {
    final var c1 = chartFactory.createControllerDef();
    c1.setName("C1");
    final var c2 = chartFactory.createControllerDef();
    c2.setName("C2");

    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");
    mod.getNodes().addAll(List.of(c1, c2));

    final var target = targetFactory.inModule(mod);

    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);

    assertThat(grp.getTarget(),
        generatesCSP("union(Mod::C1::sem__events, Mod::C2::sem__events)", gen::generate));
  }
}
