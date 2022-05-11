/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.ModuleTarget;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link ModuleTarget}s, and also tests that the factory resolves
 * them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ModuleTargetImplCustomTest extends TargetImplCustomTest<ModuleTarget> {
  @Inject private RoboCertFactory rf;
  @Inject private RoboChartFactory cf;

  private RCModule module;

  @BeforeEach
  void setUp() {
    module = cf.createRCModule();
    module.setName("foo");

    example = rf.createModuleTarget();
    example.setModule(module);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {};
  }

  @Override
  protected NamedElement expectedElement() {
    return module;
  }

  @Override
  protected String expectedString() {
    return "module foo";
  }
}
