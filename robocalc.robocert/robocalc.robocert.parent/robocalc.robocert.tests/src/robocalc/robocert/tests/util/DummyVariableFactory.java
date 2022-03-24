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
import circus.robocalc.robochart.VariableList;
import circus.robocalc.robochart.VariableModifier;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
    return var(VariableModifier.CONST, name);
  }

  /**
   * Constructs a dummy constant list with the given names, and arbitrary type.
   * @param names the name of the constants.
   * @return the dummy constants.
   */
  public VariableList constantList(String ...names) {
    return varList(VariableModifier.CONST, names);
  }

  /**
   * Constructs a dummy memory variable with the given name, and arbitrary type.
   * @param name the name of the variable.
   * @return the dummy memory variable.
   */
  public Variable mem(String name) {
    return var(VariableModifier.VAR, name);
  }

  /**
   * Constructs a dummy variable with the given name and modifier, and arbitrary type.
   * @param modifier the modifier of the variable.
   * @param name the name of the variable.
   * @return the dummy variable.
   */
  public Variable var(VariableModifier modifier, String name) {
    final Variable v = unlistedVar(name);
    // We can't directly set the modifier, so we create (and forget) a variable list to hold it:
    varListWith(modifier, List.of(v));

    return v;
  }

  /**
   * Constructs a dummy variable list with the given modifier and names, and arbitrary type.
   * @param modifier the modifier of the variables.
   * @param names the names of the variables.
   * @return the dummy variables.
   */
  public VariableList varList(VariableModifier modifier, String ...names) {
    return varListWith(modifier, Stream.of(names).map(this::unlistedVar).toList());
  }

  private VariableList varListWith(VariableModifier modifier, List<Variable> vs) {
    final var vl = roboChart.createVariableList();
    vl.setModifier(modifier);
    vl.getVars().addAll(vs);
    return vl;
  }

  private Variable unlistedVar(String name) {
    final var v = roboChart.createVariable();
    v.setName(name);
    v.setType(roboChart().createAnyType());
    return v;
  }

}
