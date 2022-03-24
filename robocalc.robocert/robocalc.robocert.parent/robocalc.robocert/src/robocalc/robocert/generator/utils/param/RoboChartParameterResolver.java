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
import circus.robocalc.robochart.RoboticPlatformDef;
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

  /**
   * Gets the variables that make up this module's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param it the RoboChart module.
   * @return a stream over parameters.
   */
  public Stream<Parameter> parameterisation(RCModule it) {
    return Stream.concat(platformParams(it), controllerParams(it));
  }

  private Stream<Parameter> platformParams(RCModule it) {
    return defResolver.platform(it).stream().flatMap(this::platformParams);
  }

  private Stream<Parameter> platformParams(RoboticPlatformDef rp) {
    return Parameter.localsOf(rp, gu);
  }

  private Stream<Parameter> controllerParams(RCModule it) {
    return defResolver.controllers(it).flatMap(this::moduleParameterisation);
  }

  /**
   * Gets this controller's contribution to its module's parameterisation.
   *
   * @param it  the controller.
   *
   * @return the stream of variables that should be added to the module
   *         parameterisation to account for this controller.
   */
  public Stream<Parameter> moduleParameterisation(ControllerDef it) {
    // TODO(@MattWindsor91): is this the controller parameterisation too?
    return Stream.concat(
        constantsOf(it),
        Stream.concat(stateMachineConstants(it), localOperationConstants(it))
    );
  }

  private Stream<Parameter> localOperationConstants(ControllerDef it) {
    return it.getLOperations().stream().map(defResolver::resolve).flatMap(this::constantsOf);
  }

  private Stream<Parameter> stateMachineConstants(ControllerDef it) {
    return it.getMachines().stream().map(defResolver::resolve).flatMap(this::constantsOf);
  }

  private Stream<Parameter> constantsOf(EObject it) {
    return Parameter.localsOf(it, gu);
  }
}
