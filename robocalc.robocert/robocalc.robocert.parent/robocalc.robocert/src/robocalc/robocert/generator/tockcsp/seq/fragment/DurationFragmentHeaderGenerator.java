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
import java.util.Objects;
import robocalc.robocert.model.robocert.DurationFragment;

/**
 * Generates CSP-M for the header part of {@link DurationFragment}s.
 *
 * @author Matt Windsor
 */
public record DurationFragmentHeaderGenerator(
		DiscreteBoundGenerator boundGen) {

	/**
	 * Name of the process family that implements the bounded duration header.
	 * (See {@link DiscreteBoundGenerator} for information about the specific processes referenced.)
	 */
	private static final String DURATION_PROC = "Duration"; // in robocert_seq_defs

	/**
	 * Constructs a CSP-M deadline generator.
	 *
	 * @param boundGen a discrete bound generator.
	 */
	@Inject
	public DurationFragmentHeaderGenerator {
		Objects.requireNonNull(boundGen);
	}

	/**
	 * Generates CSP-M for the header of a deadline fragment.
	 * <p>
	 * At the mathematical level, upper bounds becomes the 'deadline' tock-CSP operator, and lower
	 * bounds become interleaved waits.
	 *
	 * @param frag   duration fragment for which we are generating a header.
	 * @return the generated CSP.
	 */
	public CharSequence generate(DurationFragment frag) {
		Objects.requireNonNull(frag);
		final var bound = frag.getBound();
		Objects.requireNonNull(bound, "duration fragments must have bounds");
		return boundGen.generate(bound, DURATION_PROC);
	}
}
