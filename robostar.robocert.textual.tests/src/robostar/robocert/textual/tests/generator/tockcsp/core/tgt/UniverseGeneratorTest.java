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

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.InControllerTarget;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;
import robostar.robocert.textual.generator.tockcsp.core.tgt.UniverseGenerator;
import robostar.robocert.textual.tests.examples.Controllers;
import robostar.robocert.textual.tests.examples.Modules;
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
    final var target = targetFactory.controller(Controllers.nested(chartFactory));

    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);

    assertThat(grp.getTarget(), generatesCSP("Mod::Ctrl::sem__events", gen::generate));
  }

  /**
   * Tests generation of sem-events for a specification group targeting a controller inside a
   * package, but with no user-defined sets.
   *
   * <p>This is a regression test for GitHub issue #136.
   */
  @Test
  void testGenerate_PackagedTarget() {
    final var target = targetFactory.controller(Controllers.packaged(chartFactory));

    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);

    assertThat(grp.getTarget(), generatesCSP("Pkg::Ctrl::sem__events", gen::generate));
  }

  /**
   * Tests generation of sem-events for a specification group targeting components directly embedded
   * in a module.
   *
   * <p>This is a regression test for GitHub issue #123.
   */
  @Test
  void testGenerate_InModule_Direct() {

    final var target = targetFactory.inModule(Modules.directControllers(chartFactory));

    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);

    assertThat(grp.getTarget(),
        generatesCSP("union(Mod::C1::sem__events, Mod::C2::sem__events)", gen::generate));
  }

  /**
   * Tests generation of sem-events for a specification group targeting components indirectly
   * referenced from a module.
   *
   * <p>This is a regression test for GitHub issues #123 and #136.
   */
  @Test
  void testGenerate_InModule_Indirect() {
    final var mod = Modules.indirectControllers(chartFactory);
    final var target = targetFactory.inModule(mod);
    final var grp = wrapInGroup(target);

    // Conjecture: module descendants should always be referred to by their reference name.
    assertThat(grp.getTarget(),
        generatesCSP("union(Mod::R1::sem__events, Mod::R2::sem__events)", gen::generate));

    // Adding a package should cause the package to appear in the namespace:
    wrapInPackage(mod);
    assertThat(grp.getTarget(),
        generatesCSP("union(Pkg::Mod::R1::sem__events, Pkg::Mod::R2::sem__events)", gen::generate));
  }

  /**
   * Tests generation of sem-events for a specification group targeting components directly
   * embedded in a controller.
   */
  @Test
  void testGenerate_InController_Direct() {
    final var ctrl = Controllers.directSubcomponents(chartFactory);
    final var target = targetFactory.inController(ctrl);
    final var grp = wrapInGroup(target);

    assertThat(grp.getTarget(),
        generatesCSP("union(Ctrl::OP_O::sem__events, Ctrl::S::sem__events)", gen::generate));

    // Adding a package should cause the package to appear in the namespace:
    wrapInPackage(ctrl);
    assertThat(grp.getTarget(),
        generatesCSP("union(Pkg::Ctrl::OP_O::sem__events, Pkg::Ctrl::S::sem__events)",
            gen::generate));
  }

  /**
   * Tests generation of sem-events for a specification group targeting components indirectly
   * referenced from a controller.
   *
   * <p>This is a regression test for GitHub issue #136.
   */
  @Test
  void testGenerate_InController_Indirect() {
    final var ctrl = Controllers.indirectSubcomponents(chartFactory);
    final var target = targetFactory.inController(ctrl);
    final var grp = wrapInGroup(target);

    // Conjecture: state machines should be referred to by their reference name;
    // operations get partly resolved.
    assertThat(grp.getTarget(),
        generatesCSP("union(Ctrl::OP_O::sem__events, Ctrl::R1::sem__events)", gen::generate));

    // Adding a package should cause the package to appear in the namespace:
    wrapInPackage(ctrl);
    assertThat(grp.getTarget(),
        generatesCSP("union(Pkg::Ctrl::OP_O::sem__events, Pkg::Ctrl::R1::sem__events)",
            gen::generate));
  }

  private SpecificationGroup wrapInGroup(Target target) {
    final var grp = targetFactory.certFactory().createSpecificationGroup();
    grp.setName("Specs");
    grp.setTarget(target);
    return grp;
  }

  private void wrapInPackage(RCModule mod) {
    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getModules().add(mod);
  }

  private void wrapInPackage(ControllerDef ctrl) {
    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getControllers().add(ctrl);
  }
}
