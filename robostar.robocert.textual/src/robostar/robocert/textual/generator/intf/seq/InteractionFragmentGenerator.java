/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.intf.seq;

import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.InteractionFragment;

/**
 * A top-level generator for interaction fragments.
 *
 * @author Matt Windsor
 */
public interface InteractionFragmentGenerator {
	/**
	 * Generates code for an interaction fragment (inside a given lifeline).
	 *
	 * @param f   fragment to generate.
	 * @param ctx context of the lifeline for which we are generating CSP-M.
	 *
	 * @return code for the sequence step.
	 */
	CharSequence generate(InteractionFragment f, LifelineContext ctx);
}
