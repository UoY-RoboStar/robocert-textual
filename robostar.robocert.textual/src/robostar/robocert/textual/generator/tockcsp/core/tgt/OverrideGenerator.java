/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robostar.robocert.textual.generator.tockcsp.core.ExpressionGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.utils.param.Parameter;
import robostar.robocert.ConstAssignment;
import robostar.robocert.util.InstantiationHelper;
import robostar.robocert.util.StreamHelper;

/**
 * Generates overrides of constants from RoboChart and RoboCert instantiations.
 *
 * @param csp      the low-level CSP generator.
 * @param eg       the expression generator.
 * @param instHelp the instantiation helper.
 * @param gu       the RoboChart generator utilities object.
 * @author Matt Windsor
 */
public record OverrideGenerator(CSPStructureGenerator csp, ExpressionGenerator eg,
                                InstantiationHelper instHelp, CGeneratorUtils gu) {

  /**
   * Constructs an override generator.
   *
   * @param csp      the low-level CSP generator.
   * @param eg       the expression generator.
   * @param instHelp the instantiation helper.
   * @param gu       the RoboChart generator utilities object.
   */
  @Inject
  public OverrideGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(eg);
    Objects.requireNonNull(instHelp);
    Objects.requireNonNull(gu);
  }

  /**
   * Generates the override set for a target given its instantiation.
   *
   * @param inst   the instantiation from which we are taking RoboCert overrides.
   * @param params the parameters we intend to override.
   * @return CSP-M for the overrides block.
   */
  public CharSequence generate(List<ConstAssignment> inst, List<Parameter> params) {
    final var id = csp.definition(TargetGenerator.ID, "0");
    final var paramOverrides = params.stream().flatMap(p -> override(inst, p));
    return StreamHelper.push(id, paramOverrides)
        .collect(Collectors.joining("\n", "-- begin overrides\n", "\n-- end overrides"));
  }

  /**
   * Generates any override needed for a parameter given an instantiation.
   *
   * <p>This will replace the definition in instantiations.csp in the scope of any specifications
   * defined on the relevant group.
   *
   * @param inst the instantiation (may be null).
   * @param p    the parameter whose value is requested.
   * @return a zero-or-one element stream containing CSP for the parameter.
   */
  private Stream<String> override(List<ConstAssignment> inst, Parameter p) {
    final var id = p.cspId(gu);

    final var initial = p.tryGetConstant().map(Variable::getInitial);
    if (initial.isPresent()) {
      return Stream.of(commentedDefinition("RoboChart", id, initial.get()));
    }

    final var asst = p.tryGetConstant().flatMap(k -> instHelp.getConstant(inst, k));
    return asst.map(expression -> commentedDefinition("RoboCert", id, expression)).stream();
  }

  private String commentedDefinition(String comment, String id, Expression expression) {
    // trim is necessary because csp.definition emits a newline by default.
    final var def = csp.definition(id, eg.generate(expression)).toString().trim();
    return "%s -- initialised in %s".formatted(def, comment);
  }
}
