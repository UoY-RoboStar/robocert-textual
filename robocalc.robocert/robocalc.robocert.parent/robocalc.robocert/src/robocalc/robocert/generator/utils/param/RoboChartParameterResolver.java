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

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

/**
 * Resolves parameterisations for RoboChart elements.
 *
 * This is heavily based on the logic inherent to the RoboChart CSP generator's templates.
 *
 * @author Matt Windsor
 */
public record RoboChartParameterResolver(CTimedGeneratorUtils gu, DefinitionResolver defResolver) {
  /**
   * Constructs a module parameter resolver.
   * @param gu upstream RoboChart generator utilities.
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
    return Stream.concat(platformParams(mod), controllerParams(mod));
  }

  private Stream<Parameter> platformParams(RCModule mod) {
    return defResolver.platform(mod).stream().flatMap(this::constantsOf);
  }

  private Stream<Parameter> controllerParams(RCModule mod) {
    return defResolver.controllers(mod).flatMap(this::moduleParameterisation);
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
   * @return a stream over module parameters.
   */
  public Stream<Parameter> parameterisation(ControllerDef ctrl) {
    final var requiredConstants =
        gu.requiredConstants(ctrl).stream().map(x -> new Parameter(x, ctrl));
    return Stream.concat(requiredConstants, moduleParameterisation(ctrl));
  }

  /**
   * Gets this controller's contribution to its module's parameterisation.
   *
   * @param ctrl the controller.
   *
   * @return the stream of variables that should be added to the module
   *         parameterisation to account for this controller.
   */
  private Stream<Parameter> moduleParameterisation(ControllerDef ctrl) {
    final var localOperationConstants =
        ctrl.getLOperations().stream().map(defResolver::resolve).flatMap(this::constantsOf);
    return Stream.concat(
        constantsOf(ctrl),
        Stream.concat(stateMachineConstants(ctrl), localOperationConstants)
    );
  }

  private Stream<Parameter> stateMachineConstants(ControllerDef ctrl) {
    return ctrl.getMachines().stream().map(defResolver::resolve).flatMap(this::constantsOf);
  }

  //
  // Misc
  //

  private Stream<Parameter> constantsOf(EObject it) {
    return Parameter.localsOf(it, gu);
  }
}
