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
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import java.util.function.Consumer;
import robocalc.robocert.model.robocert.ConstAssignment;

/**
 * Custom instantiation implementation, adding constant access.
 *
 * @author Matt Windsor
 */
public class InstantiationImplCustom extends InstantiationImpl {
  @Override
  public Expression getConstant(Variable v) {
    return assignments.stream()
        .mapMulti(
            (ConstAssignment x, Consumer<Expression> acc) -> {
              if (x.hasConstant(v)) {
                acc.accept(x.getValue());
              }
            })
        .findFirst()
        .orElse(null);
  }
}
