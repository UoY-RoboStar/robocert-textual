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

import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DeadlineStep;

/**
 * Generates CSP-M for deadlines.
 *
 * @author Matt Windsor
 */
public record DeadlineFragmentGenerator(
		CSPStructureGenerator csp,
		ExpressionGenerator eg,
		SubsequenceGenerator sg) {

	/**
	 * Constructs a CSP-M deadline generator.
	 *
	 * @param csp a CSP structure generator.
	 * @param eg  an expression generator.
	 * @param sg  a subsequence generator.
	 */
	@Inject
	public DeadlineFragmentGenerator {
	}

	/**
	 * Generates CSP-M for a deadline step.
	 * <p>
	 * At the mathematical level, this becomes the 'deadline' tock-CSP operator.
	 *
	 * @param d   deadline step to generate.
	 * @param ctx context of the lifeline for which we are generating CSP-M.
	 * @return the generated CSP.
	 */
	public CharSequence generate(DeadlineStep d, LifelineContext ctx) {
		// TODO(@MattWindsor91): lower bounds?
		return csp.function(DEADLINE_PROC, body(d, ctx), units(d));
	}

	private CharSequence body(DeadlineStep d, LifelineContext ctx) {
		return csp.tuple(sg.generate(d.getBody(), ctx));
	}

	private String units(DeadlineStep d) {
		return "{- time units -} " + eg.generate(d.getUnits());
	}

	/**
	 * Name of the process that implements the tick-tock deadline operator.
	 */
	public static final String DEADLINE_PROC = "EndBy"; // in core_timed
}
