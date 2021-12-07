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

import robocalc.robocert.model.robocert.SequenceStep;

/**
 * Any generator for sequence steps.
 *
 * @author Matt Windsor
 */
public interface StepGenerator {
	/**
	 * Generates CSP-M for a sequence step in a particular lifeline.
	 *
	 * @param s   sequence step to generate.
	 * @param ctx context of the lifeline for which we are generating CSP-M.
	 *
	 * @return a code character sequence.
	 */
	public CharSequence generate(SequenceStep s, LifelineContext ctx);
}
