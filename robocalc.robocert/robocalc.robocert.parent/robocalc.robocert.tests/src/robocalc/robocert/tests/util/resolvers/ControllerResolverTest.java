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

package robocalc.robocert.tests.util.resolvers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.util.resolve.ControllerResolver;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests various aspects of {@link ControllerResolver}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ControllerResolverTest {
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private ControllerResolver resolver;

  private RCModule mod;
  private ControllerDef ctrl;

  @BeforeEach
  void setUp() {
    mod = chartFactory.createRCModule();
    mod.setName("Mod");

    ctrl = chartFactory.createControllerDef();
    mod.getNodes().add(ctrl);
    ctrl.setName("Ctrl");
  }

  /**
   * Tests that name resolution for controllers works properly.
   */
  @Test
  void testName() {
    assertThat(resolver.name(ctrl), is(arrayContaining("Mod", "Ctrl")));
  }

  /**
   * Tests that module resolution for controllers works properly.
   */
  @Test
  void testModule() {
    final var result = resolver.module(ctrl);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(mod));
  }

  /**
   * Tests that module resolution for controllers with no module behaves as expected.
   */
  @Test
  void testModule_noModule() {
    final var c2 = chartFactory.createControllerDef();
    assertThat(resolver.module(c2).isEmpty(), is(true));
  }
}
