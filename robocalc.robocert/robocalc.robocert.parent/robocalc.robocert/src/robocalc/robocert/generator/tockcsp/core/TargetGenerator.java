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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.utils.VariableHelper;
import robocalc.robocert.generator.utils.param.Parameter;
import robocalc.robocert.generator.utils.param.TargetParameterResolver;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.InstantiationHelper;

/**
 * Generates CSP-M for target definitions and parameterisations.
 *
 * @author Matt Windsor
 */
public record TargetGenerator(CTimedGeneratorUtils gu,
                              CSPStructureGenerator csp,
                              ExpressionGenerator eg,
                              TargetParameterResolver paramRes,
                              InstantiationHelper instHelp,
                              VariableHelper varHelp) {

  /**
   * Hardcoded ID (will need to be fixed if we support collections of robots).
   */
  private static final String ID = "{- id -} 0";

  /**
   * Eventually this should be exposed to the user.
   */
  private static final boolean USE_OPTIMISED_TARGETS = true;

  @Inject
  public TargetGenerator {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(csp);
    Objects.requireNonNull(eg);
    Objects.requireNonNull(paramRes);
    Objects.requireNonNull(instHelp);
    Objects.requireNonNull(varHelp);
  }

  /**
   * Generates an instantiated form of a target process.
   *
   * @param t    the target.
   * @param inst any constant assignments defined alongside the target.
   * @return CSP-M for the target definition.
   * @implNote This is currently just a reference to the corresponding RoboStar model element, but
   * this might change if we add any targets that don't correspond to such things. There is
   * currently no way to specify an optimised definition.
   */
  public CharSequence openDef(Target t, List<ConstAssignment> inst) {
    final var params = paramRes.parameterisation(t).toList();
    return """
        -- Begin overrides to instantiations.csp
        %s
        -- End overrides to instantiations.csp
        %s
        """.formatted(overrides(inst, params), targetProcess(t, params));
  }

  private CharSequence overrides(List<ConstAssignment> inst, List<Parameter> params) {
    return params.stream().flatMap(p -> override(inst, p)).collect(Collectors.joining("\n"));
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
      return Stream.of(
          commentedDefinition("RoboChart", id, initial.get()));
    }

    final var asst = p.tryGetConstant().flatMap(k -> instHelp.getConstant(inst, k));
    return asst.map(
            expression -> commentedDefinition("RoboCert", id, expression))
        .stream();
  }

  private String commentedDefinition(String comment, String id, Expression expression) {
    // trim is necessary because csp.definition emits a newline by default.
    final var def = csp.definition(id, eg.generate(expression)).toString().trim();
    return "%s -- initialised in %s".formatted(def, comment);
  }


  private CharSequence targetProcess(Target t, List<Parameter> params) {
    /*
     * In email with Pedro (2021-08-04): the target of a refinement against a (simple)
     * specification should usually be unoptimised (D__); model comparisons should
     * usually be optimised (O__).  However, in practice c. 2022-03-28, it seems that O__ is
     * outperforming D__ across the board and is safe to enable.  This might change gain in future.
     *
     * TODO(@MattWindsor91): eventually, we should be able to select the
     * optimisation level.
     */
    final var name = gu.getFullProcessName(t.getElement(), false, USE_OPTIMISED_TARGETS);
    final var args = Stream.concat(Stream.of(ID),
        params.stream().map(k -> k.cspId(gu))).toArray(CharSequence[]::new);
    return csp.definition(SpecGroupParametricField.TARGET.toString(), csp.function(name, args));
  }

  /**
   * Uses a target to generate the parameter list of an open specification.
   *
   * @param t        the target.
   * @param lastInst any instantiation that has already been applied to the target.
   * @param thisInst the instantiation being applied to the target here.
   * @return an array of CSP-M elements corresponding to the arguments of a target that are not yet
   * instantiated, instantiated with the given instantiation.
   */
  public CharSequence[] openSigParams(
      Target t, List<ConstAssignment> lastInst, List<ConstAssignment> thisInst) {
    // TODO(@MattWindsor91): work out what we need here to have derived
    // groups.  Maybe a stack of instantiations?
    var params = paramRes.parameterisation(t);
    params = paramRes.excludeWithValue(params);
    params = paramRes.excludeInstantiated(gu, params, lastInst);
    return params.map(k -> generateParam(thisInst, k)).toArray(CharSequence[]::new);
  }

  /**
   * Generates a parameter of a target open signature given an instantiation.
   *
   * <p>If the value isn't available, we emit the constant ID; this will resolve either to a
   * formal parameter on the target (when defining an open target) or a definition in
   * instantiations.csp (when defining a closed target).
   *
   * <p>If the value is available, we emit a CSP comment giving the name, for clarity.
   *
   * @param inst the instantiation (may be null).
   * @param p the parameter whose value is requested.
   * @return a CSP string expanding to the value of the constant.
   */
  private CharSequence generateParam(List<ConstAssignment> inst, Parameter p) {
    final var id = p.cspId(gu);
    // TODO(@MattWindsor91): this'll need generalising if we allow instantiations of non-constant
    // parameters.
    final var expr = p.tryGetConstant().flatMap(k -> constantValue(inst, k));
    return expr.map(i -> generateNamedExpression(i, id)).orElse(id);
  }

  /**
   * Tries to get a constant's initial value; failing that, its instantiation.
   *
   * <p>It is ill-formed for a constant to have both an initial value (from the RoboChart end) and
   * an instantiation (from the RoboCert end), so those two forms of value acquisition should be
   * disjoint in practice.
   *
   * @param inst the instantiation to fall back into.
   * @param k    the constant to investigate.
   * @return the retrieved expression, if any.
   */
  private Optional<Expression> constantValue(List<ConstAssignment> inst, Variable k) {
    final var initial = Optional.ofNullable(k.getInitial());
    return initial.or(() -> instHelp.getConstant(inst, k));
  }

  private CharSequence generateNamedExpression(Expression it, CharSequence id) {
    return "{- %s -} %s".formatted(id, eg.generate(it));
  }
}
