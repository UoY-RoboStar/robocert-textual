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

import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DefiniteLoopBound;
import robocalc.robocert.model.robocert.InfiniteLoopBound;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.LowerLoopBound;
import robocalc.robocert.model.robocert.RangeLoopBound;

/**
 * Generates CSP-M for the header part of {@link LoopFragment}s.
 * <p>
 * Most of this CSP is calls into either the CSP-M or RoboCert standard libraries.
 *
 * @author Matt Windsor
 */
public record LoopFragmentHeaderGenerator(
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
	public LoopFragmentHeaderGenerator {
	}

	/**
	 * Generates CSP for a loop fragment header.
	 *
	 * @param fragment the loop fragment for which we are generating a header.
	 * @return the generated CSP.
	 */
	public CharSequence generate(LoopFragment fragment) {
		final var b = fragment.getBound();
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
