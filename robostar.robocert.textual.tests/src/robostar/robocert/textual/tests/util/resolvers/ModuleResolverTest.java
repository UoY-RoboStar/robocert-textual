/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.util.resolvers;

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.util.resolve.ModuleResolver;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

/**
 * Tests various aspects of {@link ModuleResolver}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ModuleResolverTest {
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private ModuleResolver resolver;

  private RCModule mod;

  @BeforeEach
  void setUp() {
    mod = chartFactory.createRCModule();
    mod.setName("Mod");
  }

  /**
   * Tests that name resolution for modules works properly.
   */
  @Test
  void testName() {
    assertThat(resolver.name(mod), is(arrayContaining("Mod")));
  }
}
