/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Type;
import circus.robocalc.robochart.textual.RoboCalcTypeProvider;
import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.wfc.seq.MessageArgumentsChecker.ExpressionTypeChecker;

/**
 * Wraps a {@link RoboCalcTypeProvider} so that it can be an {@link ExpressionTypeChecker}.
 *
 * @param typeProvider the type provider to wrap
 * @author Matt Windsor
 */
public record RoboCalcExpressionTypeChecker(RoboCalcTypeProvider typeProvider) implements
    ExpressionTypeChecker {

  @Inject
  public RoboCalcExpressionTypeChecker {
    Objects.requireNonNull(typeProvider);
  }

  @Override
  public boolean checkType(Expression expression, Type type) {
    final var got = typeProvider.typeFor(expression);
    return typeProvider.typeCompatible(got, type);
  }
}
