/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.util;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.util.RoboChartSwitch;
import java.util.Objects;

/**
 * Formatter for expressions.
 *
 * @author Matt Windsor
 */
public class ExpressionFormatter {
  // TODO(@MattWindsor91): surely this must duplicate something?
  public static String format(Expression expr) {
    final var str = new RoboChartSwitch<String>() {
    }.doSwitch(expr);

    return Objects.requireNonNullElse(str, "?");
  }
}
