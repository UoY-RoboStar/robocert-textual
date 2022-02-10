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

package robocalc.robocert.tests.util;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import com.google.inject.Inject;
import java.util.Objects;

/**
 * Helper for producing variables for testing.
 *
 * @author Matt Windsor
 */
public record DummyVariableFactory(RoboChartFactory roboChart) {
  @Inject
  public DummyVariableFactory {
    Objects.requireNonNull(roboChart);
  }

  /**
   * Constructs a dummy constant with the given name, and arbitrary type.
   * @param name the name of the variable.
   * @return the dummy constant.
   */
  public Variable constant(String name) {
    return var(name, VariableModifier.CONST);
  }

  /**
   * Constructs a dummy memory variable with the given name, and arbitrary type.
   * @param name the name of the variable.
   * @return the dummy memory variable.
   */
  public Variable mem(String name) {
    return var(name, VariableModifier.VAR);
  }

  /**
   * Constructs a dummy variable with the given name and modifier, and arbitrary type.
   * @param name the name of the variable.
   * @param modifier the modifier of the variable.
   * @return the dummy variable.
   */
  public Variable var(String name, VariableModifier modifier) {
    final var v = roboChart.createVariable();

    // We can't directly set the modifier, so we create a dummy variable list to hold it:
    final var vl = roboChart.createVariableList();
    vl.setModifier(modifier);
    vl.getVars().add(v);

    v.setName(name);
    v.setType(roboChart().createAnyType());

    return v;
  }
}
