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

import robocalc.robocert.model.robocert.Subsequence;

/**
 * Interface for things that generate code for subsequences.
 * 
 * Subsequences induce cyclic dependencies, so the main purpose of this
 * generator is to help break up the dependency cycle.
 * 
 * @author Matt Windsor
 */
public interface SubsequenceGenerator {
	/**
	 * Generates CSP-M for a subsequence (inside a given lifeline).
	 * 
	 * @param s   the subsequence.
	 * @param ctx the lifeline context, used for deciding which actions are
	 *            relevant on this subsequence, and so on.
	 * 
	 * @return the generated code.
	 */
	public CharSequence generate(Subsequence s, LifelineContext ctx);
}