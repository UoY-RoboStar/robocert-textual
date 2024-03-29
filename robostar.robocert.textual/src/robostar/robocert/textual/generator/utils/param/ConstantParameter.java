/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.utils.param;

import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;

/**
 * A located target parameter, consisting of both the variable and the model object on which it is
 * a parameter.
 *
 * This record exists because the variable's containing object in the EMF world isn't always the
 * place where we define the parameter in the semantics; for instance, the variable might belong
 * to an interface which is then required by the target.
 *
 * This record, and much of its supporting code, derives from the GeneratorUtils parameterisation
 * code upstream.
 *
 * @author Matt Windsor
 */
public record ConstantParameter(Variable constant, EObject container) implements Parameter {
  /**
   * Constructs a parameter record.
   * @param constant the constant forming the parameter.
   * @param container the effective model object on which the constant is a parameter.
   */
  public ConstantParameter {
    Objects.requireNonNull(constant);
    Objects.requireNonNull(container);
  }

  @Override
  public String cspId(CGeneratorUtils gu) {
    // TODO(@MattWindsor91): reduce tight coupling with the CSP generator
    return gu.constantId(constant, container);
  }

  @Override
  public Optional<Variable> tryGetConstant() {
    return Optional.of(constant);
  }

  /**
   * Constructs a parameter stream by taking all local constants of a container.
   * @param container the container to search for parameters.
   * @param gu the generator utilities object used to find the local constants.
   * @return the
   */
  public static Stream<Parameter> localsOf(EObject container, CGeneratorUtils gu) {
    // TODO(@MattWindsor91): reduce tight coupling with the CSP generator
    return gu.allLocalConstants(container).parallelStream().map(k -> new ConstantParameter(k, container));
  }
}
