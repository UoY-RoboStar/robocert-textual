/********************************************************************************
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
 ********************************************************************************/
package robocalc.robocert.tests.model;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.InControllerTarget;
import robostar.robocert.RoboCertFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link InControllerTarget}s, and also tests that the factory
 * resolves them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class InControllerTargetImplCustomTest extends TargetImplCustomTest<InControllerTarget> {
  @Inject private RoboCertFactory rf;
  @Inject private RoboChartFactory cf;

  private ControllerDef ctrl;
  private StateMachineDef stm;
  private OperationDef op;

  @BeforeEach
  void setUp() {
    stm = cf.createStateMachineDef();
    stm.setName("stm");

    op = cf.createOperationDef();
    op.setName("op");

    final var rp = cf.createRoboticPlatformDef();
    rp.setName("rp");

    ctrl = cf.createControllerDef();
    ctrl.setName("foo");
    ctrl.getMachines().add(stm);
    ctrl.getLOperations().add(op);

    example = rf.createInControllerTarget();
    example.setController(ctrl);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {stm, op};
  }

  @Override
  protected NamedElement expectedElement() {
    return ctrl;
  }

  @Override
  protected String expectedString() {
    return "components of controller foo";
  }
}
