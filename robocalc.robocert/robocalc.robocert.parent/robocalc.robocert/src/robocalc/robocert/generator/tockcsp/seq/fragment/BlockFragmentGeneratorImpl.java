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
import java.util.Objects;
import robocalc.robocert.generator.intf.seq.ContextualGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.fragment.BlockFragmentGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.InteractionOperandGenerator;
import robocalc.robocert.generator.tockcsp.seq.fragment.until.UntilFragmentHeaderGenerator;
import robocalc.robocert.model.robocert.BlockFragment;
import robocalc.robocert.model.robocert.DurationFragment;
import robocalc.robocert.model.robocert.InteractionOperand;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.OptFragment;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Implementation of {@link BlockFragmentGenerator}.
 *
 * <p>There is a circular dependency between block fragment generators and themselves through
 * until-fragment generators.
 */
public record BlockFragmentGeneratorImpl
    (CSPStructureGenerator csp, ContextualGenerator<InteractionOperand> operandGen,
     DurationFragmentHeaderGenerator durationHeaderGen, LoopFragmentHeaderGenerator loopHeaderGen,
     UntilFragmentHeaderGenerator untilHeaderGen) implements BlockFragmentGenerator {

  @Inject
  public BlockFragmentGeneratorImpl {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(operandGen);
    Objects.requireNonNull(durationHeaderGen);
    Objects.requireNonNull(loopHeaderGen);
    Objects.requireNonNull(untilHeaderGen);
  }

  @Override
  public CharSequence generate(BlockFragment fragment, LifelineContext ctx) {
    // Special case: if we are generating inside a lifeline attached to one of multiple generated
    // actors, then we delegate UntilFragments to a separate process, and instead emit a
    // synchronisation with that process .ereh
    if (fragment instanceof UntilFragment u) {
      final var i = ctx.untilIndex(u);
      if (0 <= i) {
        return csp.function("UntilSync", ctx.global().untilChannel(), Integer.toString(i));
      }
    }

    return String.join("", generateHeader(fragment, ctx), generateBody(fragment, ctx));
  }

  private CharSequence generateHeader(BlockFragment fragment, LifelineContext ctx) {
    if (fragment instanceof DurationFragment d) {
      return durationHeaderGen.generate(d, ctx);
    }
    if (fragment instanceof LoopFragment l) {
      return loopHeaderGen.generate(l);
    }
    if (fragment instanceof OptFragment) {
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
