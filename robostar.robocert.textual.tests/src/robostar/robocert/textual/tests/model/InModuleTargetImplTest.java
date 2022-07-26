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
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.InControllerTarget;
import robostar.robocert.InModuleTarget;
import robostar.robocert.ModuleTarget;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests basic resolution and stringifying functionality on {@link ModuleTarget}s.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class InModuleTargetImplTest extends TargetTest<InModuleTarget> {
  @Inject private RoboCertFactory rf;
  @Inject private RoboChartFactory cf;

  private Controller ctrl1;
  private Controller ctrl2;
  private RCModule module;

  @BeforeEach
  void setUp() {
    ctrl1 = cf.createControllerDef();
    ctrl1.setName("ctrl1");

    ctrl2 = cf.createControllerDef();
    ctrl2.setName("ctrl2");

    final var rp = cf.createRoboticPlatformDef();
    rp.setName("rp");

    module = cf.createRCModule();
    module.setName("foo");
    module.getNodes().addAll(List.of(ctrl1, ctrl2, rp));

    example = rf.createInModuleTarget();
    example.setModule(module);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {ctrl1, ctrl2};
  }

  @Override
  protected NamedElement expectedElement() {
    return module;
  }

  @Override
  protected String expectedString() {
    return "components of module foo";
  }
}
