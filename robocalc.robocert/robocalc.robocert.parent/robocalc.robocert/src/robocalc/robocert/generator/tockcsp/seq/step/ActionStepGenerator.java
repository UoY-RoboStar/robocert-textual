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
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.MessageSetGenerator;
import robocalc.robocert.generator.tockcsp.seq.MessageSpecGenerator;
import robocalc.robocert.generator.utils.MessageSetOptimiser;
import robocalc.robocert.model.robocert.ActionStep;
import robocalc.robocert.model.robocert.ArrowAction;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.SequenceAction;

/**
 * Generates CSP-M for action steps.
 */
public class ActionStepGenerator {
	private CSPStructureGenerator csp;
	private ActionGenerator ag;
	private MessageSetOptimiser mso;
	private MessageSetGenerator msg;
	private MessageSpecGenerator mpg;

	// This generator handles the injection of loads for any possible
	// expressions in the action, as it is safe to do so at this level (no
	// Action recursively includes any more Steps or Actions).
	//
	// It does *not* handle the injection of stores; we do that in the
	// generator for ArrowActions.

	@Inject
	public ActionStepGenerator(CSPStructureGenerator csp, ActionGenerator ag, MessageSetOptimiser mso,
			MessageSetGenerator msg, MessageSpecGenerator mpg) {
		this.csp = csp;
		this.ag = ag;
		this.mso = mso;
		this.msg = msg;
		this.mpg = mpg;
	}

	/**
	 * Generates CSP-M for an action step.
	 *
	 * @param a the action step.
	 *
	 * @return the generated CSP-M.
	 */
	public CharSequence generateActionStep(ActionStep a) {
		return csp.function(gap(a), csp.function(EVENTUALLY_PROC, ag.generate(a.getAction())));
	}

	/**
	 * Generates CSP-M for an action step gap.
	 *
	 * Currently, we generate gaps for all action steps regardless of whether the
	 * gap is active. This part of the semantics is subject to change.
	 *
	 * @param a the action step.
	 *
	 * @return the generated CSP-M.
	 */
	private CharSequence gap(ActionStep a) {
		return csp.function(GAP_PROC, gapSet(a), actionSet(a.getAction()));
	}

	/**
	 * Optimises the gap set in place, then generates it.
	 *
	 * We do the optimisation like this to preserve containment information, so
	 * sequence group lookup works.
	 *
	 * @param a the action step.
	 *
	 * @return the generated CSP.
	 */
	private CharSequence gapSet(ActionStep a) {
		a.setGap(mso.optimise(a.getGap()));
		return msg.generate(a.getGap());
	}

	/**
	 * Generates the gap action set for an action step.
	 *
	 * This is to avoid the possibility of both the gap and the action accepting the
	 * same events.
	 *
	 * @param a the action for which we are generating CSP.
	 *
	 * @return the generated CSP sequence.
	 */
	private CharSequence actionSet(SequenceAction a) {
		return mpg.generateBulkCSPEventSet(messageSpecs(a).toList());
	}

	private Stream<MessageSpec> messageSpecs(SequenceAction s) {
		if (s instanceof ArrowAction a)
			return Stream.of(a.getBody());
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
