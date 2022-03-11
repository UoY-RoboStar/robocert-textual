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
package robocalc.robocert.generator.tockcsp.core;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import robocalc.robocert.generator.utils.TargetParameterResolver;
import robocalc.robocert.generator.utils.VariableHelper;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.InstantiationHelper;

/**
 * Generates CSP-M for the various forms of a target.
 *
 * @author Matt Windsor
 */
public record TargetBodyGenerator(CTimedGeneratorUtils gu, ExpressionGenerator eg, TargetParameterResolver paramRes, InstantiationHelper instHelp, VariableHelper varHelp) {
  private static final String ID = "{- id -} 0";
  
  @Inject
  public TargetBodyGenerator {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(eg);
    Objects.requireNonNull(paramRes);
    Objects.requireNonNull(instHelp);
    Objects.requireNonNull(varHelp);
  }

  /**
   * Generates the RHS of an open target definition.
   *
   * @implNote This is currently just a reference to the corresponding RoboStar model element, but
   *     this might change if we add any targets that don't correspond to such things. There is
   *     currently no way to specify an optimised definition.
   * @param t the target.
   * @return CSP-M for the target definition.
   */
  public CharSequence generateDef(Target t) {
    /*
     * In email with Pedro (4 Aug): the target of a refinement against a (simple)
     * specification should usually be unoptimised (D__); model comparisons should
     * usually be optimised (O__).
     *
     * TODO(@MattWindsor91): eventually, we should be able to select the
     * optimisation level.
     */
    return gu.getFullProcessName(t.getElement(), false);
  }

  /**
   * Generates the parameter list of a target reference.
   *
   * @param t the target.
   * @param lastInst any instantiation that has already been applied to the target.
   * @param thisInst the instantiation being applied to the target here.
   * @param withId whether to include the ID parameter.
   * @return an array of CSP-M elements corresponding to the arguments of a target that are not yet
   *     instantiated, instantiated with the given instantiation.
   */
  public CharSequence[] generateRefParams(
      Target t, List<ConstAssignment> lastInst, List<ConstAssignment> thisInst, boolean withId) {
    // TODO(@MattWindsor91): work out what we need here to have derived
    // groups.  Maybe a stack of instantiations?
    var params =
        paramRes.excludeInstantiated(paramRes.parameterisation(t), lastInst)
            .map(k -> generateConstant(thisInst, k));
    if (withId) params = Stream.concat(Stream.of(ID), params);
    return params.toArray(CharSequence[]::new);
  }

  // TODO(@MattWindsor91): move this?

  /**
   * Generates the value of a constant given an instantiation.
   *
   * <p>If the value isn't available, we emit the constant ID; this will resolve either to a
   * parameter (when defining an open target) or a definition in instantiations.csp (when defining a
   * closed target).
   *
   * <p>If the value is available, we emit a CSP comment giving the name, for clarity.
   *
   * @param inst the instantiation (may be null).
   * @param k the constant whose value is requested.
   * @return a CSP string expanding to the value of the constant.
   */
  private CharSequence generateConstant(List<ConstAssignment> inst, Variable k) {
    final var id = varHelp.constantId(k);
    final var expr = instHelp.getConstant(inst, k);
    return expr.map(i -> generateNamedExpression(i, id)).orElse(id);
  }



  private CharSequence generateNamedExpression(Expression it, CharSequence id) {
    return "{- %s -} %s".formatted(id, eg.generate(it));
  }
}
