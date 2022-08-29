/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.seq.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.Optional;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.seq.message.NamedSetModuleGenerator;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

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
   * Tests that a module for a specification group targeting a controller nested inside a
   * module, but with no user-defined sets, does not generate a message set.
   */
  @Test
  void testGenerate_nestedTarget_empty() {
    // This used to be a regression test for GitHub issue #109, but that is now done in
    // TargetGeneratorTest.  Instead, this mainly just makes sure that we're not putting the
    // universe or anything similar in the message sets.

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

    assertThat(msg.generate(grp), is(Optional.empty()));
  }
}
