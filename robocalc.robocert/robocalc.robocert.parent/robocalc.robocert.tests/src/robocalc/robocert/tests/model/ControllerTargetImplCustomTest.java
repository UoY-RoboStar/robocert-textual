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
package robocalc.robocert.tests.model;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.ControllerTarget;
import robostar.robocert.RoboCertFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link ControllerTarget}s, and also tests that the factory
 * resolves them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ControllerTargetImplCustomTest extends TargetImplCustomTest<ControllerTarget> {
  @Inject private RoboCertFactory rf;
  @Inject private RoboChartFactory cf;

  private ControllerDef def;

  @BeforeEach
  void setUp() {
    example = rf.createControllerTarget();

    def = cf.createControllerDef();
    def.setName("foo");
    example.setController(def);
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
    return "controller foo";
  }
}
