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

package robostar.robocert.textual.generator.utils.param;

import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import java.util.Optional;

/**
 * Uses a formal parameter of an operation as a target parameter.
 * @param param the parameter to wrap.
 *
 * @author Matt Windsor
 */
public record FormalParameter(circus.robocalc.robochart.Parameter param) implements Parameter {
  @Override
  public String cspId(CGeneratorUtils gu) {
    return gu.paramId(param);
  }

  @Override
  public Optional<Variable> tryGetConstant() {
    return Optional.empty();
  }
}
