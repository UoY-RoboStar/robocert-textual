/*******************************************************************************
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
 ******************************************************************************/
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.ArrowAction;
import robocalc.robocert.model.robocert.SequenceAction;
import robocalc.robocert.model.robocert.FinalAction;
import robocalc.robocert.model.robocert.WaitAction;
import robocalc.robocert.generator.intf.seq.ActionGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;

/**
 * Top-level CSP generator for sequence actions.
 *
 * @author Matt Windsor
 */
public record ActionGeneratorImpl(CSPStructureGenerator csp,
																	ExpressionGenerator eg,
																	LoadStoreGenerator lsg,
																	MessageSpecGenerator msg) implements
		ActionGenerator {

	/**
	 * Constructs an action generator.
	 *
	 * @param csp a CSP structure generator, used for producing common CSP-M fragments.
	 * @param eg  an expression generator.
	 * @param lsg a load and store generator.
	 * @param msg a message spec generator, used for arrow actions.
	 */
	@Inject
	public ActionGeneratorImpl {
	}

	/**
	 * Generates CSP-M for an action.
	 *
	 * @param a the action action.
	 * @return the generated CSP.
	 */
	public CharSequence generate(SequenceAction a) {
		if (a instanceof ArrowAction r) {
			return generateArrow(r);
		}
		// TODO(@MattWindsor91): one day, possibly more than one type of final action.
		if (a instanceof FinalAction) {
			return "STOP";
		}
		if (a instanceof WaitAction w) {
			return generateWait(w);
		}
		throw new IllegalArgumentException("unsupported sequence action: %s".formatted(a));
	}

	private CharSequence generateArrow(ArrowAction r) {
		// TODO(@MattWindsor91): This should really be in the CSPStructureGenerator... somehow.
		return "%s -> %sSKIP".formatted(msg.generatePrefix(r.getBody()), lsg.generateBindingStores(r));
	}

	private CharSequence generateWait(WaitAction w) {
		// This is in the tock-CSP standard library.
		return csp.function("WAIT", eg.generate(w.getUnits()));
	}
}
