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
package robocalc.robocert.generator.tockcsp.seq.step;

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.ActionGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSpecGenerator;
import robocalc.robocert.model.robocert.ArrowAction;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.OccurrenceFragment;
import robocalc.robocert.model.robocert.SequenceAction;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Generates CSP-M for action steps.
 *
 * @author Matt Windsor
 */
public record ActionStepGenerator(CSPStructureGenerator csp,
																	ActionGenerator ag,
																	MessageSetGenerator msg,
																	MessageSpecGenerator mpg) {
	// This generator handles the injection of loads for any possible
	// expressions in the action, as it is safe to do so at this level (no
	// Action recursively includes any more Steps or Actions).
	//
	// It does *not* handle the injection of stores; we do that in the
	// generator for ArrowActions.

	/**
	 * Constructs an action step generator.
	 *
	 * @param csp CSP structure generator.
	 * @param ag  action generator.
	 * @param msg message set generator.
	 * @param mpg message spec generator.
	 */
	@Inject
	public ActionStepGenerator {
	}

	/**
	 * Generates CSP-M for an action step, from the perspective of a particular lifeline.
	 *
	 * @param a the action step.
	 * @param ctx context for the current lifeline.
	 * @return the generated CSP-M.
	 */
	public CharSequence generate(OccurrenceFragment a, LifelineContext ctx) {
		// TODO(@MattWindsor91): separate until generation and occurrence generation
		
		// TODO(@MattWindsor91): temperature of action step?
		return csp.function(gap(a), csp.function(EVENTUALLY_PROC, ag.generate(a.getAction(), ctx)));
	}

	/**
	 * Generates CSP-M for an action step gap.
	 * <p>
	 * Currently, we generate gaps for all action steps regardless of whether the gap is active. This
	 * part of the semantics is subject to change.
	 *
	 * @param a the action step.
	 * @return the generated CSP-M.
	 */
	private CharSequence gap(OccurrenceFragment a) {
		return csp.function(GAP_PROC, gapSet(a), actionSet(a.getAction()));
	}

	/**
	 * Optimises the gap set in place, then generates it.
	 * <p>
	 * We do the optimisation like this to preserve containment information, so sequence group lookup
	 * works.
	 *
	 * @param a the action step.
	 * @return the generated CSP.
	 */
	private CharSequence gapSet(OccurrenceFragment a) {
		if (a instanceof UntilFragment u)
			return msg.optimiseAndGenerate(u.getIntraMessages(), u::setIntraMessages);
		return csp.set(); // for now
	}

	/**
	 * Generates the gap action set for an action step.
	 * <p>
	 * This is to avoid the possibility of both the gap and the action accepting the same events.
	 *
	 * @param a the action for which we are generating CSP.
	 * @return the generated CSP sequence.
	 */
	private CharSequence actionSet(SequenceAction a) {
		return mpg.generateBulkCSPEventSet(messageSpecs(a).toList());
	}

	private Stream<MessageSpec> messageSpecs(SequenceAction s) {
		if (s instanceof ArrowAction a) {
			return Stream.of(a.getBody());
		}
		return Stream.empty();
	}

	/**
	 * Name of the process that implements eventually-lifts.
	 */
	private static final String EVENTUALLY_PROC = "Cold"; // in robocert_seq_defs

	/**
	 * Name of the process that implements gaps.
	 */
	private static final String GAP_PROC = "Action"; // in robocert_seq_defs
}
