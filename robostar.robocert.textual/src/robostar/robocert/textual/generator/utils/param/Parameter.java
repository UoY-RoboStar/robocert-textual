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
import java.util.Optional;

/**
 * Common denominator of functionality of things that make up a target parameterisation.
 *
 * @author Matt Windsor
 */
public interface Parameter {
  /**
   * Gets the CSP-M ID of the parameter (as used in parameterisation files, etc).
   *
   * @param gu a RoboChart CSP-M generator utilities object.
   * @return the snake-cased constant identifier of the parameter.
   */
  String cspId(CGeneratorUtils gu);

  /**
   * @return the underlying constant of this parameter, if one exists.
   */
  Optional<Variable> tryGetConstant();
}
