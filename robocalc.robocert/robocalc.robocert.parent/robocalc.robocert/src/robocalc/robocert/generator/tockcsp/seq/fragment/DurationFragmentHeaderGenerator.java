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

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DurationFragment;

/**
 * Generates CSP-M for the header part of {@link DeadlineStep}s.
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
		// TODO(@MattWindsor91): lower bounds?
		return csp.function(DURATION_UP_PROC, expressionGen.generate(d.getUpperBound()));
	}

	/**
	 * Name of the process that implements the upper-bound-only duration header.
	 */
	public static final String DURATION_UP_PROC = "DurationUp"; // in robocert_seq_defs
}
