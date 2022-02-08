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
import java.util.List;
import robocalc.robocert.generator.intf.seq.InteractionFragmentGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.InteractionFragment;

/**
 * A generator that emits CSP for sequences and subsequences.
 *
 * @author Matt Windsor
 */
public record SubsequenceGeneratorImpl(
		CSPStructureGenerator csp,
		InteractionFragmentGenerator fragGen) implements SubsequenceGenerator {
	// TODO(@MattWindsor91): it should be the *fragment* generator that has an interface, surely?

	/**
	 * Constructs a subsequence generator.
	 *
	 * @param csp CSP structure generator used for the sequential composition.
	 * @param fragGen generator used for each fragment in the subsequence.
	 */
	@Inject
	public SubsequenceGeneratorImpl {
	}

	@Override
	public CharSequence generate(List<InteractionFragment> s, LifelineContext ctx) {
		final var fragments = s.parallelStream().map(x -> fragGen.generate(x, ctx));

		// @MattWindsor91 2022-02-03: I've tried concatenating SKIP_ANYTIME here unconditionally, to
		// try to make it possible to have empty subsequences as duration and until fragment bodies and
		// get the right semantics, but placing it at either end of all subsequences interacts poorly
		// with durations nested into loops and other such fragments.
		return csp.seq(fragments.toArray(CharSequence[]::new));
	}
}
