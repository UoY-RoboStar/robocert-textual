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

package robocalc.robocert.tests.generator.util.param;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.VariableModifier;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.utils.param.Parameter;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/** Tests functionality of {@link Parameter}s. */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class ParameterTest {
  @Inject private CTimedGeneratorUtils gu;
  @Inject private RoboChartFactory roboChartFactory;

  /**
   * Tests that the cspId of a parameter depends on its container and not the container of its
   * constant; for instance, a constant defined in an interface isn't attributed to that interface.
   */
  @Test
  public void testCspId_interface() {
    // We can't use DummyVariableFactory here, as we need access to the variable list.
    final var v = roboChartFactory.createVariable();
    v.setName("var");
    v.setType(roboChartFactory.createAnyType());

    final var vl = roboChartFactory.createVariableList();
    vl.setModifier(VariableModifier.CONST);
    vl.getVars().add(v);

    final var iface = roboChartFactory.createInterface();
    iface.setName("iface");
    iface.getVariableList().add(vl);

    final var ctrl = roboChartFactory.createControllerDef();
    ctrl.setName("ctrl");
    // We're not testing lookup of constants here, so we don't link the interface to ctrl.
    final var p = new Parameter(v, ctrl);

    assertThat(p.cspId(gu), is("const_ctrl_var"));
  }
}
