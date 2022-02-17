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
package robocalc.robocert.generator.intf.seq;

import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;

/**
 * A context used for a particular lifeline generation.
 *
 * @author Matt Windsor
 */
public record LifelineContext(Actor actor, long index) {
	/**
	 * Constructs a reference to this lifeline's CSP alphabet.
	 *
	 * This reference will only resolve within the sequence's let-within.
	 *
	 * @param csp a CSP-M structure generator.
	 * @return the resulting CSP.
	 */
	public CharSequence alphaCSP(CSPStructureGenerator csp) {
		return csp.function(ALPHA_FUNCTION, Long.toString(index));
	}

	/**
	 * Constructs a reference to this lifeline's CSP process.
	 *
	 * This reference will only resolve within the sequence's let-within.
	 *
	 * @param csp a CSP-M structure generator.
	 * @return the resulting CSP.
	 */
	public CharSequence procCSP(CSPStructureGenerator csp) {
		return csp.function(PROC_FUNCTION, Long.toString(index));
	}
	
	/**
	 * Name of the function used in alphaCSP.
	 */
	public static final String ALPHA_FUNCTION = "alpha";
	
	/**
	 * Name of the function used in procCSP.
	 */
	public static final String PROC_FUNCTION = "proc";
}
