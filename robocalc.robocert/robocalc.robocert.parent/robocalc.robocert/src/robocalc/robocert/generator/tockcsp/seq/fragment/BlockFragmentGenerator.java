/*******************************************************************************
 * Copyright (c) 2022 University of York and others
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

package robocalc.robocert.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.InteractionOperandGenerator;
import robocalc.robocert.model.robocert.BlockFragment;
import robocalc.robocert.model.robocert.DurationFragment;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.OptFragment;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Top-level generator for {@link BlockFragment}s.
 *
 * The CSP-M compilation strategy for block fragments is to lower them to the form F(P), where
 * P is the subsequence for the block and F is a function (possibly partially applied) representing
 * the 'header' of the fragment.
 */
public record BlockFragmentGenerator
    (CSPStructureGenerator csp,
     InteractionOperandGenerator operandGen,
     DurationFragmentHeaderGenerator durationHeaderGen,
     LoopFragmentHeaderGenerator loopHeaderGen,
     UntilFragmentHeaderGenerator untilHeaderGen
     )
{
  @Inject
  public BlockFragmentGenerator {}

  /**
   * Generates CSP-M for a block fragment.
   *
   * @param fragment the fragment to generate.
   * @param ctx the lifeline context for the current lifeline.
   * @return generated CSP-M for the block fragment.
   */
  public CharSequence generate(BlockFragment fragment, LifelineContext ctx) {
    return String.join("", generateHeader(fragment), generateBody(fragment, ctx));
  }

  private CharSequence generateHeader(BlockFragment fragment) {
    if (fragment instanceof DurationFragment d) {
      return durationHeaderGen.generate(d);
    }
    if (fragment instanceof LoopFragment l) {
      return loopHeaderGen.generate(l);
    }
    if (fragment instanceof OptFragment l) {
      return "Opt";
    }
    if (fragment instanceof UntilFragment u) {
      return untilHeaderGen.generate(u);
    }

    throw new IllegalArgumentException("unsupported block fragment: %s".formatted(fragment));
  }

  private CharSequence generateBody(BlockFragment fragment, LifelineContext ctx) {
    return csp.tuple(operandGen.generate(fragment.getBody(), ctx));
  }
}
