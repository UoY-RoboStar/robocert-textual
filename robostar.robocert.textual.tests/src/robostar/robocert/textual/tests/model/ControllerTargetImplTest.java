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
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.ControllerTarget;
import robostar.robocert.ModuleTarget;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests basic resolution and stringifying functionality on {@link ControllerTarget}s.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ControllerTargetImplTest extends TargetTest<ControllerTarget> {
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
