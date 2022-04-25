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
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.util.resolve.StateMachineResolver;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests various aspects of {@link StateMachineResolver}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class StateMachineResolverTest {
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private StateMachineResolver resolver;

  private ControllerDef ctrl;
  private StateMachineDef stm;
  private OperationDef op;

  @BeforeEach
  void setUp() {
    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");

    ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");
    mod.getNodes().add(ctrl);

    stm = chartFactory.createStateMachineDef();
    stm.setName("Stm");
    ctrl.getMachines().add(stm);

    op = chartFactory.createOperationDef();
    op.setName("Op");
    ctrl.getLOperations().add(op);
  }

  /**
   * Tests that name resolution for state machines and operators works properly.
   */
  @Test
  void testName() {
    // TODO(@MattWindsor91): stm/op not in mod/ctrl
    assertThat(resolver.name(stm), is(arrayContaining("Mod", "Ctrl", "Stm")));
    assertThat(resolver.name(op), is(arrayContaining("Mod", "Ctrl", "OP_Op")));
  }

  /**
   * Tests that module resolution for controllers works properly.
   */
  @Test
  void testController() {
    final var result = resolver.controller(stm);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(ctrl));

    final var result2 = resolver.controller(op);
    assertThat(result2.isPresent(), is(true));
    assertThat(result2.get(), is(ctrl));
  }

  /**
   * Tests that module resolution for controllers with no module behaves as expected.
   */
  @Test
  void testController_noController() {
    final var s2 = chartFactory.createStateMachineDef();
    assertThat(resolver.controller(s2).isEmpty(), is(true));
    final var o2 = chartFactory.createOperationDef();
    assertThat(resolver.controller(o2).isEmpty(), is(true));
  }
}
