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
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.StepGenerator;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Subsequence;

/**
 * A generator that emits CSP for sequences and subsequences.
 *
 * @author Matt Windsor
 */
public record SubsequenceGeneratorImpl(
		CSPStructureGenerator csp,
		StepGenerator sg) implements SubsequenceGenerator {

	/**
	 * Constructs a subsequence generator.
	 *
	 * @param csp CSP structure generator used for the sequential composition.
	 * @param sg  step generator used for each step in the subsequence.
	 */
	@Inject
	public SubsequenceGeneratorImpl {
	}

	@Override
	public CharSequence generate(Subsequence s, LifelineContext ctx) {
		var steps = s.getSteps().parallelStream().map(x -> sg.generate(x, ctx))
				.toArray(CharSequence[]::new);
		return csp.seq(steps);
	}
}
