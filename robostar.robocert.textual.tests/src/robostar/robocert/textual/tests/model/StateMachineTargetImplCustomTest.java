/********************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robostar.robocert.textual.tests.model;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.StateMachineTarget;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link StateMachineTarget}s, and also tests that the factory
 * resolves them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class StateMachineTargetImplCustomTest extends TargetImplCustomTest<StateMachineTarget> {
  @Inject private RoboCertFactory rf;
  @Inject private RoboChartFactory cf;

  private StateMachineDef def;

  @BeforeEach
  void setUp() {
    example = rf.createStateMachineTarget();

    def = cf.createStateMachineDef();
    def.setName("foo");
    example.setStateMachine(def);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {};
  }

  @Override
  protected NamedElement expectedElement() {
    return def;
  }

  @Override
  protected String expectedString() {
    return "state machine foo";
  }
}
