/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.seq.fragment;

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;

import java.util.Objects;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DurationFragment;

/**
 * Generates CSP-M for the header part of {@link DurationFragment}s.
 *
 * @author Matt Windsor
 */
public record DurationFragmentHeaderGenerator(
		CSPStructureGenerator csp,
		ExpressionGenerator expressionGen) {

	/**
	 * Constructs a CSP-M deadline generator.
	 *
	 * @param csp a CSP structure generator.
	 * @param expressionGen an expression generator.
	 */
	@Inject
	public DurationFragmentHeaderGenerator {
	}

	/**
	 * Generates CSP-M for the header of a deadline fragment.
	 * <p>
	 * At the mathematical level, this becomes the 'deadline' tock-CSP operator.
	 *
	 * @param d   duration fragment for which we are generating a header.
	 * @return the generated CSP.
	 */
	public CharSequence generate(DurationFragment d) {
		Objects.requireNonNull(d);

		final var lb = d.getLowerBound();
		final var ub = d.getUpperBound();

		// 2x2 matrix on whether we have each bound.
		if (lb == null) {
			return ub == null ? "{- no bounds -}" : generateUpperBound(ub);
		}
		return ub == null ? generateLowerBound(lb) : generateBothBounds(lb, ub);
	}

	private CharSequence generateLowerBound(Expression lb) {
		Objects.requireNonNull(lb);

		return csp.function(DURATION_LB_PROC, expressionGen.generate(lb));
	}

	private CharSequence generateUpperBound(Expression ub) {
		Objects.requireNonNull(ub);

		return csp.function(DURATION_UB_PROC, expressionGen.generate(ub));
	}

	private CharSequence generateBothBounds(Expression lb, Expression ub) {
		Objects.requireNonNull(lb);
		Objects.requireNonNull(ub);

		return csp.function(DURATION_PROC, expressionGen.generate(lb), expressionGen.generate(ub));
	}


	/**
	 * Name of the process that implements the lower-bound-only duration header.
	 */
	public static final String DURATION_LB_PROC = "DurationLB"; // in robocert_seq_defs

	/**
	 * Name of the process that implements the upper-bound-only duration header.
	 */
	public static final String DURATION_UB_PROC = "DurationUB"; // in robocert_seq_defs

	/**
	 * Name of the process that implements the fully-bounded duration header.
	 */
	public static final String DURATION_PROC = "Duration"; // in robocert_seq_defs
}
