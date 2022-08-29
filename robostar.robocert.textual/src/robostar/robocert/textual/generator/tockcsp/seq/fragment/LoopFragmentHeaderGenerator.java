/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.LoopFragment;

/**
 * Generates CSP-M for the header part of {@link LoopFragment}s.
 * <p>
 * Most of this CSP is calls into either the CSP-M or RoboCert standard libraries.
 *
 * @author Matt Windsor
 */
public record LoopFragmentHeaderGenerator(
		DiscreteBoundGenerator boundGen) {

	/**
	 * Name of the process family that implements the bounded loop header.
	 * (See {@link DiscreteBoundGenerator} for information about the specific processes referenced.)
	 */
	private static final String BOUNDED_LOOP_PROC = "BoundedLoop"; // in robocert_seq_defs

	/**
	 * Constructs a CSP-M loop generator.
	 *
	 * @param boundGen a discrete bound generator.
	 */
	@Inject
	public LoopFragmentHeaderGenerator {
		Objects.requireNonNull(boundGen);
	}

	/**
	 * Generates CSP-M for the header of a loop fragment.
	 * <p>
	 * At the mathematical level, this becomes the 'loop' builtin for unbounded loops, and a variety
	 * of recursive contraptions for bounded loops.
	 *
	 * @param frag   loop fragment for which we are generating a header.
	 * @return the generated CSP.
	 */
	public CharSequence generate(LoopFragment frag) {
		Objects.requireNonNull(frag);
		final var bound = frag.getBound();
		// The absence of a bound implies an unbounded loop.
		return bound == null ? "loop" : boundGen.generate(bound, BOUNDED_LOOP_PROC);
	}
}
