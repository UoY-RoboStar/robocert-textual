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

package robostar.robocert.textual.tests.generator.util.param;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.utils.param.ConstantParameter;
import robostar.robocert.textual.tests.util.DummyVariableFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/** Tests functionality of {@link ConstantParameter}s. */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class ConstantParameterTest {
  @Inject private CTimedGeneratorUtils gu;
  @Inject private DummyVariableFactory varFactory;
  @Inject private RoboChartFactory roboChartFactory;

  /**
   * Tests that the cspId of a parameter depends on its container and not the container of its
   * constant; for instance, a constant defined in an interface isn't attributed to that interface.
   */
  @Test
  public void testCspId_interface() {
    final var vl = varFactory.constantList("var");

    final var iface = roboChartFactory.createInterface();
    iface.setName("iface");
    iface.getVariableList().add(vl);

    final var ctrl = roboChartFactory.createControllerDef();
    ctrl.setName("ctrl");
    // We're not testing lookup of constants here, so we don't link the interface to ctrl.
    final var p = new ConstantParameter(vl.getVars().get(0), ctrl);

    assertThat(p.cspId(gu), is("const_ctrl_var"));
  }
}
