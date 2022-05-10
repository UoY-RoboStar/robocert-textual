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
import robocalc.robocert.generator.intf.seq.context.LifelineContext;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.DeadlineFragment;

/**
 * Generates CSP-M for the header part of {@link DeadlineFragment}s.
 *
 * @author Matt Windsor
 */
public record DeadlineFragmentHeaderGenerator(CSPStructureGenerator csp,
                                              ExpressionGenerator exprGen) {

  /**
   * Name of the process family that implements the deadline process.
   */
  private static final String DEADLINE_PROC = "DeadlineF"; // in robocert_seq_defs

  /**
   * Constructs a CSP-M deadline generator.
   *
   * @param exprGen an expression generator.
   */
  @Inject
  public DeadlineFragmentHeaderGenerator {
    Objects.requireNonNull(exprGen);
  }

  /**
   * Generates CSP-M for the header of a deadline fragment.
   * <p>
   * At the mathematical level, upper bounds becomes the 'deadline' tock-CSP operator.
   * This only occurs on the lifeline affected by the deadline.
   *
   * @param frag duration fragment for which we are generating a header.
   * @param ctx  lifeline context, used to see if this is the correct lifeline for the duration to
   *             take effect.
   * @return the generated CSP-M.
   */
  public CharSequence generate(DeadlineFragment frag, LifelineContext ctx) {
    Objects.requireNonNull(frag);

    if (!ctx.isFor(frag.getActor())) {
      return csp.commented("deadline on %s".formatted(ctx.actorName()), "");
    }

    return csp.function(DEADLINE_PROC, exprGen.generate(frag.getUnits()));
  }
}
