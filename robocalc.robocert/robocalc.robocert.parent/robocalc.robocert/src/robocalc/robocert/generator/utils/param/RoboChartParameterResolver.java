/*******************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa and Pedro Ribeiro - initial definition in RoboChart
 *   Matt Windsor - porting to RoboCert
 ******************************************************************************/
package robocalc.robocert.generator.utils.param;

import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineBody;
import circus.robocalc.robochart.StateMachineDef;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

/**
 * Resolves parameterisations for RoboChart elements.
 * <p>
 * This is heavily based on the logic inherent to the RoboChart CSP generator's templates.
 *
 * @author Matt Windsor
 */
public record RoboChartParameterResolver(CTimedGeneratorUtils gu, DefinitionResolver defResolver) {

  /**
   * Constructs a module parameter resolver.
   *
   * @param gu          upstream RoboChart generator utilities.
   * @param defResolver resolver for RoboChart definitions.
   */
  @Inject
  public RoboChartParameterResolver {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(defResolver);
  }

  //
  // Modules
  //

  /**
   * Gets the variables that make up this module's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param mod the RoboChart module.
   * @return a stream over module parameters.
   */
  public Stream<Parameter> parameterisation(RCModule mod) {
    final var platformParams = defResolver.platform(mod).stream().flatMap(this::localsOf);
    final var ctrlParams = defResolver.controllers(mod).map(defResolver::resolve)
        .flatMap(this::moduleParameterisation);
    return Stream.concat(platformParams, ctrlParams);
  }

  //
  // Controllers
  //

  /**
   * Gets this controller's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param ctrl the RoboChart controller.
   * @return a stream over controller parameters.
   */
  public Stream<Parameter> parameterisation(ControllerDef ctrl) {
    return Stream.concat(requiredParams(ctrl), moduleParameterisation(ctrl));
  }

  /**
   * Gets this controller's contribution to its module's parameterisation.
   *
   * @param ctrl the controller.
   * @return the stream of variables that should be added to the module parameterisation to account
   * for this controller.
   */
  private Stream<Parameter> moduleParameterisation(ControllerDef ctrl) {
    final var localOpParams =
        ctrl.getLOperations().stream().map(defResolver::resolve).flatMap(this::localsOf);
    final var stmParams = ctrl.getMachines().stream().map(defResolver::resolve)
        .flatMap(this::localsOf);
    return Stream.concat(localsOf(ctrl), Stream.concat(stmParams, localOpParams));
  }

  //
  // State machines
  //

  /**
   * Gets this state machine's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param stm the RoboChart state machine.
   * @return a stream over state machine parameters.
   */
  public Stream<Parameter> parameterisation(StateMachineDef stm) {
    return bodyParameterisation(stm);
  }

  //
  // Operations
  //

  /**
   * Gets this operation's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param op the RoboChart operation.
   * @return a stream over operation parameters.
   */
  public Stream<Parameter> parameterisation(OperationDef op) {
    final var formalParams = op.getParameters().stream().map(FormalParameter::new);
    return Stream.concat(formalParams, bodyParameterisation(op));
  }

  //
  // Helpers
  //

  private Stream<Parameter> bodyParameterisation(StateMachineBody body) {
    // TODO(@MattWindsor91): what is 'defined' here?
    final var ops = gu.requiredOperationDefinitions(Set.of(), gu.requiredOperations(body));
    final var opParams = ops.stream().flatMap(this::localsOf);
    return Stream.concat(requiredParams(body), Stream.concat(localsOf(body), opParams));
  }

  private Stream<Parameter> requiredParams(Context ctx) {
    return gu.requiredConstants(ctx).stream().map(x -> new ConstantParameter(x, ctx));
  }

  private Stream<Parameter> localsOf(EObject it) {
    return ConstantParameter.localsOf(it, gu);
  }
}
