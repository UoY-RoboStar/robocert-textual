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
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DurationFragment;

/**
 * Generates CSP-M for the header part of {@link DurationFragment}s.
 *
 * @author Matt Windsor
 */
public record DurationFragmentHeaderGenerator(CSPStructureGenerator csp,
                                              DiscreteBoundGenerator boundGen) {

  /**
   * Name of the process family that implements the bounded duration header. (See
   * {@link DiscreteBoundGenerator} for information about the specific processes referenced.)
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
   * bounds become interleaved waits.  This only occurs on the lifeline affected by the deadline.
   *
   * @param frag duration fragment for which we are generating a header.
   * @param ctx  lifeline context, used to see if this is the correct lifeline for the duration to
   *             take effect.
   * @return the generated CSP-M.
   */
  public CharSequence generate(DurationFragment frag, LifelineContext ctx) {
    Objects.requireNonNull(frag);

    if (!ctx.isForLifeline(frag.getActor())) {
      return csp.commented("duration on %s".formatted(ctx.actor().getName()), "");
    }

    final var bound = frag.getBound();
    Objects.requireNonNull(bound, "duration fragments must have bounds");
    return boundGen.generate(bound, DURATION_PROC);
  }
}
