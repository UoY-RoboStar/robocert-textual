/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tikz.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tikz.util.TargetTypeNameGenerator;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;
import robostar.robocert.util.TargetFactory;

/**
 * Tests the {@link TikzStructureGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class TargetTypeNameGeneratorTest {

  @Inject
  private TargetTypeNameGenerator gen;

  @Inject
  private TargetFactory targetFactory;
  @Inject
  private RoboChartFactory chartFactory;

  /**
   * Tests that generating a type name for a module works properly.
   */
  @Test
  void testModule() {
    final var mod = targetFactory.module(chartFactory.createRCModule());
    assertThat(gen.targetTypeName(mod), is("\\rccomptarget{module}"));
  }

  /**
   * Tests that generating a type name for an in-module works properly.
   */
  @Test
  void testInModule() {
    final var mod = targetFactory.inModule(chartFactory.createRCModule());
    assertThat(gen.targetTypeName(mod), is("\\rccolltarget{module}"));
  }
}
