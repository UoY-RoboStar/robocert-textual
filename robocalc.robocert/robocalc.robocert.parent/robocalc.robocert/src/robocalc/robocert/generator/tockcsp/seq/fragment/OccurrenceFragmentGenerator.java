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

import robocalc.robocert.generator.intf.seq.OccurrenceGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageGenerator;
import robocalc.robocert.model.robocert.OccurrenceFragment;

/**
 * Generates CSP-M for action steps.
 *
 * @author Matt Windsor
 */
public record OccurrenceFragmentGenerator(CSPStructureGenerator csp,
																					OccurrenceGenerator occGen,
																					MessageSetGenerator msg,
																					MessageGenerator mpg) {
	// This generator handles the injection of loads for any possible
	// expressions in the action, as it is safe to do so at this level (no
	// Action recursively includes any more Steps or Actions).
	//
	// It does *not* handle the injection of stores; we do that in the
	// generator for MessageOccurrences.

	/**
	 * Constructs an action step generator.
	 *
	 * @param csp CSP structure generator.
	 * @param occGen occurrence generator.
	 * @param msg message set generator.
	 * @param mpg message spec generator.
	 */
	@Inject
	public OccurrenceFragmentGenerator {
	}

	/**
	 * Generates CSP-M for an occurrence fragment, from the perspective of a particular lifeline.
	 *
	 * @param fragment the occurrence fragment.
	 * @param ctx context for the current lifeline.
	 * @return the generated CSP-M.
	 */
	public CharSequence generate(OccurrenceFragment fragment, LifelineContext ctx) {
		final var body = occGen.generate(fragment.getOccurrence(), ctx);
		return switch (fragment.getTemperature()) {
			case COLD -> csp.function(COLD_PROC, body);
			case HOT -> body;
		};
	}

	/**
	 * Name of the process that implements cold temperature.
	 */
	private static final String COLD_PROC = "Cold"; // in robocert_seq_defs
}
