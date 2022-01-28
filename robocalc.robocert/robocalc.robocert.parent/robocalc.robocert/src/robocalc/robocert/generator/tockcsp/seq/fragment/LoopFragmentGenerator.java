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
import robocalc.robocert.model.robocert.DefiniteLoopBound;
import robocalc.robocert.model.robocert.InfiniteLoopBound;
import robocalc.robocert.model.robocert.LoopBound;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.LowerLoopBound;
import robocalc.robocert.model.robocert.RangeLoopBound;

/**
 * Generates CSP-M for loops.
 * <p>
 * Most of this CSP is calls into either the CSP-M or RoboCert standard libraries.
 *
 * @author Matt Windsor
 */
public record LoopFragmentGenerator(
		CSPStructureGenerator csp,
		ExpressionGenerator eg,
		SubsequenceGenerator sg) {

	/**
	 * Constructs a CSP-M loop generator.
	 *
	 * @param csp a CSP structure generator.
	 * @param eg  an expression generator.
	 * @param sg  a subsequence generator.
	 */
	@Inject
	public LoopFragmentGenerator {
	}

	/**
	 * Generates CSP for a loop action.
	 *
	 * @param l   the loop action to generate.
	 * @param ctx the context for the lifeline on which the step sits.
	 * @return the generated CSP.
	 */
	public CharSequence generate(LoopFragment l, LifelineContext ctx) {
		return csp.function(bound(l.getBound()), sg.generate(l.getBody(), ctx));
	}

	/**
	 * Expands to the appropriate stock process for a loop bound.
	 *
	 * @param b the loop bound.
	 * @return the parametric process to be instantiated with the process-to-loop to yield the
	 * appropriate loop.
	 */
	private CharSequence bound(LoopBound b) {
		if (b instanceof InfiniteLoopBound) {
			return "loop";
		}
		if (b instanceof LowerLoopBound l) {
			return csp.function("loop_at_least", eg.generate(l.getLowerTimes()));
		}
		if (b instanceof DefiniteLoopBound d) {
			return csp.function("loop_exactly", eg.generate(d.getTimes()));
		}
		if (b instanceof RangeLoopBound r) {
			return csp.function("loop_between", eg.generate(r.getLowerTimes()),
					eg.generate(r.getUpperTimes()));
		}
		throw new IllegalArgumentException("unsupported loop bound: %s".formatted(b));
	}
}
