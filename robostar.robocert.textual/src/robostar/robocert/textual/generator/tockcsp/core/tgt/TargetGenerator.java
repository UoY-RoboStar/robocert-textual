/*
 * Copyright (c) 2021-2022 University of York and others
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
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import robostar.robocert.textual.generator.tockcsp.core.ExpressionGenerator;
import robostar.robocert.textual.generator.utils.param.Parameter;
import robostar.robocert.textual.generator.utils.param.TargetParameterResolver;
import robostar.robocert.ComponentTarget;
import robostar.robocert.ConstAssignment;
import robostar.robocert.InControllerTarget;
import robostar.robocert.InModuleTarget;
import robostar.robocert.Target;
import robostar.robocert.util.InstantiationHelper;

/**
 * Generates CSP-M for target definitions and parameterisations.
 *
 * @author Matt Windsor
 */
public record TargetGenerator(InControllerTargetBodyGenerator ctrlGen,
                              InModuleTargetBodyGenerator modGen,
                              ComponentTargetBodyGenerator compGen,
                              CTimedGeneratorUtils gu,
                              ExpressionGenerator eg, TargetParameterResolver paramRes,
                              InstantiationHelper instHelp) {

  // TODO(@MattWindsor91): this class is doing three or so different things.

  /**
   * Hardcoded ID (may need to be changed if we support collections of robots).
   * <p>
   * The value of this is defined in the block of overrides for the specification group.
   */
  public static final String ID = "id__";


  @Inject
  public TargetGenerator {
    Objects.requireNonNull(ctrlGen);
    Objects.requireNonNull(modGen);
    Objects.requireNonNull(compGen);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(eg);
    Objects.requireNonNull(paramRes);
    Objects.requireNonNull(instHelp);
  }

  /**
   * Generates an instantiated form of a target process.
   *
   * @param t the target.
   * @return CSP-M for the target definition.
   */
  public CharSequence openDef(Target t) {
    if (t instanceof InModuleTarget m) {
      return modGen.generate(m.getModule());
    }
    if (t instanceof InControllerTarget c) {
      return ctrlGen.generate(c.getController());
    }
    if (t instanceof ComponentTarget c) {
      return compGen.generate(c);
    }

    throw new IllegalArgumentException("unsupported target type for generation: %s".formatted(t));
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
  public CharSequence[] openSigParams(Target t, List<ConstAssignment> lastInst,
      List<ConstAssignment> thisInst) {
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
   * @param p    the parameter whose value is requested.
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
