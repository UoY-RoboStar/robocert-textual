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

package robocalc.robocert.generator.tockcsp.seq.fragment;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.IntegerExp;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.util.Objects;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DiscreteBound;

/**
 * Generates CSP-M for a discrete bound set.
 *
 * @author Matt Windsor
 */
public record DiscreteBoundGenerator(CSPStructureGenerator csp, ExpressionGenerator expressionGen) {

  /**
   * Constructs a discrete bound generator.
   * @param csp helper used to generate valid CSP-M.
   * @param expressionGen expression generator.
   */
  @Inject
  public DiscreteBoundGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(expressionGen);
  }

  /**
   * Generates CSP-M for a discrete bound.
   *
   * <p> This generates one of multiple process calls, which should correspond to definitions in
   * the RoboCert support library.  The names of the processes, given a prefix 'x', are 'xLB' for
   * a lower bound; 'xUB' for an upper bound (emitted when the lower bound is zero); 'x' for
   * an exact bound, and 'xRange' for a ranged bound.
   *
   * @param bound bound to generate (must not be null, and at least one feature must be non-null).
   * @param prefix prefix to use for names of functions.
   * @return the CSP-M for the discrete bound.
   */
  public CharSequence generate(DiscreteBound bound, String prefix) {
    Objects.requireNonNull(bound, "discrete bound must be non-null");
    if (Strings.isNullOrEmpty(prefix))
      throw new IllegalArgumentException("prefix must not be null or empty");

    final var lb = bound.getLower();
    final var ub = bound.getUpper();

    // 2x2 matrix on whether we have each bound.
    if (lb == null) {
      // The default lower bound is `ub`, eg. we have an exact bound.
      Objects.requireNonNull(ub, "cannot have both bounds be null on discrete-bound %s".formatted(bound));
      return generateExactBound(ub, prefix);
    }
    return ub == null ? generateLowerBound(lb, prefix) : generateBothBounds(lb, ub, prefix);
  }

  private CharSequence generateLowerBound(Expression lb, String prefix) {
    return csp.function(prefix + LB_SUFFIX, expressionGen.generate(lb));
  }

  private CharSequence generateExactBound(Expression b, String prefix) {
    return csp.function(prefix + EXACT_SUFFIX, expressionGen.generate(b));
  }

  private CharSequence generateBothBounds(Expression lb, Expression ub, String prefix) {
    // if the lower bound is obviously zero, emit a simpler upper-bound-only version
    if (isZero(lb))
      return csp.function(prefix + UB_SUFFIX, expressionGen.generate(ub));

    return csp.function(prefix + BOTH_SUFFIX, expressionGen.generate(lb), expressionGen.generate(ub));
  }

  private boolean isZero(Expression expr) {
    if (expr instanceof IntegerExp i) {
      return i.getValue() == 0;
    }
    return false;
  }

  private static final String LB_SUFFIX = "LB";
  private static final String UB_SUFFIX = "UB";
  private static final String EXACT_SUFFIX = "";
  private static final String BOTH_SUFFIX = "Range";
}
